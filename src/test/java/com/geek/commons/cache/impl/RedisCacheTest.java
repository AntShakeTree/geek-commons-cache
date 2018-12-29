//package com.geek.commons.cache.impl;
//
//import com.geek.commons.cache.EnableCacheProxy;
//import org.assertj.core.api.Assertions;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.test.context.junit4.SpringRunner;
//@EnableAspectJAutoProxy
//@SpringBootTest
//@RunWith(SpringRunner.class)
//@EnableCacheProxy
//public class RedisCacheTest {
//    @Autowired
//    RedisTemplate<String,Object> redisTemplate;
//
//
//    @Test
//    public void getValue() {
//        RedisCache redisCache=new RedisCache(redisTemplate,"1");
//
//        redisCache.put(1,1);
//        redisCache.put(1,1);
//        Integer i=redisCache.getValue(1);
//        Assertions.assertThat(i).isEqualTo(1);
//    }
//
//    @Test
//    public void put() {
//    }
//}