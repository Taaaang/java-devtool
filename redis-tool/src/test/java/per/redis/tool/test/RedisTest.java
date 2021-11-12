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
import per.redis.tool.support.handler.IBasicRedis;
import per.redis.tool.support.handler.IListRedis;
import per.redis.tool.support.handler.ListRedisHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private RedisTemplate<String,String> template;

    @Autowired
    @Qualifier("basicRedisHandler")
    private IBasicRedis basicRedis;

    @Autowired
    private IListRedis listRedisHandler;

    @Autowired
    private Gson gson;

    @Test
    public void testOpsValueSetAndGet(){
        redisSupport.set("test",1);
        TestInfo testInfo=new TestInfo("abc",10);
        redisSupport.set("test1",testInfo);
        redisSupport.set("test2","test");
        List<TestInfo> testInfoList=new ArrayList<>();
        testInfoList.add(new TestInfo("aaa",2));
        testInfoList.add(new TestInfo("bbb",3));
        redisSupport.set("test3",testInfoList);
        TestInfo[] testInfos=new TestInfo[2];
        testInfos[0]=new TestInfo("a",41);
        testInfos[1]=new TestInfo("ad",445);
        redisSupport.set("test4",testInfos);

        int test = redisSupport.get("test",int.class);
        TestInfo testInfo1=redisSupport.get("test1",TestInfo.class);
        String test2=redisSupport.get("test2",String.class);
        List<TestInfo> test3=redisSupport.getListOrMap("test3", new TypeToken<List<TestInfo>>(){});
        TestInfo[] test4 = redisSupport.get("test4", TestInfo[].class);

        System.out.println(test);
        System.out.println(gson.toJson(testInfo1));
        System.out.println(test2);
        System.out.println(gson.toJson(test3));
        System.out.println(test3.get(0).getName());
        System.out.println(test4[0].getName());
    }

    @Test
    public void testOpsValueMultiSet(){

        Map<String,Object> map=new HashMap<>();
        map.put("multi1","abc");
        map.put("multi2",3);
        map.put("multi3",new TestInfo("multi3",3));
        Map<String,TestInfo> maps=new HashMap<>();
        maps.put("key1",new TestInfo("map1",1));
        maps.put("key2",new TestInfo("map2",2));
        map.put("multi4",maps);
        redisSupport.mset(map);

        String multi1 = redisSupport.get("multi1", String.class);
        int multi2 = redisSupport.get("multi2", int.class);
        TestInfo multi3 = redisSupport.get("multi3", TestInfo.class);
        Map<String, TestInfo> multi4 = redisSupport.getListOrMap("multi4", new TypeToken<Map<String, TestInfo>>() {
        });

        Assert.assertEquals(map.get("multi1"),multi1);
        Assert.assertEquals(map.get("multi2"),multi2);
        Assert.assertEquals("multi3",multi3.getName());
        Assert.assertEquals("map1",multi4.get("key1").getName());
        Assert.assertEquals("map2",multi4.get("key2").getName());

    }

    @Test
    public void testOpsValueMultiGet(){
        testOpsValueMultiSet();
        Map<String,String> mget = redisSupport.mgetToMap("multi1", "multi2", "multi5", "multi4");
        System.out.println(mget.get("multi5"));
        List<String> mget1 = redisSupport.mget("multi1", "multi2", "multi5", "multi4");
        System.out.println(mget1.get(2));
        System.out.println(mget1.get(3));
    }

    @Test
    public void testOpsValueInDecrement(){
        redisSupport.set("number",1);
        long result1 = redisSupport.increment("number");
        Assert.assertEquals(2, (int) redisSupport.get("number", int.class));
        long result = redisSupport.incrementByNumber("number", 2);
        Assert.assertEquals(4,(int)redisSupport.get("number",int.class));
        Assert.assertEquals(result1,2);
        Assert.assertEquals(result,4);

        redisSupport.set("number",3);
        long number = redisSupport.decrement("number");
        Assert.assertEquals(number,2);
        long number1 = redisSupport.decrementByNumber("number", 2);
        Assert.assertEquals(number1,0);

    }

    @Test
    public void testOpsListPopAndPush(){
        String key="list";
        basicRedis.del(key);
        Long number = listRedisHandler.rightPush(key, "right1");
        Assert.assertEquals(1,number.intValue());
        number = listRedisHandler.leftPush(key, "left1");
        Assert.assertEquals(2,number.intValue());
        List<Object> values=new ArrayList<>(3);
        values.add(1);
        values.add("right2");
        values.add(new TestInfo("right3",2));
        number = listRedisHandler.rightPushAll(key, values);
        Assert.assertEquals(values.size()+2,number.intValue());
        //left1,right1,1,right2,[right3,2]
        //[right3,2],left1,right1,1,right2
        TestInfo list = listRedisHandler.rightPopAndLeftPush(key, "list", TestInfo.class);
        Assert.assertEquals(list.getName(),"right3");
        String right2 = listRedisHandler.rightPop(key, String.class);
        Assert.assertEquals(right2,"right2");
        Integer one = listRedisHandler.rightPop(key, int.class);
        Assert.assertEquals(1,one.intValue());
    }

    @Test
    public void testOpsListPop(){
        String key="popEmpty";
        String rightKey="rightKey";
        String leftKey="leftKey";
        basicRedis.del(key,rightKey,leftKey);
        Long number = listRedisHandler.rightPush(key, "abc");
        number=listRedisHandler.rightPop(key,null);
        number=listRedisHandler.rightPop(key,null);


        listRedisHandler.rightPush(leftKey,"right");
        String s = listRedisHandler.rightPopAndLeftPush(rightKey, leftKey, String.class);
        System.out.println();
    }

    @Test
    public void testOpsListRightPushPosition(){
        String key="rightPushPosition";
        basicRedis.del(key);
        listRedisHandler.rightPush(key,new TestInfo("right3",1));
        listRedisHandler.rightPush(key,new TestInfo("right2",1));
        listRedisHandler.rightPush(key,new TestInfo("right1",1));
        Long aLong = listRedisHandler.leftPush(key, new TestInfo("right2", 1), "abc");
        System.out.println();
    }

    @Test
    public void testOpsListLeftPushIfPresent(){
        String emptyKey="emptyKey";
        String key="list1";
        long del = basicRedis.del(emptyKey, key);
        Long number = listRedisHandler.leftPushIfPresent(emptyKey, "empty");
        number = listRedisHandler.leftPush(key, "left1");
        number=listRedisHandler.leftPushIfPresent(key,"left2");
        System.out.println();
    }

    @Test
    public void testOpsListRange(){
        String rangeKey="rangeKey";
        basicRedis.del(rangeKey);

        listRedisHandler.leftPush(rangeKey,"1");
        listRedisHandler.leftPush(rangeKey,"2");
        listRedisHandler.leftPush(rangeKey,"3");
        List<String> range = listRedisHandler.range(rangeKey, 0, 1, String.class);
        System.out.println();
    }

    @Test
    public void testOpsListTrim(){
        String trimKey="trimKey";
        basicRedis.del(trimKey);

        listRedisHandler.leftPush(trimKey,"1");
        listRedisHandler.leftPush(trimKey,"2");
        listRedisHandler.leftPush(trimKey,"3");
        listRedisHandler.leftPush(trimKey,"4");
        listRedisHandler.trim(trimKey, 1, 2);
        System.out.println();
    }

    @Test
    public void testOpsListIndex(){
        String indexKey="listIndex";
        basicRedis.del(indexKey);

        listRedisHandler.leftPush(indexKey,"1");
        listRedisHandler.leftPush(indexKey,"2");
        final String s = listRedisHandler.listIndex(indexKey, 1, String.class);

    }

    @Test
    public void testOpsListRemove(){
        String removeKey="listRemove";
        basicRedis.del(removeKey);
        listRedisHandler.leftPush(removeKey,"a");
        listRedisHandler.leftPush(removeKey,"a");
        listRedisHandler.leftPush(removeKey,"b");
        listRedisHandler.leftPush(removeKey,"a");
        listRedisHandler.leftPush(removeKey,"a");
        listRedisHandler.leftPush(removeKey,"a");
        listRedisHandler.listRemove(removeKey,1,"a");
    }

    @Test
    public void testPushList(){
        String key="pushList";
        basicRedis.del(key);
        TestInfo testInfo = new TestInfo("abc", 10);
        TestInfo listInfo=new TestInfo("list",10);
        List<TestInfo> list=new ArrayList<>();
        list.add(listInfo);
        testInfo.setList(list);
        listRedisHandler.leftPush(key,testInfo);
        TestInfo testInfo1 = listRedisHandler.rightPop(key, TestInfo.class);
        System.out.println();
    }

    @Data
    @AllArgsConstructor
    public static class TestInfo{
        private String name;
        private Integer age;
        private List<TestInfo> list;

        public TestInfo(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
