package com.geek.commons.cache.impl;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.CacheException;
import com.geek.commons.cache.CacheManager;
import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-24 20:21
 */
public final class BaseGuavaCache implements Cache {

    private Object refreshParams;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private String id;
    /**
     * 缓存自动刷新周期
     */
    private int refreshDuration;
    /**
     * 缓存刷新周期时间格式
     */
    private TimeUnit refreshTimeunit;
    /**
     * 缓存过期时间（可选择）
     */
    private int expireDuration;
    /**
     * 缓存刷新周期时间格式
     */
    private TimeUnit expireTimeunit;
    /**
     * 缓存最大容量
     */
    private int maxSize;

    /**
     * 数据刷新线程池
     */

    private static ListeningExecutorService refreshPool = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(20, 20, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<>(1000)));


    public <K, V> BaseGuavaCache(String id, int size, Function<K, V> valueWhenExpiredFunction, int refreshDuration, TimeUnit refreshTimeunit, int expireDuration, TimeUnit expireTimeunit) {
        this.valueWhenExpiredFunction = valueWhenExpiredFunction;
        this.refreshDuration = refreshDuration;
        this.refreshTimeunit = refreshTimeunit;
        this.expireDuration = expireDuration;
        this.expireTimeunit = expireTimeunit;
        this.maxSize = size;
        this.id = id;
        CacheManager.put(id, this);
    }

    public BaseGuavaCache(String id, Function function) {
        this(id, 1024, function, 10, TimeUnit.MINUTES, 24, TimeUnit.HOURS);
    }

    @Override
    public String getId() {
        return id;
    }


    @Override
    public void remove(Object key) {
        getCache().invalidate(key);
    }

    /**
     * The number of elements stored in the cache (not its capacity).
     *
     * @return
     */
    @Override
    public int size() {
        return this.maxSize;
    }

    @Override
    public Object params(Object o) {
        this.refreshParams = o;
        return refreshParams;
    }

    @Override
    public java.util.function.Function getFunction() {
        return this.valueWhenExpiredFunction;
    }

    @Override
    public void setFunction(java.util.function.Function function) {

    }

    @Override
    public <K, V> void put(K key, V value) {
        getCache().put(key, value);
    }

    private final java.util.function.Function valueWhenExpiredFunction;


    @Override
    public void clear() {
        this.getCache().invalidateAll();
    }

    private com.google.common.cache.Cache cache;

    /**
     * @description: 获取cache实例
     * @author: Captain.Ma
     * @date: 2017年6月13日 下午2:50:11
     */
    private <K, V> com.google.common.cache.Cache<K, V> getCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    CacheBuilder<Object, Object> cacheBuilder =
                            CacheBuilder.newBuilder().maximumSize(maxSize);

                    if (refreshDuration > 0) {
                        cacheBuilder = cacheBuilder.refreshAfterWrite(refreshDuration, refreshTimeunit);
                    }
                    if (expireDuration > 0) {
                        cacheBuilder = cacheBuilder.expireAfterWrite(expireDuration, expireTimeunit);
                    }
                    if (valueWhenExpiredFunction != null) {
//                        cache = cacheBuilder.build(CacheLoader.asyncReloading(CacheLoader.from((Key) -> defaultReFresh(BaseGuavaCache.this.valueWhenExpiredFunction)), refreshPool));

                        cache = cacheBuilder.build(CacheLoader.from((Key) -> defaultReFresh(BaseGuavaCache.this.valueWhenExpiredFunction)));
                    }
                    if (valueWhenExpiredFunction == null) {
                        cache = CacheBuilder.newBuilder().build();
                    }

                }
            }
        }
        return cache;

    }


    @Override
    public String toString() {
        return "BaseGuavaCache{" +
                "refreshDuration=" + refreshDuration +
                ", refreshTimeunit=" + refreshTimeunit +
                ", expireDuration=" + expireDuration +
                ", expireTimeunit=" + expireTimeunit +
                ", maxSize=" + maxSize +
                '}';
    }


    //构建
    public static <Key, Val> BaseGuavaCache build(String id, Function<Key, Val> kvFunction) {
        return new BaseGuavaCache(id, kvFunction);
    }

    public static <K, V> BaseGuavaCache build(String id, int size, Function<K, V> valueWhenExpiredFunction, int refreshDuration, TimeUnit refreshTimeunit, int expireDuration, TimeUnit expireTimeunit) {
        return new BaseGuavaCache(id, size, valueWhenExpiredFunction, refreshDuration, refreshTimeunit, expireDuration, expireTimeunit);
    }

    public static BaseGuavaCache build(String id) {
        return new BaseGuavaCache(id, null);
    }

    /**
     * @param key
     * @throws Exception
     * @description: 从cache中拿出数据操作
     * @author: Captain.Ma
     * @date: 2017年6月13日 下午5:07:11
     */

    @Override
    public <K, V> V getValue(K key) {
        try {
            com.google.common.cache.Cache<Object, Object> cache = getCache();
            if (cache instanceof LoadingCache) {
                try {
                    return (V) ((LoadingCache) getCache()).get(key);
                } catch (Exception e) {
                    return null;
                }
            }
            return (V) getCache().getIfPresent(key);

        } catch (Exception e) {
            logger.error("从内存缓存中获取内容时发生异常，key: " + key, e);
            throw new RuntimeException(e);
        }
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

    public Object defaultReFresh(java.util.function.Function function) {


        return function.apply(BaseGuavaCache.this.refreshParams);
    }
}