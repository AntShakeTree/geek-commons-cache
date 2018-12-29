package com.geek.commons.cache.impl;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ConcurrentHashMapCacheTest {

    @Test
    public void put() throws InterruptedException {
        ConcurrentHashMapCache concurrentHashMapCache = new ConcurrentHashMapCache("id");
        concurrentHashMapCache.put("id", 1, 1, TimeUnit.SECONDS);
        System.out.println(concurrentHashMapCache.getValue("id").toString());
        Thread.sleep(2000);
        System.out.println(concurrentHashMapCache.getValue("id") + "");


    }
}