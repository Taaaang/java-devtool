package per.redis.tool.support.handler;

import com.google.gson.Gson;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author:TangFenQi
 * @Date:2021/11/11 18:44
 **/
public abstract class AbstractRedis {

    protected RedisTemplate<String,String> redisTemplate;
    protected Gson gson;

    protected static final String GET_NOTICE_LIST_OR_MAP="pls don't use this method! pls use getListOrMap() method.";
    protected static final String REDIS_KEY_EMPTY="redis key is empty!";
    protected static final String REDIS_VALUE_EMPTY="redis value is empty!";

    public AbstractRedis(RedisTemplate<String, String> redisTemplate, Gson gson) {
        this.redisTemplate = redisTemplate;
        this.gson=gson;
    }


    protected String toJson(Object value){
        if(value.getClass().isAssignableFrom(String.class) ) {
            return (String) value;
        }else {
            return gson.toJson(value);
        }
    }

    protected <T> T toObject(String json,Class<T> clazz){
        if(json==null){
            return null;
        }
        if(clazz==null){
            return null;
        }
        return gson.fromJson(json,clazz);
    }
}
