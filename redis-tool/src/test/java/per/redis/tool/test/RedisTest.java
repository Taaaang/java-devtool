package per.redis.tool.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import per.redis.tool.RedisToolBootstrap;
import per.redis.tool.support.DefaultRedisSupport;
import per.redis.tool.support.handler.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author:TangFenQi
 * @Date:2021/11/11 10:34
 **/
@SpringBootTest(classes = {RedisToolBootstrap.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class RedisTest {

    @Autowired
    private DefaultRedisSupport redisSupport;

    @Autowired
    private RedisTemplate<String, String> template;

    @Autowired
    @Qualifier("basicRedisHandler")
    private IBasicRedis basicRedis;

    @Autowired
    private IListRedis listRedisHandler;

    @Autowired
    private IHashRedis hashRedis;

    @Autowired
    private ISetRedis setRedis;

    @Autowired
    private Gson gson;

    @Test
    public void testOpsValueSetAndGet() {
        redisSupport.set("test", 1);
        TestInfo testInfo = new TestInfo("abc", 10);
        redisSupport.set("test1", testInfo);
        redisSupport.set("test2", "test");
        List<TestInfo> testInfoList = new ArrayList<>();
        testInfoList.add(new TestInfo("aaa", 2));
        testInfoList.add(new TestInfo("bbb", 3));
        redisSupport.set("test3", testInfoList);
        TestInfo[] testInfos = new TestInfo[2];
        testInfos[0] = new TestInfo("a", 41);
        testInfos[1] = new TestInfo("ad", 445);
        redisSupport.set("test4", testInfos);

        int test = redisSupport.get("test", int.class);
        TestInfo testInfo1 = redisSupport.get("test1", TestInfo.class);
        String test2 = redisSupport.get("test2", String.class);
        List<TestInfo> test3 = redisSupport.getListOrMap("test3", new TypeToken<List<TestInfo>>() {
        });
        TestInfo[] test4 = redisSupport.get("test4", TestInfo[].class);

        System.out.println(test);
        System.out.println(gson.toJson(testInfo1));
        System.out.println(test2);
        System.out.println(gson.toJson(test3));
        System.out.println(test3.get(0).getName());
        System.out.println(test4[0].getName());
    }

    @Test
    public void testOpsValueMultiSet() {

        Map<String, Object> map = new HashMap<>();
        map.put("multi1", "abc");
        map.put("multi2", 3);
        map.put("multi3", new TestInfo("multi3", 3));
        Map<String, TestInfo> maps = new HashMap<>();
        maps.put("key1", new TestInfo("map1", 1));
        maps.put("key2", new TestInfo("map2", 2));
        map.put("multi4", maps);
        redisSupport.mset(map);

        String multi1 = redisSupport.get("multi1", String.class);
        int multi2 = redisSupport.get("multi2", int.class);
        TestInfo multi3 = redisSupport.get("multi3", TestInfo.class);
        Map<String, TestInfo> multi4 = redisSupport.getListOrMap("multi4", new TypeToken<Map<String, TestInfo>>() {
        });

        Assert.assertEquals(map.get("multi1"), multi1);
        Assert.assertEquals(map.get("multi2"), multi2);
        Assert.assertEquals("multi3", multi3.getName());
        Assert.assertEquals("map1", multi4.get("key1").getName());
        Assert.assertEquals("map2", multi4.get("key2").getName());

    }

    @Test
    public void testOpsValueMultiGet() {
        testOpsValueMultiSet();
        Map<String, String> mget = redisSupport.mgetToMap("multi1", "multi2", "multi5", "multi4");
        System.out.println(mget.get("multi5"));
        List<String> mget1 = redisSupport.mget("multi1", "multi2", "multi5", "multi4");
        System.out.println(mget1.get(2));
        System.out.println(mget1.get(3));
    }

    @Test
    public void testOpsValueInDecrement() {
        redisSupport.set("number", 1);
        long result1 = redisSupport.increment("number");
        Assert.assertEquals(2, (int) redisSupport.get("number", int.class));
        long result = redisSupport.incrementByNumber("number", 2);
        Assert.assertEquals(4, (int) redisSupport.get("number", int.class));
        Assert.assertEquals(result1, 2);
        Assert.assertEquals(result, 4);

        redisSupport.set("number", 3);
        long number = redisSupport.decrement("number");
        Assert.assertEquals(number, 2);
        long number1 = redisSupport.decrementByNumber("number", 2);
        Assert.assertEquals(number1, 0);

    }

    @Test
    public void testOpsListPopAndPush() {
        String key = "list";
        basicRedis.del(key);
        Long number = listRedisHandler.rightPush(key, "right1");
        Assert.assertEquals(1, number.intValue());
        number = listRedisHandler.leftPush(key, "left1");
        Assert.assertEquals(2, number.intValue());
        List<Object> values = new ArrayList<>(3);
        values.add(1);
        values.add("right2");
        values.add(new TestInfo("right3", 2));
        number = listRedisHandler.rightPushAll(key, values);
        Assert.assertEquals(values.size() + 2, number.intValue());
        //left1,right1,1,right2,[right3,2]
        //[right3,2],left1,right1,1,right2
        TestInfo list = listRedisHandler.rightPopAndLeftPush(key, "list", TestInfo.class);
        Assert.assertEquals(list.getName(), "right3");
        String right2 = listRedisHandler.rightPop(key, String.class);
        Assert.assertEquals(right2, "right2");
        Integer one = listRedisHandler.rightPop(key, int.class);
        Assert.assertEquals(1, one.intValue());
    }

    @Test
    public void testOpsListPop() {
        String key = "popEmpty";
        String rightKey = "rightKey";
        String leftKey = "leftKey";
        basicRedis.del(key, rightKey, leftKey);
        Long number = listRedisHandler.rightPush(key, "abc");
        number = listRedisHandler.rightPop(key, null);
        number = listRedisHandler.rightPop(key, null);


        listRedisHandler.rightPush(leftKey, "right");
        String s = listRedisHandler.rightPopAndLeftPush(rightKey, leftKey, String.class);
        System.out.println();
    }

    @Test
    public void testOpsListRightPushPosition() {
        String key = "rightPushPosition";
        basicRedis.del(key);
        listRedisHandler.rightPush(key, new TestInfo("right3", 1));
        listRedisHandler.rightPush(key, new TestInfo("right2", 1));
        listRedisHandler.rightPush(key, new TestInfo("right1", 1));
        Long aLong = listRedisHandler.leftPush(key, new TestInfo("right2", 1), "abc");
        System.out.println();
    }

    @Test
    public void testOpsListLeftPushIfPresent() {
        String emptyKey = "emptyKey";
        String key = "list1";
        long del = basicRedis.del(emptyKey, key);
        Long number = listRedisHandler.leftPushIfPresent(emptyKey, "empty");
        number = listRedisHandler.leftPush(key, "left1");
        number = listRedisHandler.leftPushIfPresent(key, "left2");
        System.out.println();
    }

    @Test
    public void testOpsListRange() {
        String rangeKey = "rangeKey";
        basicRedis.del(rangeKey);

        listRedisHandler.leftPush(rangeKey, "1");
        listRedisHandler.leftPush(rangeKey, "2");
        listRedisHandler.leftPush(rangeKey, "3");
        List<String> range = listRedisHandler.range(rangeKey, 0, 1, String.class);
        System.out.println();
    }

    @Test
    public void testOpsListTrim() {
        String trimKey = "trimKey";
        basicRedis.del(trimKey);

        listRedisHandler.leftPush(trimKey, "1");
        listRedisHandler.leftPush(trimKey, "2");
        listRedisHandler.leftPush(trimKey, "3");
        listRedisHandler.leftPush(trimKey, "4");
        listRedisHandler.trim(trimKey, 1, 2);
        System.out.println();
    }

    @Test
    public void testOpsListIndex() {
        String indexKey = "listIndex";
        basicRedis.del(indexKey);

        listRedisHandler.leftPush(indexKey, "1");
        listRedisHandler.leftPush(indexKey, "2");
        final String s = listRedisHandler.listIndex(indexKey, 1, String.class);

    }

    @Test
    public void testOpsListRemove() {
        String removeKey = "listRemove";
        basicRedis.del(removeKey);
        listRedisHandler.leftPush(removeKey, "a");
        listRedisHandler.leftPush(removeKey, "a");
        listRedisHandler.leftPush(removeKey, "b");
        listRedisHandler.leftPush(removeKey, "a");
        listRedisHandler.leftPush(removeKey, "a");
        listRedisHandler.leftPush(removeKey, "a");
        listRedisHandler.listRemove(removeKey, 1, "a");
    }

    @Test
    public void testPushList() {
        String key = "pushList";
        basicRedis.del(key);
        TestInfo testInfo = new TestInfo("abc", 10);
        TestInfo listInfo = new TestInfo("list", 10);
        List<TestInfo> list = new ArrayList<>();
        list.add(listInfo);
        testInfo.setList(list);
        listRedisHandler.leftPush(key, testInfo);
        TestInfo testInfo1 = listRedisHandler.rightPop(key, TestInfo.class);
        System.out.println();
    }

    @Test
    public void testOpsHashPutAndGet() {
        String key = "hashPutAndGet";
        basicRedis.del(key);
        template.opsForHash().put(key, "abc", gson.toJson(new TestInfo("aa", 10)));
        Object abc = template.opsForHash().get(key, "abc");
        System.out.println();
    }

    @Test
    public void testOpsHash() {
        String key = "hashListOrMap";
        String mapKey = "hashMap";
        basicRedis.del(key);

        List<TestInfo> list = new ArrayList<>();
        list.add(new TestInfo("abc", 10));
        list.add(new TestInfo("ab", 20));
        hashRedis.hashPut(key, "hashKey", list);
        List<TestInfo> list1 = hashRedis.hashGetListOrMap(key, "hashKey", new TypeToken<List<TestInfo>>() {
        });
        System.out.println();

        hashRedis.hashPut(mapKey, "one", new TestInfo("a", 31));
        hashRedis.hashPut(mapKey, "two", new TestInfo("b", 31));
        hashRedis.hashPut(mapKey, "three", new TestInfo("c", 31));
        hashRedis.hashPut(mapKey, "four", new TestInfo("d", 31));
        final TestInfo testInfo = hashRedis.hashGet(mapKey, "one", TestInfo.class);
        List<Object> hashKeys = new ArrayList<>();
        hashKeys.add("one");
        hashKeys.add("two");
        hashKeys.add("three");
        hashKeys.add("four");
        final List<TestInfo> list2 = hashRedis.hashMultiGet(mapKey, hashKeys, TestInfo.class);
        System.out.println();

        final Long aLong = hashRedis.hashSize(mapKey);
        final Map<String, String> stringStringMap = hashRedis.hashGet(mapKey);
        System.out.println();
    }

    @Test
    public void testOpsHashInDecrement() {
        String key = "hashInDecrement";
        basicRedis.del(key);

        hashRedis.hashPut(key, "in", 2);
        hashRedis.hashPut(key, "de", 2);

        final Long in = hashRedis.hashIncrement(key, "in");
        final Long de = hashRedis.hashDecrement(key, "de");
        final Long in1 = hashRedis.hashIncrement(key, "in", 2);
        final Long de1 = hashRedis.hashDecrement(key, "de", 2);
        System.out.println();

        hashRedis.hashPut(key, "st", "abc");
        // final Long st = hashRedis.hashIncrement(key, "st");
        hashRedis.hashPut(key, "sd", "1");
        final Long sd = hashRedis.hashIncrement(key, "sd");
        System.out.println();
    }


    @Test
    public void testOpsSet() {
        String key = "opsSet";
        String targetKey=key+"target";
        String errorKey=key+"error";
        basicRedis.del(key,targetKey,errorKey);

        final Long aLong = setRedis.setAdd(key, new TestInfo("a", 1), new TestInfo("b", 2));
        final Long c = setRedis.setAdd(key, new TestInfo("c", 3));
        final Boolean a = setRedis.setExist(key, new TestInfo("a", 1));
        final Boolean b = setRedis.setExist(key, new TestInfo("a", 3));
        final Set<TestInfo> testInfos = setRedis.setMembers(key, TestInfo.class);
        final Set<TestInfo> testInfose = setRedis.setMembers(errorKey, TestInfo.class);
        final TestInfo testInfo = setRedis.setPop(key, TestInfo.class);
        final TestInfo testInfoe = setRedis.setPop(errorKey, TestInfo.class);
        final Long aLong1 = setRedis.setSize(key);
        final Long aLong2 = setRedis.setSize(key);
        final Long aLong3 = setRedis.setSize(errorKey);
        setRedis.setAdd(key,new TestInfo("a1",30),new TestInfo("a2",30),new TestInfo("a3",30));
        final Long a1 = setRedis.setRemove(key, new TestInfo("a1", 30));
        final Long b1 = setRedis.setRemove(key, new TestInfo("b1", 3));
        final Set<TestInfo> testInfos1 = setRedis.setRandomMember(key, 3, TestInfo.class);
        final Set<TestInfo> testInfos2 = setRedis.setDistinctRandomMember(key, 3, TestInfo.class);

        setRedis.setAdd(key,new TestInfo("move",1));
        setRedis.setMove(key,new TestInfo("move",1),targetKey);

        System.out.println();

    }

    @Test
    public void testOpsSetDifferent(){
        String key1="setDifference1";
        String key2="setDifference2";
        basicRedis.del(key1,key2);

        setRedis.setAdd(key1,1,2,3,10);
        setRedis.setAdd(key2,20,21,22,10);
        final Set<Integer> strings = setRedis.setDifferent(key1, key2, int.class);
        System.out.println();
    }

    @Test
    public void testOpsSetIntersect(){
        String key1="setIntersect1";
        String key2="setIntersect2";
        String key3="setIntersect3";
        String key4="setMove1";
        String key5="setMove2";
        basicRedis.del(key1,key2,key3);

        setRedis.setAdd(key1,1,2,3,4);
        setRedis.setAdd(key2,2,3,5,6);
        setRedis.setAdd(key3,3,4,5,7);
        final Set<Integer> integers = setRedis.setIntersect(key1, Arrays.asList(key2, key3), int.class);
        final Set<Integer> integers1 = setRedis.setIntersect(key2, Arrays.asList(key1, key3), int.class);
        final Set<Integer> integers2 = setRedis.setIntersect(key3, Arrays.asList(key1, key2), int.class);
        System.out.println();
        setRedis.setDifferenceAndStore(key1,Arrays.asList(key2,key3),key4);
        System.out.println();
    }

    @Test
    public void testOpsSetUnion(){
        String key1="setUnion1";
        String key2="setUnion2";
        String key3="setUnion3";
        String key4="setUnionMove";
        basicRedis.del(key1,key2,key3,key4);

        setRedis.setAdd(key1,1,2,3);
        setRedis.setAdd(key2,2,3,4);
        setRedis.setAdd(key3,3,4,5);

        final Set<Integer> integers = setRedis.setUnion(key1, key2, int.class);
        final Set<Integer> integers1 = setRedis.setUnion(key1, Arrays.asList(key2, key3), int.class);
        final Long aLong = setRedis.setUnionAndStore(key1, Arrays.asList(key2, key3), key4);
        System.out.println();
    }

    @Data
    @AllArgsConstructor
    public static class TestInfo {
        private String name;
        private Integer age;
        private List<TestInfo> list;

        public TestInfo(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }


    @Test
    public void test() {
        LocalDateTime of = LocalDateTime.of(2021, 11, 13, 9, 0, 0);
        final Duration between = Duration.between(LocalDateTime.now(), of);
        System.out.println(between.toHours());
        System.out.println(between.toMinutes());
        System.out.println(between.toMillis() / 1000);
    }
}
