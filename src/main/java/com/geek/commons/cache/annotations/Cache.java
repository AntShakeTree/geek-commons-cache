package com.geek.commons.cache.annotations;

import com.geek.commons.cache.enums.CacheType;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-26 18:20
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Cache {

    /**
     * 是否刷新。
     * 如果值为True，则会在 @code interval() @timeUnit() 后进行刷新
     *
     * @return
     */
    boolean refresh() default false;

    /**
     * 初始化cache大小
     *
     * @return
     */
    int size() default 16;


    int interval() default 10;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 是否输出日志
     *
     * @return
     */
    boolean isLog() default false;

    /**
     * Lru
     *
     * @return
     */
    int lru() default 0;

    /**
     * 默认是Hashmap ，如果定时刷新GUAVA，不建议在注解方法里使用redis。
     *
     * @return
     */
    CacheType value() default CacheType.HASH;


}
