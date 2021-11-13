package per.redis.tool.support.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @Author:TangFenQi
 * @Date:2021/11/11 18:44
 **/
public abstract class AbstractRedis {

    protected RedisTemplate<String,String> redisTemplate;
    protected Gson gson;

    protected static final String GET_NOTICE_LIST_OR_MAP="pls don't use this method, just look api note! pls use ListOrMap() method.";
    protected static final String REDIS_KEY_EMPTY="redis key is empty!";
    protected static final String REDIS_VALUE_EMPTY="redis value is empty!";
    protected static final String REDIS_HASH_KEY_EMPTY="redis hash key is empty!";
    protected static final String REDIS_TRANSFORM_CLASS_EMPTY="transform class is empty!";

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

    protected String[] toJson(Object[] values){
        if(values==null||values.length==0){
            return new String[0];
        }
        String[] strings=new String[values.length];
        for (int i = 0; i < values.length; i++) {
            strings[i]=toJson(values[i]);
        }
        return strings;
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

    protected <T> T toObject(String json, TypeToken<T> typeToken){
        if(json==null){
            return null;
        }
        if(typeToken==null){
            return null;
        }
        return gson.fromJson(json,typeToken.getType());
    }
}
