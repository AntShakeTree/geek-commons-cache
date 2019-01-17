package com.geek.commons.cache;

import com.geek.commons.cache.decorders.ComposeCache;
import com.geek.commons.cache.decorders.LoggingCache;
import com.geek.commons.cache.decorders.LruCache;
import com.geek.commons.cache.enums.CacheType;
import com.geek.commons.cache.impl.BaseGuavaCache;
import com.geek.commons.cache.impl.ConcurrentHashMapCache;
import com.geek.commons.cache.impl.HashMapCache;
import com.geek.commons.cache.impl.RedisCache;
import com.google.common.base.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.data.redis.core.RedisTemplate;

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
@Slf4j
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


    public static com.geek.commons.cache.Cache createRedisCache(String id, RedisTemplate redisTemplate) {
        if (cache(id) != null && cache(id) instanceof RedisCache) {
            return cache(id);
        }
        Cache cache = new RedisCache(redisTemplate, id);
        CACHE_MAP.putIfAbsent(id, cache);
        return cache;
    }

    public static com.geek.commons.cache.Cache createComposeCache(String id, Cache... caches) {
        if (cache(id) != null && cache(id) instanceof ComposeCache) {
            return cache(id);
        }
        Cache cache = new ComposeCache(id, caches);
        CACHE_MAP.putIfAbsent(id, cache);
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
        new BasicThreadFactory.Builder().daemon(true).namingPattern("CACHE-MANAGER-DQ").uncaughtExceptionHandler((t, e) -> log.error(t.getName(), e)).build().newThread(
                () -> {
                    DelayItems delayItems = null;
                    try {
                        delayItems = queue.take();

                        Object o = delayItems.getKey();
                        CacheManager.cache(delayItems.getId()).remove(o);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }
        ).start();

    }

    public static DelayQueue<DelayItems> queue() {
        return queue;
    }
}
