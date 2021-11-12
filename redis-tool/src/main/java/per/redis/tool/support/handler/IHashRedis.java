package per.redis.tool.support.handler;

import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

public interface IHashRedis {

    /**
     * 将值放入hash表
     * @param key
     * @param hashKey
     * @param value
     */
    void hashPut(String key,Object hashKey,Object value);

    /**
     * 如果存在key，hashKey 则放入值
     * @param key
     * @param hashKey
     * @param value
     * @return true：存在并且放入 false：不存在不进行操作
     */
    boolean hashPutIfAbsent(String key,Object hashKey,Object value);

    /**
     * 批量放入hash 表
     * @param key
     * @param hashKeyAndValue key：hashKey，value：value
     */
    void hashPutAll(String key, Map<Object,Object> hashKeyAndValue);

    /**
     * 从hash表中获取值， **注意，请勿使用该接口获取List和Map负责对象，请使用 {@link IHashRedis}的hashGetListOrMap()方法**
     * @param key
     * @param hashKey
     * @param clazz 对象类
     * @param <T> 对象类型
     * @return 实例化对象
     */
    <T> T hashGet(String key,Object hashKey,Class<T> clazz);

    /**
     * 从hash表中获取对象
     * @param key
     * @param hashKey
     * @param typeToken 类型Type
     * @param <T> 需转换的类型 支持List 和Map  示例： new TypeToken<List<Test>>(){}
     * @return 转换后集合对象
     */
    <T> T hashGetListOrMap(String key, Object hashKey, TypeToken<T> typeToken);

    /**
     * 批量获取对象 **注意，请勿使用该接口获取List和Map负责对象，请使用 {@link IHashRedis}的hashGetListOrMap()方法**
     * @param key
     * @param hashKeys
     * @param clazz 需要转换成的类
     * @param <T> 对象类型
     * @return 转换后集合, **注意，若hashKey未取出值会存入null值**
     */
    <T> List<T> hashMultiGet(String key, List<Object> hashKeys, Class<T> clazz);

    /**
     * 删除指定值
     * @param key
     * @param hashKeys
     * @return 已删除条数
     */
    Long hashDelete(String key,Object... hashKeys);

    /**
     * 查询key下有多少个hashKey
     * @param key
     * @return hashKey个数
     */
    Long hashSize(String key);

    /**
     * 获取key下所有的数据
     * @param key
     * @return key：hashKey value：value
     */
    Map<String, String> hashGet(String key);

    /**
     * 字段值自增
     * @param key
     * @param hashKey
     * @return 自增后数值
     */
    Long hashIncrement(String key,Object hashKey);

    /**
     * 字段值自减
     * @param key
     * @param hashKey
     * @return 自减后数值
     */
    Long hashDecrement(String key,Object hashKey);

    /**
     * 字段值增加
     * @param key
     * @param hashKey
     * @param number 增加的数值
     * @return 加完后的数值
     */
    Long hashIncrement(String key,Object hashKey,long number);

    /**
     * 字段值进行自减
     * @param key
     * @param hashKey
     * @param number 减少的数值
     * @return 减少后的数值
     */
    Long hashDecrement(String key,Object hashKey,long number);
}
