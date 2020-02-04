package com.geek.commons.cache;

import com.geek.commons.cache.impl.BaseGuavaCache;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-25 10:56
 */

public class BaseGuavaCacheTest {

    private int count = 1;

    @Test
    public void put() {
        BaseGuavaCache baseGuavaCache = BaseGuavaCache.build("id");
        baseGuavaCache.put("1", "1");

        assertEquals("1", baseGuavaCache.getValue("1"));


    }

    @Test
    public void clear() {
        BaseGuavaCache baseGuavaCache = BaseGuavaCache.build("id");
        baseGuavaCache.put("1", "1");
        baseGuavaCache.clear();
        assertNull(baseGuavaCache.getValue("1"));
    }

    @Test
    public void autoFresh() throws InterruptedException {
        BaseGuavaCache baseGuavaCache2 = BaseGuavaCache.build("id", 10, (String k) -> refreshKey(k), 1, TimeUnit.SECONDS, 5, TimeUnit.SECONDS);
        baseGuavaCache2.put("1","1");
        Thread.sleep(2000);
        String a=baseGuavaCache2.getValue("1");
        System.out.println(a);
        String a2=baseGuavaCache2.getValue("2");
//        Thread.sleep(2000);
//        String a2=baseGuavaCache2.getValue("2");
//        String a3=baseGuavaCache2.getValue("3");
//        String a4=baseGuavaCache2.getValue("2");
//        assertNotEquals("2", baseGuavaCache2.getValue("2"));
//        assertNotNull(baseGuavaCache2.getValue("2"));
    }



    @Test
    public void expireCache() throws InterruptedException {
        //do noting
    }

    private String refreshKey(String key) {
        return key+" ===111";
    }
}