package per.redis.tool.support.handler;

public interface IBasicRedis {

    /**
     * 删除多个或单个key
     * @param keys key值集合
     * @return 删除的条数
     */
    long del(String... keys);

    /**
     * 是否存在某个key
     * @param key key值
     * @return true：存在 false：不存在
     */
    boolean exists(String key);
}
