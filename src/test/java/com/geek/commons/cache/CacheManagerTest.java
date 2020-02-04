package com.geek.commons.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.geek.commons.cache.enums.CacheType;
import com.geek.commons.cache.impl.ConcurrentHashMapCache;
import com.geek.commons.cache.impl.HashMapCache;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;

public class CacheManagerTest {

    @Test
    public void createComposeCache() {
        CacheManager.createComposeCache("id",new ConcurrentHashMapCache("id"),new HashMapCache("id"));
    }

    @Test
    public void createLocalCache() {
        CacheManager.createLocalCache("id", CacheType.GUAVA);

    }

    @Test
    public void createRedisCache() {
        RedisTemplate redisTemplate=new RedisTemplate();
        CacheManager.createRedisCache("id",redisTemplate);
    }
    @Getter
    @Setter
    static class Response<T>{
        T body;
    }
    @Test
    public void parseJson(){
//
//        Response<Integer> response=JSON.parseObject("{\"body\":5}",Response.class);
//       JSONObject response1=JSON.parseObject("{\"body\":5}");
//        Response<Object> response2=JSON.parseObject("{\"body\":\"5\"}",Response.class);
        Response<Object> response3=JSON.parseObject("{\"body\":null}",Response.class);


//        System.out.println(response2.getBody());
//        System.out.println(response3.getBody());

    }


}