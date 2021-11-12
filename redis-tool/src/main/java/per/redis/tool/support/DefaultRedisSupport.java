package per.redis.tool.support;

import com.google.gson.reflect.TypeToken;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import per.redis.tool.support.handler.*;

import java.util.List;
import java.util.Map;

/**
 * redis所有操作的集合类
 * Basic操作类 {@link BasicRedisHandler}
 * String操作类{@link StringRedisHandler}
 * List操作类{@link ListRedisHandler}
 * @Author:TangFenQi
 * @Date:2021/11/11 10:08
 **/
@Component
public class DefaultRedisSupport implements IBasicRedis, IStringRedis, IListRedis {

    private RedisTemplate<String,String> redisTemplate;

    private IBasicRedis basicRedis;
    private IStringRedis stringRedis;
    private IListRedis listRedis;

    public DefaultRedisSupport(BasicRedisHandler basicRedis,
                               StringRedisHandler stringRedis,
                               ListRedisHandler listRedis) {
        this.basicRedis = basicRedis;
        this.stringRedis=stringRedis;
        this.listRedis=listRedis;
    }


    @Override
    public long del(String... keys) {
        return basicRedis.del(keys);
    }

    @Override
    public boolean exists(String key) {
        return basicRedis.exists(key);
    }

    /**
     * String 操作集合
     */

    @Override
    public void set(String key, Object value) {
        stringRedis.set(key,value);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return stringRedis.get(key,clazz);
    }

    @Override
    public <T> T getListOrMap(String key, TypeToken<T> typeToken) {
        return stringRedis.getListOrMap(key,typeToken);
    }

    @Override
    public void mset(Map<String, Object> map) {
        stringRedis.mset(map);
    }

    @Override
    public Map<String, String> mgetToMap(String... keys) {
        return stringRedis.mgetToMap(keys);
    }

    @Override
    public List<String> mget(String... keys) {
        return stringRedis.mget(keys);
    }

    @Override
    public boolean expire(String key, Long expiredTime) {
        return stringRedis.expire(key,expiredTime);
    }

    @Override
    public void setAndExpire(String key, Long expireTime, Object value) {
        stringRedis.setAndExpire(key,expireTime,value);
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        return stringRedis.setIfAbsent(key,value);
    }

    @Override
    public Long increment(String key) {
        return stringRedis.increment(key);
    }

    @Override
    public Long incrementByNumber(String key, int number) {
        return stringRedis.incrementByNumber(key,number);
    }

    @Override
    public Long decrement(String key) {
        return stringRedis.decrement(key);
    }

    @Override
    public Long decrementByNumber(String key, int number) {
        return stringRedis.decrementByNumber(key,number);
    }

    /**
     * List操作集合
     */

    @Override
    public Long rightPush(String key, Object value) {
        return listRedis.rightPush(key,value);
    }

    @Override
    public Long rightPush(String key, Object positionValue, Object value) {
        return listRedis.rightPush(key,positionValue,value);
    }

    @Override
    public Long rightPushAll(String key, List<Object> values) {
        return listRedis.rightPushAll(key,values);
    }

    @Override
    public Long rightPushIfPresent(String key, Object value) {
        return listRedis.rightPushIfPresent(key,value);
    }

    @Override
    public <T> T rightPopAndLeftPush(String rightKey, String leftKey, Class<T> clazz) {
        return listRedis.rightPopAndLeftPush(rightKey,leftKey,clazz);
    }

    @Override
    public Long leftPush(String key, Object value) {
        return listRedis.leftPush(key,value);
    }

    @Override
    public Long leftPush(String key, Object positionValue, Object value) {
        return listRedis.leftPush(key,positionValue,value);
    }

    @Override
    public <T> T rightPop(String key, Class<T> clazz) {
        return listRedis.rightPop(key,clazz);
    }

    @Override
    public <T> T leftPop(String key, Class<T> clazz) {
        return listRedis.leftPop(key,clazz);
    }

    @Override
    public Long leftPushIfPresent(String key, Object value) {
        return listRedis.leftPushIfPresent(key,value);
    }

    @Override
    public <T> List<T> range(String key, long start, long end, Class<T> clazz) {
        return listRedis.range(key,start,end,clazz);
    }

    @Override
    public void trim(String key, long start, long end) {
        listRedis.trim(key,start,end);
    }

    @Override
    public Long listLength(String key) {
        return listRedis.listLength(key);
    }

    @Override
    public <T> T listIndex(String key, long index, Class<T> clazz) {
        return listRedis.listIndex(key,index,clazz);
    }

    @Override
    public Long listRemove(String key, int count, Object value) {
        return listRedis.listRemove(key,count,value);
    }
}
