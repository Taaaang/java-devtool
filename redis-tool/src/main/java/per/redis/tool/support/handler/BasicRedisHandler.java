package per.redis.tool.support.handler;

import com.google.gson.Gson;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * redis基础操作类
 * @Author:TangFenQi
 * @Date:2021/11/11 18:41
 **/
@Component
public class BasicRedisHandler extends AbstractRedis implements IBasicRedis {

    public BasicRedisHandler(RedisTemplate<String, String> redisTemplate, Gson gson) {
        super(redisTemplate,gson);
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
}
