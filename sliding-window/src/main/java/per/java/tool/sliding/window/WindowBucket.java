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

    public void add(long count){
        counter.add(count);
    }

    public long get(){
        return counter.sum();
    }

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

