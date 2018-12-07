package com.geek.commons.cache.aops;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geek.commons.cache.annotations.Cache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @Description: geek-data-platform   Aop
 * @Author: Captain.Ma
 * @Date: 2018-10-26 18:14
 */
@Aspect
@Component
@Configuration
@EnableRedisRepositories
public class CacheProxy {
    @Pointcut("@annotation(com.geek.commons.cache.annotations.Cache)")// the pointcut expression
    public void cachePointcut() {
    }

    @Value("${geek.redis.host:39.105.179.138}")
    private String host;
    @Value("${geek.redis.port:6379}")
    private int port;
    @Value("${geek.redis.password:jieke@123}")
    private String password;


    /**
     * 处理异常
     */
    ThreadLocal<Throwable> throwableThreadLocal = new ThreadLocal<>();

    @Around(value = "cachePointcut()&&@annotation(cache)", argNames = "proceedingJoinPoint,cache")
    public Object doAccessCheck(final ProceedingJoinPoint proceedingJoinPoint, Cache cache) throws Throwable {
        MethodCacheResolver mapper = MethodCacheResolver.create(proceedingJoinPoint);
        com.geek.commons.cache.Cache cac = mapper.cache((o) -> put(proceedingJoinPoint, o), cache);
        Object o = cac.getValue(mapper.cacheKey());
        /**
         * 必须要在执行方法后面,顺序不能乱。
         */
        if (throwableThreadLocal.get() != null) {
            throw throwableThreadLocal.get();
        }
        if (o != null) {
            return o;
        }
        return proceedingJoinPoint.proceed();
    }

    private Object put(ProceedingJoinPoint proceedingJoinPoint, Object o) {
        try {
            Object result = proceedingJoinPoint.proceed((Object[]) o);
            return result;
        } catch (Throwable throwable) {
            CacheProxy.this.throwableThreadLocal.set(throwable);
        }
        return null;
    }

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(host, port);
        standaloneConfiguration.setPassword(RedisPassword.of(password));
        return new LettuceConnectionFactory(standaloneConfiguration);
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {

//        RedisStandaloneConfiguration standaloneConfiguration=new RedisStandaloneConfiguration("39.105.179.138", 6379);
//        standaloneConfiguration.setPassword(RedisPassword.of("jieke@123"));
//        LettuceConnectionFactory lettuceConnectionFactory=new LettuceConnectionFactory(standaloneConfiguration);

        //
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
//        template.set
        return template;
    }


}
