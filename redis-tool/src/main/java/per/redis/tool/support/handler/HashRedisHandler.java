package per.redis.tool.support.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author:TangFenQi
 * @Date:2021/11/12 17:25
 **/
@Component
public class HashRedisHandler extends AbstractRedis implements IHashRedis {

    private HashOperations<String,String,String> hashOperations;

    public HashRedisHandler(RedisTemplate<String, String> redisTemplate, Gson gson) {
        super(redisTemplate, gson);
        this.hashOperations=redisTemplate.opsForHash();
    }
    @Override
    public void hashPut(String key,Object hashKey,Object value){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(hashKey,REDIS_HASH_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);
        hashOperations.put(key,toJson(hashKey),toJson(value));
    }
    @Override
    public boolean hashPutIfAbsent(String key,Object hashKey,Object value){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(hashKey,REDIS_HASH_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);
       return hashOperations.putIfAbsent(key, toJson(hashKey), toJson(value));
    }
    @Override
    public void hashPutAll(String key, Map<Object,Object> hashKeyAndValue){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notEmpty(hashKeyAndValue,REDIS_VALUE_EMPTY);
        Map<String, String> newMap = hashKeyAndValue.entrySet().stream().collect(Collectors.toMap(e->toJson(e.getKey()), e -> toJson(e.getValue())));
        hashOperations.putAll(key,newMap);
    }
    @Override
    public <T> T hashGet(String key,Object hashKey,Class<T> clazz){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(hashKey,REDIS_HASH_KEY_EMPTY);
        Assert.notNull(clazz,REDIS_TRANSFORM_CLASS_EMPTY);
        Assert.isTrue(!List.class.isAssignableFrom(clazz),GET_NOTICE_LIST_OR_MAP);
        Assert.isTrue(!Map.class.isAssignableFrom(clazz),GET_NOTICE_LIST_OR_MAP);
        return toObject(hashOperations.get(key, toJson(hashKey)),clazz);
    }
    @Override
    public <T> T hashGetListOrMap(String key, Object hashKey, TypeToken<T> typeToken){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(hashKey,REDIS_HASH_KEY_EMPTY);
        Assert.notNull(typeToken,REDIS_TRANSFORM_CLASS_EMPTY);
        return toObject(hashOperations.get(key, toJson(hashKey)),typeToken);
    }
    @Override
    public <T> List<T> hashMultiGet(String key, List<Object> hashKeys,Class<T> clazz){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notEmpty(hashKeys,"hash keys are empty!");
        Assert.notNull(clazz,REDIS_TRANSFORM_CLASS_EMPTY);
        List<String> list = hashOperations.multiGet(key, hashKeys.stream().map(this::toJson).collect(Collectors.toList()));
        if(CollectionUtils.isEmpty(list)){
            return new ArrayList<>();
        }
        return list.stream().map(v->toObject(v,clazz)).collect(Collectors.toList());
    }
    @Override
    public Long hashDelete(String key,Object... hashKeys){
        Assert.notNull(key,REDIS_KEY_EMPTY);
        Assert.notEmpty(hashKeys,REDIS_HASH_KEY_EMPTY);
        return hashOperations.delete(key,toJson(hashKeys));
    }
    @Override
    public Long hashSize(String key){
        Assert.hasText(key,REDIS_HASH_KEY_EMPTY);
        return hashOperations.size(key);
    }
    @Override
    public Map<String, String> hashGet(String key){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        return hashOperations.entries(key);
    }
    @Override
    public Long hashIncrement(String key,Object hashKey){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(hashKey,REDIS_HASH_KEY_EMPTY);
        return hashOperations.increment(key,toJson(hashKey),1);
    }
    @Override
    public Long hashDecrement(String key,Object hashKey){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(hashKey,REDIS_HASH_KEY_EMPTY);
        return hashOperations.increment(key,toJson(hashKey),-1);
    }
    @Override
    public Long hashIncrement(String key,Object hashKey,long number){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(hashKey,REDIS_HASH_KEY_EMPTY);
        Assert.isTrue(number>0,String.format("number less than or equals 0! number=[%s]",number));
        return hashOperations.increment(key,toJson(hashKey),number);
    }
    @Override
    public Long hashDecrement(String key,Object hashKey,long number){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(hashKey,REDIS_HASH_KEY_EMPTY);
        Assert.isTrue(number>0,String.format("number less than or equals 0! number=[%s]",number));
        return hashOperations.increment(key,toJson(hashKey),number*-1);
    }

}
