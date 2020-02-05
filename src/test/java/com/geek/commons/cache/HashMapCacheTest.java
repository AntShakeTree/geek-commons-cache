package com.geek.commons.cache;

import com.geek.commons.cache.impl.ConcurrentHashMapCache;
import com.geek.commons.cache.impl.HashMapCache;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-25 11:49
 */

public class HashMapCacheTest {

    @Test
    public void getValue() {
        //do noting
    }

    @Test
    public void put() {
        HashMapCache cache = new HashMapCache("id");
        cache.put("1", "1");
        assertEquals("1", cache.getValue("1"));
    }

    @Test
    public void clear() {
        HashMapCache cache = new HashMapCache("id");
        cache.put("1", "1");
        cache.clear();
        assertNull(cache.getValue("1"));
        assertEquals(0, cache.size());
    }

    @Test
    public void remove() {
        HashMapCache cache = new HashMapCache("id");
        cache.put("1", "1");
        cache.clear();
        assertNull(cache.getValue("1"));
    }

    @Test
    public void refreshTest() throws InterruptedException {

        ConcurrentHashMapCache cache = new ConcurrentHashMapCache("id");
        cache.setRefreshFu(k->refresh((String) k));
        cache.put("1","1",1, TimeUnit.SECONDS);
        System.out.println("v: "+(String) cache.getValue("1"));
        Thread.sleep(2000);
        System.out.println("rv:::::"+(String) cache.getValue("1"));

    }
    public String refresh(String key) {
        return key+"======";
    }

    @Test
    public void size() {
        //do noting.
    }
}