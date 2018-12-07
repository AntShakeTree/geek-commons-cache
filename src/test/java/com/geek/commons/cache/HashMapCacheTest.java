package com.geek.commons.cache;

import com.geek.commons.cache.impl.HashMapCache;
import org.junit.Test;

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
    public void size() {
        //do noting.
    }
}