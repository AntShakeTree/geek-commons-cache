package com.geek.commons.cache;

import com.geek.commons.cache.aops.CacheProxy;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @program: geek-commons-cache
 * @author: captain.ma
 * @date: 2018-11-21
 * @since:
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CacheProxy.class)
@Configuration
@EnableAspectJAutoProxy
@EnableRedisRepositories
@EnableCaching
@ComponentScan
public @interface EnableCacheProxy {
}
