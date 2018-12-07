package com.geek.commons.cache.decorders;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.impl.HashMapCache;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Description: geek-commons-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-25 15:47
 */

public class LoggingCacheTest {

    @Test
    public void put() {
        Cache cache=new HashMapCache("default");
        cache.put("1","1");
        Cache cache2=new LoggingCache(cache);
        cache2.put("2","2");
        //
        assertEquals(2,cache2.size());

        assertTrue(cache.equals(cache2));
    }

    @Test
    public void getValue() {
        Cache cache=new LoggingCache(new HashMapCache("default"));
        for (int i=0;i<100;i++) {
            cache.put(i,i);
        }
        cache.getValue(1);
        cache.getValue(10000);
        assertNotNull(cache);
    }
}