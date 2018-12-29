package com.geek.commons.cache;

/**
 * @program: geek-commons-cache
 * @author: captain.ma
 * @date: 2018-12-28
 * @since: 1.0.0.0
 */
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @program: inte-kids
 * @author: captain.ma
 * @date: 2018-12-27
 * @since: 1.0.0.0
 */
public class DelayItems implements Delayed {
    private final long startTime;
    @Getter
    private final Object key;
    @Getter
    private String id;
    public DelayItems id(String id){
        this.id=id;
        return this;
    }
    @Getter
    private final long delayTime;

    public DelayItems(Object key, long delayTime, TimeUnit timeUnit) {
        this.key = key;
        this.delayTime = delayTime;
        startTime = TimeUnit.MILLISECONDS.convert(delayTime,timeUnit) + System.currentTimeMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startTime - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }


    @Override
    public int compareTo(Delayed o) {
        if (this.startTime < ((DelayItems) o).startTime) {
            return -1;
        }
        if (this.startTime > ((DelayItems) o).startTime) {
            return 1;
        }
        return 0;
    }
}
