package per.redis.tool.support.handler;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface ISetRedis {

    /**
     * 向set集合中放入元素
     * @param key
     * @param value
     * @return 添加的元素个数
     */
    Long setAdd(String key,Object... value);

    /**
     * 取出key中的所有元素
     * @param key
     * @param clazz 对象类
     * @param <T> 对象类型
     * @return 实例集合，若不存在则会返回空集合而不是null
     */
    <T> Set<T> setMembers(String key, Class<T> clazz);

    /**
     * 判断集合中是否包含该值
     * @param key
     * @param value
     * @return true：包含  false：不包含
     */
    Boolean setExist(String key,Object value);

    /**
     * 查看集合包含的元素数量
     * @param key
     * @return 元素数量
     */
    Long setSize(String key);

    /**
     * 随机弹出一个元素
     * @param key
     * @param clazz
     * @param <T>
     * @return 实例对象，若存在元素则返回null
     */
    @Nullable
    <T> T setPop(String key,Class<T> clazz);

    /**
     * 随机弹出指定数量的元素，**redis版本过低会抛出错误number数量的异常**
     * @param key
     * @param count
     * @param clazz
     * @param <T>
     * @return
     */
    <T> Set<T> setPop(String key, long count, Class<T> clazz);


    /**
     * 从集合中移除指定元素
     * @param key
     * @param values
     * @return 移除条数，若未找到value返回0
     */
    Long setRemove(String key,Object...values);

    /**
     * 随机从集合中获取元素，不移除元素
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T setRandomMember(String key,Class<T> clazz);

    /**
     * 随机从集合中获取指定数量元素，不移除元素 **注意：该方法会出现重复元素，set集合size<=count，若不希望出现重复元素请使用setDistinctRandomMember()**
     * @param key
     * @param count 随机元素
     * @param clazz
     * @param <T>
     * @return 元素集合
     */
    <T> Set<T>  setRandomMember(String key,long count,Class<T> clazz);

    /**
     * 随机从集合中获取指定数量的不重复元素，若count大于整个集合，则返回集合大小
     * @param key
     * @param count
     * @param clazz
     * @param <T>
     * @return 元素集合
     */
    <T> Set<T>  setDistinctRandomMember(String key,long count,Class<T> clazz);

    /**
     * 将集合A中指定元素移动到集合B中
     * @param key
     * @param value
     * @param targetKey 移动到的key
     * @return true：移动成功 false：移动失败
     */
    Boolean setMove(String key,Object value,String targetKey);

    /**
     * key集合对于targetKey中独有的元素
     * @param key 进行比较的基准key
     * @param targetKey 比较的key
     * @param clazz
     * @param <T>
     * @return 集合
     */
    <T> Set<T> setDifferent(String key,String targetKey,Class<T> clazz);

    /**
     * 集合中第一个对于后面的独有的元素，参考上面的方法，将targetKey替换为集合
     * @param key 进行比较的基准key
     * @param targetKey 比较的key
     * @param clazz
     * @param <T>
     * @return 集合
     */
    <T> Set<T> setDifferent(String key,List<String> targetKey, Class<T> clazz);

    /**
     * 将key中独有元素移动的movedKey中 **注意，只是元素拷贝，原来key中的元素保留**
     * @param key 进行比较的基准key
     * @param targetKey 比较的key
     * @param movedKey 需要将结果移动到的key
     * @return 移动的元素数量
     */
    Long setDifferenceAndStore(String key,String targetKey,String movedKey);

    /**
     * 将key中独有的元素移动到movedKey中 **注意，只是元素拷贝，原来key中的元素保留**
     * @param key 进行比较的基准key
     * @param targetKey 比较的key
     * @param movedKey 需要将结果移动到的key
     * @return 移动的元素数量
     */
    Long setDifferenceAndStore(String key,List<String> targetKey,String movedKey);

    /**
     * 获取两个集合的交集
     * @param key 进行比较的key
     * @param targetKey 进行比较的key
     * @param clazz
     * @param <T>
     * @return 交集中的对象集合
     */
    <T> Set<T> setIntersect(String key,String targetKey,Class<T> clazz);

    /**
     * 获取集合交集中的对象
     * @param key
     * @param targetKey
     * @param clazz
     * @param <T>
     * @return 交集中的对象
     */
    <T> Set<T> setIntersect(String key,List<String> targetKey,Class<T> clazz);

    /**
     * 获取集合的交集，并移动到movedKey中 **注意，只是元素拷贝，原来key中的元素保留**
     * @param key 进行比较的基准key
     * @param targetKey 比较的key
     * @param movedKey 需要将结果移动到的key
     * @return 移动的元素个数
     */
    Long setIntersectAndStore(String key,String targetKey,String movedKey);

    /**
     * 获取集合的交集，并移动到movedKey中 **注意，只是元素拷贝，原来key中的元素保留**
     * @param key 进行比较的基准key
     * @param targetKey 比较的key
     * @param movedKey 需要将结果移动到的key
     * @return 移动的元素个数
     */
    Long setIntersectAndStore(String key,List<String> targetKey,String movedKey);

    /**
     * 获取集合的并集
     * @param key 进行比较的基准key
     * @param targetKey 比较的key
     * @param clazz
     * @param <T>
     * @return 并集对象
     */
    <T> Set<T> setUnion(String key, String targetKey, Class<T> clazz);

    /**
     * 获取集合的并集
     * @param key 进行比较的基准key
     * @param targetKey 比较的key
     * @param clazz
     * @param <T>
     * @return 并集对象
     */
    <T> Set<T> setUnion(String key, List<String> targetKey, Class<T> clazz);

    /**
     * 获取集合的并集 并移动到movedKey中  **注意，只是元素拷贝，原来key中的元素保留**
     * @param key 进行比较的基准key
     * @param targetKey 比较的key
     * @param movedKey 并集元素移动到的key
     * @return 并集元素移动的个数
     */
    Long setUnionAndStore(String key, List<String> targetKey,String movedKey);

    /**
     * 获取集合的并集 并移动到movedKey中  **注意，只是元素拷贝，原来key中的元素保留**
     * @param key 进行比较的基准key
     * @param targetKey 比较的key
     * @param movedKey 并集元素移动到的key
     * @return 并集元素移动的个数
     */
    Long setUnionAndStore(String key, String targetKey,String movedKey);
}
