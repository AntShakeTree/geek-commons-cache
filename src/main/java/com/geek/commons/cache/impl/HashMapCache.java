package com.geek.commons.cache.impl;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.CacheException;
import com.geek.commons.cache.CacheManager;
import com.geek.commons.cache.DelayItems;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.geek.commons.cache.CacheManager.queue;

/**
 * @Description: geek-data-platform   hashMap 实现的cache
 * @Author: Captain.Ma
 * @Date: 2018-10-25 10:45
 */
public class HashMapCache implements Cache {
    private HashMap hashMap = new HashMap();

    private final String id;

    private Function function;

    private final long defaultTimes;
    private final TimeUnit timeUnit;


    public HashMapCache(String id) {
        this.id = id;

        timeUnit = null;
        defaultTimes = 0;
        CacheManager.put(id, this);

    }

    public HashMapCache(String id, long defaultTimes, TimeUnit timeUnit) {

        this.id = id;
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

    @Override
    public <K, V> V getValue(K key) {
        return (V) this.hashMap.get(key);

    }


    @Override
    public <K, V> void put(K key, V value, long times, TimeUnit timeUnit) {
        this.hashMap.put(key, value);
        queue().put(new DelayItems(key, times, timeUnit).id(id));
    }

    @Override
    public <K, V> void put(K key, V value) {
        this.hashMap.put(key, value);
        if (defaultTimes > 0) {
            queue().put(new DelayItems(key, defaultTimes, timeUnit).id(id));
        }
    }

    /**
     * Clears this cache instance
     */
    @Override
    public void clear() {
        this.hashMap.clear();
    }

    /**
     * @param key The key
     */
    @Override
    public void remove(Object key) {
        this.hashMap.remove(key);
    }

    /**
     * The number of elements stored in the cache (not its capacity).
     *
     * @return
     */
    @Override
    public int size() {
        return this.hashMap.size();
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
    public Function refresh() {
        return function;
    }

    @Override
    public void setRefresh(Function function) {
        this.function = function;
    }
}
