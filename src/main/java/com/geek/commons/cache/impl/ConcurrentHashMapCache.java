package com.geek.commons.cache.impl;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.CacheException;
import com.geek.commons.cache.CacheManager;
import com.geek.commons.cache.DelayItems;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.geek.commons.cache.CacheManager.queue;

/**
 * @Description: geek-data-platform   ConcurentHashMap
 * @Author: Captain.Ma
 * @Date: 2018-10-25 10:47
 */
public class ConcurrentHashMapCache implements Cache {
    private final ConcurrentMap concurrentMap;

    private final String id;
    private final long defaultTimes;
    private final TimeUnit timeUnit;


    public ConcurrentHashMapCache(String id) {
        this.id = id;
        concurrentMap = new ConcurrentHashMap();
        defaultTimes = 0;
        timeUnit = TimeUnit.SECONDS;

        CacheManager.put(id, this);
    }

    public ConcurrentHashMapCache(String id, long defaultTimes, TimeUnit timeUnit) {
        this.id = id;
        concurrentMap = new ConcurrentHashMap();

        this.defaultTimes = defaultTimes;
        this.timeUnit = timeUnit;
        CacheManager.put(id, this);

    }

    /**
     * @return The identifier of this cache
     */
    @Override
    public String getId() {
        return this.id;
    }


    private Object refreshParams;

    @Override
    public <K, V> V getValue(K key) {
        return (V) this.concurrentMap.get(key);
    }

    private Object[] params;

    @Override
    public void args(Object... params) {
        this.params = params;
    }

    @Override
    public Object[] args() {
        return params;
    }


    @Override
    public <K, V> void put(K key, V value) {
        this.concurrentMap.put(key, value);
        if (defaultTimes > 0) {
            queue().put(new DelayItems(key, defaultTimes, timeUnit).id(id));
        }
    }

    @Override
    public <K, V> void put(K key, V value, long times, TimeUnit timeUnit) {
        this.concurrentMap.put(key, value);
        queue().put(new DelayItems(key, times, timeUnit).id(id));
    }

    /**
     * Clears this cache instance
     */
    @Override
    public void clear() {
        this.concurrentMap.clear();
    }

    /**
     * @param key The key
     */
    @Override
    public void remove(Object key) {
        this.concurrentMap.remove(key);
    }

    /**
     * The number of elements stored in the cache (not its capacity).
     *
     * @return
     */
    @Override
    public int size() {
        return this.concurrentMap.size();
    }

    private Function function;


    @Override
    public Function refresh() {
        return this.function;
    }

    @Override
    public void setRefresh(Function function) {
        this.function = function;
    }

    @Override
    public boolean equals(Object o) {
        if (getId() == null) {
            throw new CacheException("Cache instances require an ID.");
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cache)) {
            return false;
        }

        Cache otherCache = (Cache) o;
        return getId().equals(otherCache.getId());
    }

    @Override
    public int hashCode() {
        if (getId() == null) {
            throw new CacheException("Cache instances require an ID.");
        }
        return getId().hashCode();
    }

    @Override
    public <K, V> void putIfAbsent(K k, V v) {
        this.concurrentMap.putIfAbsent(k, v);
        if (defaultTimes > 0) {
            queue().put(new DelayItems(k, defaultTimes, timeUnit).id(id));
        }

    }

    @Override
    public boolean contain(Object key) {
        return this.concurrentMap.containsKey(key);
    }

}
