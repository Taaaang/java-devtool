package per.java.tool.sliding.window;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * @Author:TangFenQi
 * @Date:2021/11/10 11:57
 **/
public class Test {

    public static void main(String[] args) throws InterruptedException {
        int windowCount = 16;
        long interval = 10000L;
        //创建一个每1s一个窗口的容器
        WindowWorker worker = new WindowWorker(interval, windowCount);
        //记录每个窗口应该有的次数，与结果做比较
        ConcurrentHashMap<Long, AtomicInteger> counter = new ConcurrentHashMap<>(windowCount);
        //启动线程去写入
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(8);

        //统计
        for (int i = 0; i < 8; i++) {
            executor.scheduleAtFixedRate(new MytTask(worker,counter),i*100,300, TimeUnit.MILLISECONDS);
        }

        Thread.sleep(10000L);
        executor.shutdownNow();
        Thread.sleep(500L);

        //验证
        AtomicReferenceArray<WindowBucket> buckets = worker.getBuckets();
        for (int i = 0; i < buckets.length(); i++) {
            WindowBucket bucket = buckets.get(i);
            if (bucket.get() == counter.get(bucket.windowStart()).get()) {
                System.out.println(String.format("成功 ,时间窗口：[%s],窗口计数[%s],统计计数[%s]", bucket.windowStart(), bucket.get(), counter.get(bucket.windowStart()).get()));
            }else {
                System.out.println(String.format("失败 ,时间窗口：[%s],窗口计数[%s],统计计数[%s]", bucket.windowStart(), bucket.get(), counter.get(bucket.windowStart()).get()));
            }

        }

    }

    public static class MytTask implements Runnable{

        private WindowWorker worker;
        ConcurrentHashMap<Long, AtomicInteger> counter;

        public MytTask(WindowWorker worker,ConcurrentHashMap<Long, AtomicInteger> counter) {
            this.worker=worker;
            this.counter=counter;
        }

        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            long startTime = currentTime - currentTime % worker.windowsInterval();
            worker.count(1, currentTime);
            AtomicInteger atomicInteger = counter.putIfAbsent(startTime, new AtomicInteger(1));
            if (atomicInteger != null) {
                atomicInteger.incrementAndGet();
            }
        }
    }

}
