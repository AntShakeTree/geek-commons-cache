package com.geek.commons.cache;

import com.geek.commons.cache.decorders.LoggingCache;
import com.geek.commons.cache.decorders.LruCache;
import com.geek.commons.cache.enums.CacheType;
import com.geek.commons.cache.impl.BaseGuavaCache;
import com.geek.commons.cache.impl.ConcurrentHashMapCache;
import com.geek.commons.cache.impl.HashMapCache;
import com.google.common.base.Function;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;


/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-28 11:19
 */
public class CacheManager {
    private static final Map<String, com.geek.commons.cache.Cache> CACHE_MAP = new ConcurrentHashMap<>();

    private static final Object lock = new Object();

    public static com.geek.commons.cache.Cache cache(String id) {
        return CACHE_MAP.get(id);
    }

    public static com.geek.commons.cache.Cache put(String id, com.geek.commons.cache.Cache cache) {
        return CACHE_MAP.put(id, cache);
    }

    public static com.geek.commons.cache.Cache createLocalCache(String id, CacheType cacheType) {
        if (cache(id) != null) {
            return cache(id);
        }
        Cache cache;
        switch (cacheType) {
            case GUAVA:
                cache = BaseGuavaCache.build(id);
                break;
            case CONCURRENT:
                cache = new ConcurrentHashMapCache(id);
                break;
            default:
                cache = new HashMapCache(id);
                break;
        }
        CACHE_MAP.put(id, cache);
        return cache;
    }

    public static com.geek.commons.cache.Cache remove(String id) {
        return CACHE_MAP.remove(id);
    }

    public static com.geek.commons.cache.Cache createCache(String id, com.geek.commons.cache.annotations.Cache cache, Function function) {
        com.geek.commons.cache.Cache ca = cache(id);
        if (ca != null) {
            return ca;
        }
        synchronized (lock) {
            CacheType type = cache.value();
            boolean fresh = cache.refresh();
            int interval = cache.interval();
            TimeUnit timeUnit = cache.timeUnit();
            int lru = cache.lru();
            switch (type) {
                case HASH:
                    if (fresh) {
                        ca = new HashMapCache(id, interval, timeUnit);
                    } else
                        ca = new HashMapCache(id);
                    ca.setFunction(function);
                    break;
                case GUAVA:
                    if (fresh) {
                        ca = BaseGuavaCache.build(id, cache.size(), function, interval, timeUnit, 10, TimeUnit.HOURS);
                    } else {
                        ca = BaseGuavaCache.build(id);
                    }
                    break;
                case CONCURRENT:
                    if (fresh) {
                        ca = new ConcurrentHashMapCache(id, interval, timeUnit);
                    } else
                        ca = new ConcurrentHashMapCache(id);
                    ca.setFunction(function);
                    break;
            }
            if (lru > 0) {
                ca = new LruCache(ca, lru);
                ca.setFunction(function);
            }
            if (cache.isLog()) {
                ca = new LoggingCache(ca);
                ca.setFunction(function);
            }
            CACHE_MAP.put(id, ca);
        }
        return ca;
    }


    public static Object createCacheKey(List<Object> args) {
        int i = 0;
        for (Object ar : args) {
            if (ar.getClass().isArray()) {
                i ^= Arrays.hashCode((Object[]) ar);
            } else {
                if (i == 0) {
                    i = ar.hashCode();
                } else {
                    i = (ar.hashCode() ^ i);
                }
            }
        }
        return i;

    }

    private static final DelayQueue<DelayItems> queue = new DelayQueue<>();

    static {
        new Thread(() -> {
            try {
                DelayItems delayItems = queue.take();
                Object o = delayItems.getKey();
                CacheManager.cache(delayItems.getId()).remove(o);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static DelayQueue<DelayItems> queue() {
        return queue;
    }
}
