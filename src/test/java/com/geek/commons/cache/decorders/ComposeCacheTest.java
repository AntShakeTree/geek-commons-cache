package com.geek.commons.cache.decorders;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.impl.BaseGuavaCache;
import com.geek.commons.cache.impl.HashMapCache;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Description: geek-commons-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-29 17:03
 */

public class ComposeCacheTest {

    private ComposeCache cache;
    @Before
    public void setUp(){
        cache=  new ComposeCache("1",new LruCache(new HashMapCache("1"),1),BaseGuavaCache.build("1",(k)->refresh((String) k)));
    }

    @Test
    public void getValue() {
        String v=cache.getValue("1");
        String v2=cache.getValue("1");
        assertNotNull(v);
        assertEquals(v,v2);
    }

    @Test
    public void put() {
        cache.put("3","3");
        cache.put("4","4");
        Cache hash=cache.getCache(0);
        Cache guava=cache.getCache(1);
        assertNull(hash.getValue("3"));
        assertNotNull(guava.getValue("3"));
    }

    @Test
    public void remove() {

    }

    @Test
    public void remove1() {
    }

    private String refresh(String key){
        return  "Refresh"+RandomUtils.nextInt()+""+key;
    }
}