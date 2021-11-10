package per.java.tool.sliding.window;

import java.util.concurrent.atomic.LongAdder;

/**
 * 窗口对象
 * @Author:TangFenQi
 * @Date:2021/11/10 10:12
 **/
public class WindowBucket {

    private long windowStartInMs;//窗口的起始时间
    private long intervalInMs;//窗口的间隙时间
    private LongAdder counter;//计数器


    public WindowBucket(long windowStartInMs, long intervalInMs) {
        this.windowStartInMs = windowStartInMs;
        this.intervalInMs = intervalInMs;
        this.counter=new LongAdder();
    }

    /**
     * 计数器进行累加
     * @param count 本次增加的次数
     */
    public void add(long count){
        counter.add(count);
    }

    /**
     * 获取当前的次数
     * @return 当前的次数
     */
    public long get(){
        return counter.sum();
    }

    /**
     * 重置窗口对象
     * @param startTime 窗口新的开始时间
     * @param intervalTime 窗口的间隔时间
     */
    public void reset(long startTime,long intervalTime){
        this.windowStartInMs=startTime;
        this.intervalInMs=intervalTime;
        this.counter.reset();
    }

    public long windowStart(){
        return windowStartInMs;
    }

    public long interval(){
        return intervalInMs;
    }

}

