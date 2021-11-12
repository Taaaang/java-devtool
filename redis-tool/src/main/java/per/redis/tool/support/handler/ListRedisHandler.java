package per.redis.tool.support.handler;

import com.google.gson.Gson;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author:TangFenQi
 * @Date:2021/11/11 19:07
 **/
@Component
public class ListRedisHandler extends AbstractRedis implements IListRedis {
    public ListRedisHandler(RedisTemplate<String, String> redisTemplate, Gson gson) {
        super(redisTemplate, gson);
    }

    @Override
    public Long rightPush(String key,Object value) {
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);
        return redisTemplate.opsForList().rightPush(key,toJson(value));
    }

    @Override
    public Long rightPush(String key,Object positionValue,Object value) {
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);
        Assert.notNull(positionValue,"positionKey is empty!");
        return redisTemplate.opsForList().rightPush(key,toJson(positionValue),toJson(value));
    }

    @Override
    public Long rightPushAll(String key, List<Object> values){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notEmpty(values,REDIS_VALUE_EMPTY);
        return redisTemplate.opsForList().rightPushAll(key,values.stream().map(this::toJson).collect(Collectors.toList()));
    }

    @Override
    public Long rightPushIfPresent(String key,Object value){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);
       return redisTemplate.opsForList().rightPushIfPresent(key,toJson(value));
    }

    @Override
    public <T> T rightPopAndLeftPush(String rightKey,String leftKey,Class<T> clazz){
        Assert.hasText(rightKey,REDIS_KEY_EMPTY);
        Assert.hasText(leftKey,REDIS_KEY_EMPTY);
        String popObject = redisTemplate.opsForList().rightPopAndLeftPush(rightKey, leftKey);
        return toObject(popObject,clazz);
    }

    @Override
    public Long leftPush(String key,Object value){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);
        return redisTemplate.opsForList().leftPush(key,toJson(value));
    }

    @Override
    public Long leftPush(String key,Object positionValue,Object value){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);
        Assert.notNull(positionValue,"positionKey is empty!");
        return redisTemplate.opsForList().leftPush(key,toJson(positionValue),toJson(value));
    }

    @Override
    public <T> T rightPop(String key,Class<T> clazz){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        String popObject = redisTemplate.opsForList().rightPop(key);
        return toObject(popObject,clazz);
    }

    @Override
    public <T> T leftPop(String key,Class<T> clazz){
        Assert.hasText(key,REDIS_KEY_EMPTY);
         String json = redisTemplate.opsForList().leftPop(key);
         return toObject(json,clazz);
    }

    @Override
    public Long leftPushIfPresent(String key,Object value){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);
        return redisTemplate.opsForList().leftPushIfPresent(key, toJson(value));
    }

    @Override
    public <T> List<T> range(String key,long start,long end,Class<T> clazz){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.isTrue(start>=0,String.format("start less than 0! start=[%s]",start));
        Assert.isTrue(end>=0,String.format("end less than 0! end=[%s]",end));
        Assert.isTrue(start<=end,String.format("end less than start ! start=[%s] , end=[%s]",start,end));
        List<String> range = redisTemplate.opsForList().range(key, start, end);
        if(CollectionUtils.isEmpty(range)){
            return new ArrayList<>();
        }
        return range.stream().map((v)-> toObject(v,clazz)).collect(Collectors.toList());
    }

    @Override
    public void trim(String key,long start,long end){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.isTrue(start>=0,String.format("start less than 0! start=[%s]",start));
        Assert.isTrue(end>=0,String.format("end less than 0! end=[%s]",end));
        Assert.isTrue(start<=end,String.format("end less than start ! start=[%s] , end=[%s]",start,end));
        redisTemplate.opsForList().trim(key,start,end);
    }

    @Override
    public Long listLength(String key){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        return redisTemplate.opsForList().size(key);
    }

    @Override
    public <T> T listIndex(String key,long index,Class<T> clazz){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.isTrue(index>=0,String.format("index less than 0! index=[%s]",index));
        return toObject(redisTemplate.opsForList().index(key,index),clazz);
    }

    @Override
    public Long listRemove(String key,int count,Object value){
        Assert.hasText(key,REDIS_KEY_EMPTY);
        Assert.notNull(value,REDIS_VALUE_EMPTY);
        return redisTemplate.opsForList().remove(key,count,toJson(value));
    }
}
