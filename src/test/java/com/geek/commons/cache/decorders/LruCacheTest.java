package com.geek.commons.cache.decorders;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.impl.HashMapCache;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @Description: geek-commons-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-25 15:16
 */

public class LruCacheTest {

    @Test
    public void put(){
        Cache lruCache=new LruCache(new HashMapCache("Default"),1);
        lruCache.put(1,1);
        lruCache.put(2,2);
        int s =lruCache.size();
        assertEquals(1,1);
    }

    @Test
    public void get(){
        Cache lruCache=new LruCache(new HashMapCache("Default"),1);
        lruCache.put(1,1);
        lruCache.put(1,2);
        int s =lruCache.size();
        assertEquals(1,1);
        int r=lruCache.getValue(1);
        assertEquals(2,r);
        assertEquals(lruCache,new HashMapCache("Default"));
    }

}