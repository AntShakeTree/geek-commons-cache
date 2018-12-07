package com.geek.commons.cache.annotations;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-27 16:28
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface Param {
}
