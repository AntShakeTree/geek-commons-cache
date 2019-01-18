package com.geek.commons.cache;

import com.geek.commons.cache.enums.CacheType;
import com.geek.commons.cache.impl.ConcurrentHashMapCache;
import com.geek.commons.cache.impl.HashMapCache;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;

public class CacheManagerTest {

    @Test
    public void createComposeCache() {
        CacheManager.createComposeCache("id",new ConcurrentHashMapCache("id"),new HashMapCache("id"));
    }

    @Test
    public void createLocalCache() {
        CacheManager.createLocalCache("id", CacheType.GUAVA);

    }

    @Test
    public void createRedisCache() {
        RedisTemplate redisTemplate=new RedisTemplate();
        CacheManager.createRedisCache("id",redisTemplate);
    }


}