## 目标

1:方便快捷进行操作Cache
2: 屏蔽底层Cache实现
3: 实现多种灵活的层级Cache功能
4: 支持功能定制化


https://oneship.yuque.com/platform/hosuys/rmv8to

### Cache 

#### HashMapCache 底层用的是HashMap存储

``java
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

``


#### BaseGuavaCacheTest 底层用的是BaseGuavaC存储（自动在后台刷新Key）

``
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
        baseGuavaCache2.put("2", "2");
        baseGuavaCache2.put("3", "2");
        String a=baseGuavaCache2.getValue("2");
        Thread.sleep(2000);
        String a2=baseGuavaCache2.getValue("2");
        String a3=baseGuavaCache2.getValue("3");
        String a4=baseGuavaCache2.getValue("2");
        assertNotEquals("2", baseGuavaCache2.getValue("2"));
        assertNotNull(baseGuavaCache2.getValue("2"));
    }



    @Test
    public void expireCache() throws InterruptedException {
        //do noting
    }

    private String refreshKey(String key) {
        return key + ":" + count++;
    }
}
``

#### CONCURRENT 底层用的是CONCURRENTHashMap存储

``
略，用法同 HashMapCache
``

#### LRU

``
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
``

#### LoggingCache 记录命中率

``
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
``

#### Aop

#### 每个方法相当于一个Cache，默认情况，所有参数作为Key 

**1：如果方法参数的类型是 Object 则必须要重写HashCode 与Equase 方法**
**2：@Param 注解代码用哪个参数作为key**
**3：其它项目引用：@Import(CacheProxy.class)**

```$xslt
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
        public String refresh(String key) {
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
```
