package com.geek.commons.cache.impl;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.CacheException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @Description: geek-data-platform   ConcurentHashMap
 * @Author: Captain.Ma
 * @Date: 2018-10-25 10:47
 */
public class ConcurrentHashMapCache implements Cache {
    private ConcurrentMap concurrentMap = new ConcurrentHashMap();

    private final String id;

    public ConcurrentHashMapCache(String id) {
        this.id = id;
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
        V v = (V) this.concurrentMap.get(key);
        if (v == null) {
            Object re = apply();
            if (re != null)
                this.put(key, re);
            return (V) this.concurrentMap.get(key);
        } else {
            return v;
        }
    }
    private Object apply() {
        if (this.getFunction() != null)
            return this.function.apply(refreshParams);
        return null;
    }
    @Override
    public <K, V> void put(K key, V value) {
        this.concurrentMap.put(key, value);
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
    public Object params(Object o) {
        this.refreshParams = o;
        return refreshParams;
    }

    @Override
    public Function getFunction() {
        return this.function;
    }

    @Override
    public void setFunction(Function function) {
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
}
