package per.redis.tool.support;

import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public interface IRedisSupport {

    /**
     * redis 的 String值操作
     */

    /**
     * set value值
     * @param key key
     * @param value 值
     */
    void set(String key, Object value);

    /**
     * 获取值, **注意** 不可使用该接口接收List，Map复杂对象，可以使用该接口接收数组
     * @param key key
     * @param clazz 需要转换的对象类型
     * @param <T>
     * @return 对象实例
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 获取list和map这种复杂对象的方式
     * @param key key
     * @param typeToken 示例（复制后面代码括号中的代码）【new TypeToken<？>(){}】 在？号处填写自己需要转换的List<?>或者Map<?,?>
     * @param <T> 需要转换的类型，实例 List<？> 或者Map<?,?>
     * @return 返回实例对象 List<?>
     */
    <T> T getListOrMap(String key, TypeToken<T> typeToken);


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

    /**
     * 批量写入
     * @param map key:key值 value:value值
     */
    void mset(Map<String, Object> map);

    /**
     * 批量获取
     * @param keys key值集合
     * @return key:key值，value：json字符串，若未查询到会是null
     */
    Map<String,String> mgetToMap(String... keys);

    /**
     * 批量获取
     * @param keys key值集合
     * @return json结果集合
     */
    List<String> mget(String... keys);

    /**
     * 设置过期时间
     * @param key key
     * @param expiredTime 过期时间，单位：秒
     * @return true：成功 false：失败
     */
    boolean expire(String key, Long expiredTime);

    /**
     * 设置值和过期时间
     * @param key key
     * @param expireTime 过期时间，单位：秒
     * @param value 值
     */
    void setAndExpire(String key, Long expireTime, Object value);

    /**
     * 如果存在key，设置值
     * @param key key
     * @param value 值
     * @return true：存在key，设置了值， false：不存在key，未设置值
     */
    boolean setIfAbsent(String key, Object value);

    /**
     * 对key的值进行自增
     * @param key key值
     * @return 自增后的值
     */
    long increment(String key);

    /**
     * 对key的值加上number
     * @param key key值
     * @param number 需要加上的值
     * @return 加上后的值
     */
    long incrementByNumber(String key, int number);

    /**
     * 对key的值进行自减
     * @param key key值
     * @return 减少后的值
     */
    long decrement(String key);

    /**
     * 对key的值进行减少number
     * @param key key 值
     * @param number 进行减少的值
     * @return 减少后的值
     */
    long decrement(String key,int number);

}
