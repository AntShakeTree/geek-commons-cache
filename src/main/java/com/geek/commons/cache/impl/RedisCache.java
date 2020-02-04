package com.geek.commons.cache.impl;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @program: geek-commons-cache
 * @author: captain.ma
 * @date: 2018-11-21
 * @since: 1.0.0.0
 */
public class RedisCache implements Cache {

    private Object refreshParams;
    private final RedisTemplate<String, Object> redisTemplate;
    private final long defaultFreshTime;
    private final TimeUnit timeUnit;

    public RedisCache(RedisTemplate<String, Object> redisTemplate, String id) {
        this.redisTemplate = redisTemplate;
        this.defaultFreshTime = 10;
        this.timeUnit = TimeUnit.HOURS;
        this.id = id;
        CacheManager.put(id, this);
    }

    public RedisCache(RedisTemplate<String, Object> redisTemplate, String id, long defaultFreshTime, TimeUnit timeUnit) {
        this.redisTemplate = redisTemplate;
        this.defaultFreshTime = defaultFreshTime;
        this.timeUnit = timeUnit;
        this.id = id;
        CacheManager.put(id, this);
    }

    private final String id;

    /**
     * @return The identifier of this cache
     */
    @Override
    public String getId() {
        return id;
    }

    @Override
    public <K, V> V getValue(K key) {
        V v = (V) redisTemplate.opsForValue().get(key(key));
        if (v != null) {
            return v;
        } else {
            Object re = apply();
            if (re != null)
                this.put(key, re);
            return (V) redisTemplate.opsForValue().get(key(key));
        }
    }

    private Object apply() {
        if (this.getFunction() != null)
            return this.function.apply(refreshParams);
        return null;
    }

    @Override
    public <K, V> void put(K key, V value) {
        this.put(key, value, defaultFreshTime, timeUnit);
    }

    @Override
    public <K, V> void put(K key, V value, long times, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key(key), value, times, timeUnit);
    }

    /**
     * Clears this cache instance
     */
    @Override
    public void clear() {

    }

    /**
     * @param key The key
     */
    @Override
    public void remove(Object key) {
//        redisTemplate.opsForValue().set(id + ":" + key.toString(), 1, 1, TimeUnit.NANOSECONDS);
        redisTemplate.delete(id + ":" + key.toString());
    }

    /**
     * The number of elements stored in the cache (not its capacity).
     *
     * @return
     */
    @Override
    public int size() {
        return 0;
    }

    /**
     * Sets the set of parameters needed for the calculation
     *
     * @param o
     * @return
     */
    @Override
    public Object params(Object o) {
        this.refreshParams = 0;
        return refreshParams;
    }

    private Function function;

    @Override
    public Function getFunction() {
        return function;
    }

    @Override
    public void setFunction(Function function) {
        this.function = function;
    }

    private String key(Object key) {
        return key instanceof String || key.getClass().isPrimitive() ? id + ":" + key : id + ":" + key.hashCode();
    }
}
