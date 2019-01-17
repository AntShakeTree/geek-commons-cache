package com.geek.commons.cache;

import com.geek.commons.cache.impl.ConcurrentHashMapCache;
import com.geek.commons.cache.impl.HashMapCache;
import org.junit.Test;

public class CacheManagerTest {

    @Test
    public void createComposeCache() {
        CacheManager.createComposeCache("id",new ConcurrentHashMapCache("id"),new HashMapCache("id"));
    }
}