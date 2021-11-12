package per.redis.tool.support.handler;

import org.springframework.lang.Nullable;

import java.util.List;

public interface IListRedis {

    /**
     * 从右侧推入
     * @param key
     * @param value
     * @return 添加后集合长度
     */
    Long rightPush(String key,Object value);

    /**
     * 从右侧推入到指定值的后方，【right就是positionValue右边】
     * @param key
     * @param positionValue 位置的值
     * @param value 插入的值
     * @return 添加后的集合长度
     */
    Long rightPush(String key,Object positionValue,Object value);

    /**
     * 从右侧批量推入
     * @param key
     * @param values
     * @return 推入后集合长度
     */
    Long rightPushAll(String key, List<Object> values);

    /**
     * 如果存在key，从右侧推入
     * @param key
     * @param value
     * @return 添加后集合长度，若不存在key返回0
     */
    Long rightPushIfPresent(String key,Object value);

    /**
     * 从rightKey集合右侧弹出，从leftKey集合左侧推入
     * @param rightKey 右侧弹出集合key
     * @param leftKey 左侧推入集合key
     * @param clazz 弹出对象类
     * @param <T> 弹出对象类型
     * @return 转换后的实例对象，1.若clazz参数为null则返回值也为null 2.若集合无对象返回值也为null
     */
    @Nullable
    <T> T rightPopAndLeftPush(String rightKey,String leftKey,Class<T> clazz);

    /**
     * 从左侧推入
     * @param key key
     * @param value value
     * @return 返回集合长度（添加后）
     */
    Long leftPush(String key,Object value);

    /**
     * 从左侧推入到指定值的后方，【left就是positionValue左边】
     * @param key
     * @param positionValue 指定位置的值
     * @param value 插入的值
     * @return 插入后集合的长度
     */
    Long leftPush(String key,Object positionValue,Object value);

    /**
     * 从右侧弹出值  **复杂集合[List，Map]请勿使用该接口**
     * @param key
     * @param clazz 需要转换的对象类
     * @param <T> 对象类
     * @return 转换后实体对象,1.若clazz参数为null则返回值也为null 2.若集合无对象返回值也为null
     */
    @Nullable
    <T> T rightPop(String key,Class<T> clazz);

    /**
     * 从左侧弹出值   **复杂集合[List，Map]请勿使用该接口**
     * @param key 集合值
     * @param clazz 对象class
     * @param <T> 对象类型
     * @return 转换后实体对象,1.若clazz参数为null则返回值也为null 2.若集合无对象返回值也为null
     */
    @Nullable
    <T> T leftPop(String key,Class<T> clazz);

    /**
     * 如果存在key，从左推入
     * @param key key
     * @param value value
     * @return 如果存在返回集合长度（添加后），不存在返回0
     */
    Long leftPushIfPresent(String key,Object value);

    /**
     * 从指定集合的指定位置取出值，从0开始  **注意 取出的值不会被弹出**
     * @param key
     * @param start 起始坐标[包含起始坐标]
     * @param end 终止坐标[包含终止坐标]
     * @param clazz 转换类
     * @param <T> 转换类型
     * @return 指定类型集合
     */
    <T> List<T> range(String key,long start,long end,Class<T> clazz);


    /**
     * 裁剪集合,保留划定的区域
     * @param key
     * @param start 保留开始位置[包含开始位置]
     * @param end 保留结束位置[包含结束位置]
     */
    void trim(String key,long start,long end);

    /**
     * 获取集合长度
     * @param key
     * @return 集合长度
     */
    Long listLength(String key);

    /**
     * 获取指定位置的值  [获取的值不会被弹出]
     * @param key
     * @param index 集合的索引
     * @param clazz 转换类
     * @param <T> 转换类型
     * @return 实例对象， 1.若clazz为null，返回null 2.若无索引值，返回null
     */
    @Nullable
    <T> T listIndex(String key,long index,Class<T> clazz);

    /**
     * 从集合中移除元素
     * @param key
     * @param count 绝对值表示个数， 整数从左开始，负数从右开始，0代表移除所有
     * @param value 需要移除的元素
     * @return 被移除元素个数
     */
    Long listRemove(String key,int count,Object value);
}

