package com.geek.commons.cache.impl;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.CacheManager;
import com.geek.commons.cache.DelayItems;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.geek.commons.cache.CacheManager.queue;

/**
 * @program: geek-commons-cache
 * @author: captain.ma
 * @date: 2018-11-21
 * @since: 1.0.0.0
 */
public class RedisCache implements Cache {

    private Object[] refreshParams;
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
//        if (v == null && this.function != null) {
//            if (this.refreshParams != null && this.refreshParams.length > 0) {
//                this.function.apply(this.refreshParams);
//            } else {
//                this.function.apply(key);
//            }
//        }
        return v;
    }


    @Override
    public <K, V> void put(K key, V value) {
        this.put(key, value, defaultFreshTime, timeUnit);
        queue().put(new DelayItems(key, defaultFreshTime, timeUnit).id(id));

    }

    @Override
    public <K, V> void put(K key, V value, long times, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key(key), value, times, timeUnit);
        queue().put(new DelayItems(key, times, timeUnit).id(id));
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


    private Function function;

    @Override
    public Function refreshFu() {
        return function;
    }

    @Override
    public void setRefreshFu(Function function) {
        this.function = function;
    }

    @Override
    public boolean contain(Object key) {
        return this.getValue(key) == null;
    }

    private String key(Object key) {
        return key instanceof String || key.getClass().isPrimitive() ? id + ":" + key : id + ":" + key.hashCode();
    }
}
