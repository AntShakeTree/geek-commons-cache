package com.geek.commons.cache.impl;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.CacheException;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @Description: geek-data-platform   hashMap 实现的cache
 * @Author: Captain.Ma
 * @Date: 2018-10-25 10:45
 */
public class HashMapCache implements Cache {
    private HashMap hashMap = new HashMap();

    private final String id;

    private Function function;
    private Object refreshParams;

    public HashMapCache(String id) {
        this.id = id;
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
        V v = (V) this.hashMap.get(key);
        if (v == null) {
            Object re = apply();
            if (re != null)
                this.put(key, re);
            return (V) this.hashMap.get(key);
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
        this.hashMap.put(key, value);
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
    public Object params(Object o) {
        this.refreshParams = o;
        return refreshParams;
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

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }
}
