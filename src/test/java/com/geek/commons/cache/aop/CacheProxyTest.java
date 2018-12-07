package com.geek.commons.cache.aop;

import com.geek.BaseTest;
import com.geek.commons.cache.annotations.Cache;
import com.geek.commons.cache.annotations.Param;
import com.geek.commons.cache.enums.CacheType;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-29 16:06
 */

public class CacheProxyTest  extends BaseTest{

    static Logger logger = LoggerFactory.getLogger(CacheProxyTest.class);

    @Autowired
    Target target;

    @Component
    public static class Target {
        @Cache
        public String get(@Param String key) {
            return "Hello_world" + key + RandomUtils.nextInt();
        }

        @Cache(isLog = true)
        public String getLog(@Param String key) {
            return "Hello_world" + key + RandomUtils.nextInt();
        }

        @Cache(lru = 1)
        public String getLru(@Param String key) {
            return "Hello_world" + key + RandomUtils.nextInt();
        }

        @Cache(refresh = true, interval = 1, value = CacheType.GUAVA)
        public String refresh(@Param String key) {
            logger.warn("refresh execute.");
            return "Hello_world" + key + RandomUtils.nextInt();
        }

    }

    @Test
    public void get() throws InterruptedException {
        StopWatch stopWatch=StopWatch.createStarted();
        for (int i=0;i<10000;i++){
            String res = target.get("普通调用");
            String res2 = target.get("普通调用" );
            assertEquals(res, res2);
        }
        stopWatch.stop();
        logger.info(stopWatch.getTime()+"");

//        assertEquals(res, res2);
    }

    @Test
    public void getLog() {
        String res = target.getLog("日志调用");
        String res2 = target.getLog("日志调用");
        assertEquals(res, res2);
    }

    @Test
    public void getLru() throws InterruptedException {
        String res = target.getLru("LRU");
        String res2 = target.getLru("LRU2");
        String res3 = target.getLru("LRU2");
        String res4 = target.getLru("LRU");
        assertEquals(res3, res2);
//        assertEquals(res, res2);

        assertNotEquals(res, res4);
    }

    @Test
    public void refresh() throws InterruptedException {
        String res = target.refresh("refresh");
        String res2 = target.refresh("refresh");
        assertEquals(res, res2);
        Thread.sleep(5000);
        String res3 = target.refresh("refresh");
        logger.warn("Res 1 " + res);
        logger.warn("Res 3 " + res3);
        assertNotEquals(res, res3);
    }


}