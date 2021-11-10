package per.java.tool.sliding.window;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 窗口操作类
 *
 * @Author:TangFenQi
 * @Date:2021/11/10 10:31
 **/
public class WindowWorker {

    private AtomicReferenceArray<WindowBucket> buckets;//窗口容器
    private long intervalInMs;//间隔时间
    private long windowsCount;//窗口数量
    private long windowsInterval;//窗口间隔时间

    private ReentrantLock countLock = new ReentrantLock();//

    public WindowWorker(long intervalInMs, long windowsCount) {
        this.intervalInMs = intervalInMs;
        this.windowsCount = windowsCount;
        this.windowsInterval = intervalInMs / windowsCount;
        this.buckets = new AtomicReferenceArray<WindowBucket>((int) windowsCount);
    }

    public long windowsInterval() {
        return windowsInterval;
    }

    public void count() {
        count(1L, System.currentTimeMillis());
    }

    public void count(long acquireCount, long currentTime) {
        if (acquireCount < 1) throw new RuntimeException(String.format("invalid acquireCount[%s]", acquireCount));
        WindowBucket bucket = getBucket(currentTime);
        bucket.add(acquireCount);
    }

    public long get() {
        long currentTime = System.currentTimeMillis();
        return get(currentTime);
    }

    public long get(long currentTime) {
        long trimTime = trimTime(currentTime);
        int bucketId = getIndexId(currentTime);
        WindowBucket windowBucket = buckets.get(bucketId);
        if (windowBucket.windowStart() == trimTime) {
            return windowBucket.get();
        } else {
            return 0L;
        }
    }

    public AtomicReferenceArray<WindowBucket> getBuckets(){
        return buckets;
    }

    private WindowBucket getBucket(long currentTime) {
        //获取currentTime标识的时间所对应的buckets索引
        int bucketId = getIndexId(currentTime);
        //对当前时间进行修剪，得到窗口开始时间
        long startTime = trimTime(currentTime);
        /**
         * 判断bucket情况，这里分为两种情况
         * 1.根据索引，拿到的bucket为null，代表这个未被使用过，可以直接使用
         * 2.根据索引，拿到的bucket不为null，代表已经被使用，也分为两种情况
         *  2.1 bucket的windowStartInMs窗口开始时间，与currentTime对齐时间一致，代表是当前时间的窗口，直接使用
         *  2.2 与currentTime对齐时间不一致
         *   2.2.1 如果是过期时间，是之前，那么进行重置
         *   2.2.2 如果是未来时间，那么抛出异常
         */

        //如果更新失败，进行自旋
        while (true) {
            WindowBucket bucket = buckets.get(bucketId);
            if (bucket == null) {
                WindowBucket newBucket = new WindowBucket(startTime, windowsInterval);
                if (buckets.compareAndSet(bucketId, null, newBucket)) {
                    return newBucket;
                }
            } else {
                if (bucket.windowStart() == startTime) {
                    return bucket;
                } else if (bucket.windowStart() < startTime) {
                    try {
                        countLock.tryLock();
                        bucket.reset(startTime, windowsInterval);
                        return bucket;
                    } catch (Exception ex) {
                        //让出当前cpu分配的时间分片，等待下次cpu唤醒（可能下次还是该线程执行，只是标识为可让出）
                        Thread.yield();
                    } finally {
                        countLock.unlock();
                    }
                } else {
                    throw new RuntimeException(String.format("bucket window start[%s] beyond current time[%s]", bucket.windowStart(), currentTime));
                }
            }
        }
    }

    //裁剪时间
    private long trimTime(long currentTime) {
        return currentTime - (currentTime % windowsInterval);
    }

    private int getIndexId(long currentTime) {
        //计算在时间轴上处于哪个bucket里面
        long timeNumber = currentTime / windowsInterval;
        //桶有涯，轴无涯，以有涯逐无涯，殆已
        //时间对于buckets进行取余操作，使容器成环，重复使用避免创建与移除超过窗口的bucket
        return (int) (timeNumber % buckets.length());
    }
}
