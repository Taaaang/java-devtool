package per.redis.tool.support.handler;

import com.google.gson.Gson;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author:TangFenQi
 * @Date:2021/11/13 10:21
 **/
@Component
public class SetRedisHandler extends AbstractRedis implements ISetRedis {

    public SetRedisHandler(RedisTemplate<String, String> redisTemplate, Gson gson) {
        super(redisTemplate, gson);
    }

    public Long setAdd(String key, Object... value) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notEmpty(value, REDIS_VALUE_EMPTY);
        return redisTemplate.opsForSet().add(key, toJson(value));
    }

    public <T> Set<T> setMembers(String key, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Set<String> members = redisTemplate.opsForSet().members(key);
        if (CollectionUtils.isEmpty(members)) {
            return new HashSet<>();
        }
        return members.stream().map(v -> toObject(v, clazz)).collect(Collectors.toSet());
    }

    public Boolean setExist(String key, Object value) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notNull(value, REDIS_VALUE_EMPTY);
        return redisTemplate.opsForSet().isMember(key, toJson(value));
    }

    public Long setSize(String key) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        return redisTemplate.opsForSet().size(key);
    }

    public <T> T setPop(String key, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        return toObject(redisTemplate.opsForSet().pop(key), clazz);
    }

    public <T> Set<T> setPop(String key, long count, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.isTrue(count > 0, String.format("count less than or equal 0! count=[%s]", count));
        return redisTemplate.opsForSet().pop(key, count).stream().map(v -> toObject(v, clazz)).collect(Collectors.toSet());
    }

    public Long setRemove(String key, Object... values) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notEmpty(values, REDIS_VALUE_EMPTY);
        return redisTemplate.opsForSet().remove(key, toJson(values));
    }

    public <T> T setRandomMember(String key, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notNull(clazz, REDIS_TRANSFORM_CLASS_EMPTY);
        return toObject(redisTemplate.opsForSet().randomMember(key), clazz);
    }

    public <T> Set<T> setRandomMember(String key, long count, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.isTrue(count > 0, String.format("count less than or equal 0! count=[%s]", count));
        Assert.notNull(clazz, REDIS_TRANSFORM_CLASS_EMPTY);
        return redisTemplate.opsForSet().randomMembers(key, count).stream().map(v -> toObject(v, clazz)).collect(Collectors.toSet());
    }

    public <T> Set<T> setDistinctRandomMember(String key, long count, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.isTrue(count > 0, String.format("count less than or equal 0! count=[%s]", count));
        Assert.notNull(clazz, REDIS_TRANSFORM_CLASS_EMPTY);
        return redisTemplate.opsForSet().distinctRandomMembers(key, count).stream().map(v -> toObject(v, clazz)).collect(Collectors.toSet());
    }

    public Boolean setMove(String key, Object value, String targetKey) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notNull(value, REDIS_VALUE_EMPTY);
        Assert.hasText(targetKey, "target key is empty!");
        return redisTemplate.opsForSet().move(key, toJson(value), targetKey);
    }

    public <T> Set<T> setDifferent(String key, String targetKey, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.hasText(targetKey, "targetKey is empty!");
        Assert.notNull(clazz, REDIS_TRANSFORM_CLASS_EMPTY);
        return redisTemplate.opsForSet().difference(key, targetKey).stream().map(v -> toObject(v, clazz)).collect(Collectors.toSet());

    }

    public <T> Set<T> setDifferent(String key, List<String> targetKey, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notEmpty(targetKey, "targeKey is empty!");
        Assert.notNull(clazz, REDIS_TRANSFORM_CLASS_EMPTY);
        return redisTemplate.opsForSet().difference(key, targetKey).stream().map(v -> toObject(v, clazz)).collect(Collectors.toSet());
    }

    public Long setDifferenceAndStore(String key, String targetKey, String movedKey) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.hasText(targetKey, "target key is empty!");
        Assert.hasText(movedKey, "move key is empty!");
        return redisTemplate.opsForSet().differenceAndStore(key, targetKey, movedKey);
    }

    public Long setDifferenceAndStore(String key, List<String> targetKey, String movedKey) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notEmpty(targetKey, "target key is empty!");
        Assert.hasText(movedKey, "move key is empty!");
        return redisTemplate.opsForSet().differenceAndStore(key, targetKey, movedKey);
    }

    public <T> Set<T> setIntersect(String key, String targetKey, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.hasText(targetKey, "target key is empty!");
        Assert.notNull(clazz, REDIS_TRANSFORM_CLASS_EMPTY);
        return redisTemplate.opsForSet().intersect(key, targetKey).stream().map(v -> toObject(v, clazz)).collect(Collectors.toSet());
    }

    public <T> Set<T> setIntersect(String key, List<String> targetKey, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notEmpty(targetKey, "target key is empty!");
        Assert.notNull(clazz, REDIS_TRANSFORM_CLASS_EMPTY);
        return redisTemplate.opsForSet().intersect(key, targetKey).stream().map(v -> toObject(v, clazz)).collect(Collectors.toSet());
    }


    public Long setIntersectAndStore(String key, String targetKey, String movedKey) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.hasText(targetKey, "target key is empty!");
        Assert.hasText(movedKey, "moved key is empty!");
        return redisTemplate.opsForSet().intersectAndStore(key, targetKey, movedKey);
    }

    public Long setIntersectAndStore(String key, List<String> targetKey, String movedKey) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notEmpty(targetKey, "target key is empty!");
        Assert.hasText(movedKey, "moved key is empty!");
        return redisTemplate.opsForSet().intersectAndStore(key, targetKey, movedKey);
    }

    public <T> Set<T> setUnion(String key, String targetKey, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.hasText(targetKey, "target key is empty!");
        Assert.notNull(clazz, REDIS_TRANSFORM_CLASS_EMPTY);
        return redisTemplate.opsForSet().union(key, targetKey).stream().map(v -> toObject(v, clazz)).collect(Collectors.toSet());
    }


    public <T> Set<T> setUnion(String key, List<String> targetKey, Class<T> clazz) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notEmpty(targetKey, "target key is empty!");
        Assert.notNull(clazz, REDIS_TRANSFORM_CLASS_EMPTY);
        return redisTemplate.opsForSet().union(key, targetKey).stream().map(v -> toObject(v, clazz)).collect(Collectors.toSet());
    }

    public Long setUnionAndStore(String key, List<String> targetKey,String movedKey) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.notEmpty(targetKey, "target key is empty!");
        Assert.hasText(movedKey, "moved key is empty!");
        return redisTemplate.opsForSet().unionAndStore(key, targetKey,movedKey);
    }

    public Long setUnionAndStore(String key, String targetKey,String movedKey) {
        Assert.hasText(key, REDIS_KEY_EMPTY);
        Assert.hasText(targetKey, "target key is empty!");
        Assert.hasText(movedKey, "moved key is empty!");
        return redisTemplate.opsForSet().unionAndStore(key, targetKey,movedKey);
    }

}
