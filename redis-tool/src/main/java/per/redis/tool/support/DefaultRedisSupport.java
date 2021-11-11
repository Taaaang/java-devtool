package per.redis.tool.support;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author:TangFenQi
 * @Date:2021/11/11 10:08
 **/
@Component
public class DefaultRedisSupport implements IRedisSupport {

    private RedisTemplate<String,String> redisTemplate;

    private static final String GET_NOTICE_LIST_OR_MAP="pls don't use this method! pls use getListOrMap() method.";
    private static final String REDIS_KEY_EMPTY="redis key is empty!";
    private static final String REDIS_VALUE_EMPTY="redis value is empty!";
    @Autowired
    private Gson gson;

    public DefaultRedisSupport(RedisTemplate<String,String> redisTemplate) {
        this.redisTemplate=redisTemplate;
    }


    private String toJson(Object value){
        if(value.getClass().isAssignableFrom(String.class) ) {
            return (String) value;
        }else {
            return gson.toJson(value);
        }
    }

    @Override
    public void set(String key, Object value) {
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);

        redisTemplate.opsForValue().set(key,toJson(value));
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Assert.notNull(key,REDIS_KEY_EMPTY);
        Assert.isTrue(!List.class.isAssignableFrom(clazz),GET_NOTICE_LIST_OR_MAP);
        Assert.isTrue(!Map.class.isAssignableFrom(clazz),GET_NOTICE_LIST_OR_MAP);
        return gson.fromJson(redisTemplate.opsForValue().get(key),clazz);
    }

    @Override
    public <T> T getListOrMap(String key, TypeToken<T> typeToken) {
        if(typeToken.getRawType().isAssignableFrom(List.class)||typeToken.getRawType().isAssignableFrom(Map.class)){
            return gson.fromJson(redisTemplate.opsForValue().get(key), typeToken.getType());
        }
        throw new IllegalArgumentException("just List or Map typeToken!!pls look the note.");
    }


    @Override
    public long del(String... key) {
        Assert.notEmpty(key,REDIS_KEY_EMPTY);
        return redisTemplate.delete(CollectionUtils.arrayToList(key));
    }

    @Override
    public boolean exists(String key) {
        Assert.hasText(key,REDIS_KEY_EMPTY);
        return redisTemplate.hasKey(key);
    }

    @Override
    public void mset(Map<String, Object> map) {
        Assert.notEmpty(map,REDIS_VALUE_EMPTY);

        Map<String,String> stringMap=new HashMap<>(map.size());
        map.entrySet().stream().forEach(v->{
            stringMap.put(v.getKey(),toJson(v.getValue()));
        });
        redisTemplate.opsForValue().multiSet(stringMap);
    }

    @Override
    public Map<String, String> mgetToMap(String... keys) {
        Assert.notEmpty(keys,REDIS_KEY_EMPTY);
        Map<String,String> map=new HashMap<>();
        List<String> list = redisTemplate.opsForValue().multiGet(CollectionUtils.arrayToList(keys));
        for (int i = 0; i < list.size(); i++) {
            map.put(keys[i],list.get(i));
        }
        return map;
    }

    @Override
    public List<String> mget(String... keys) {
        Assert.notEmpty(keys,REDIS_KEY_EMPTY);
        return (List<String>)redisTemplate.opsForValue().multiGet(CollectionUtils.arrayToList(keys));
    }

    @Override
    public boolean expire(String key, Long expiredTime) {
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(expiredTime,"expiredTime is null!");
        Assert.isTrue(expiredTime>0,String.format("expired time less than or equal 0 ! expiredTime=[%s]",expiredTime));
        return redisTemplate.expire(key,expiredTime, TimeUnit.SECONDS);
    }

    @Override
    public void setAndExpire(String key, Long expiredTime, Object value) {
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(expiredTime,"expiredTime is empty");
        Assert.notNull(value,REDIS_VALUE_EMPTY);
        Assert.isTrue(expiredTime>0,String.format("expiredTime less than or equal 0! expiredTime=[%s]",expiredTime));
        redisTemplate.opsForValue().set(key,toJson(value),expiredTime,TimeUnit.SECONDS);
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);
        return redisTemplate.opsForValue().setIfAbsent(key,toJson(value));
    }

    @Override
    public long increment(String key) {
        Assert.notNull(key,REDIS_KEY_EMPTY);
        return redisTemplate.opsForValue().increment(key);
    }

    @Override
    public long incrementByNumber(String key, int number) {
        Assert.notNull(key,REDIS_KEY_EMPTY);
        Assert.isTrue(number>0,String.format("number less than or equal 0 ! number=[%s]",number));
        return redisTemplate.opsForValue().increment(key,number);
    }

    @Override
    public long decrement(String key) {
        Assert.hasText(key,REDIS_KEY_EMPTY);
        return redisTemplate.opsForValue().decrement(key);
    }

    @Override
    public long decrement(String key, int number) {
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.isTrue(number>0,String.format("number less than or equal 0 ! number=[%s]",number));
        return redisTemplate.opsForValue().decrement(key,number);
    }
}
