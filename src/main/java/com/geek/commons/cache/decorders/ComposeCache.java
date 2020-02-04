package com.geek.commons.cache.decorders;


import com.geek.commons.cache.Cache;
import com.geek.commons.cache.CacheException;
import com.geek.commons.cache.CacheManager;
import com.geek.commons.cache.enums.CacheType;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;


/**
 * @Description: geek-commons-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-29 16:16
 */
public class ComposeCache implements Cache {
    private final List<Cache> caches = Lists.newArrayList();


    String id;

    public ComposeCache(String id, Cache... c) {
        if (StringUtils.isEmpty(id)) {
            throw new CacheException("ID is required");
        }
        this.id = id;
        if (c == null || c.length == 0) {
            throw new CacheException("Cache is null.", new NullPointerException());
        }
        for (Cache cache : c) {
            if (cache != null) {
                if (!id.equals(cache.getId())) {
                    throw new CacheException("invalid Id", new IllegalArgumentException());
                }
                caches.add(cache);
            }
        }
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
        for (Cache cache : caches) {
            V v = cache.getValue(key);
            if (v != null) {
                this.putIfAbsent(key, v);
                return v;
            }
        }
        return null;
    }

    @Override
    public <K, V> void put(K key, V value) {
        for (Cache cache : caches) {
            cache.put(key, value);
        }
    }

    /**
     * Clears this cache instance
     */
    @Override
    public void clear() {
        for (Cache cache : caches) {
            cache.clear();
        }
    }

    /**
     * @param key The key
     */
    @Override
    public void remove(Object key) {
        for (Cache cache : caches) {
            cache.remove(key);
        }
    }

    /**
     * The number of elements stored in the cache (not its capacity).
     *
     * @return
     */
    @Override
    public int size() {
        return caches.get(0).size();
    }

    /**
     * Sets the set of parameters needed for the calculation
     *
     * @param o
     * @return
     */
    @Override
    public Object params(Object o) {
        for (Cache cache : caches) {
            cache.params(o);
        }
        return o;
    }

    @Override
    public Function getFunction() {
        return null;
    }

    @Override
    public void setFunction(Function function) {

    }

    public void remove(int index, Object key) {
        caches.get(index).remove(key);
    }

    public Cache getCache(int index) {
        return this.caches.get(index);
    }

    public <K, V> void putIfAbsent(K key, V value) {
        for (Cache cache : caches) {
            if (cache.getValue(key) == null) {
                this.put(key, value);
            }
        }
    }


}
