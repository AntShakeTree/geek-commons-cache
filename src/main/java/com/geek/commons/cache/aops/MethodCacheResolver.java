package com.geek.commons.cache.aops;

import com.geek.commons.cache.Cache;
import com.geek.commons.cache.CacheException;
import com.geek.commons.cache.CacheManager;
import com.geek.commons.cache.annotations.Param;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-29 12:01
 */
class MethodCacheResolver {
    private final ProceedingJoinPoint proceedingJoinPoint;
    private Object[] args;
    private final Method method;
    private Cache cache;
    private Object key;
    private final String id;
    private final String methodName;

    private static Map<String, CacheParamsIndex> cacheParamsIndexMap = new ConcurrentHashMap<>(256);
    private static Map<String, MethodCacheResolver> methodMethodCacheMapperMap = new ConcurrentHashMap<>(256);

    private static String id(ProceedingJoinPoint proceedingJoinPoint) {
//        proceedingJoinPoint.get
        return proceedingJoinPoint.getTarget().getClass().getName() + "#" + proceedingJoinPoint.getSignature().getName();
    }


    public static MethodCacheResolver create(ProceedingJoinPoint proceedingJoinPoint) {
        if (methodMethodCacheMapperMap.get(id(proceedingJoinPoint)) != null) {
            MethodCacheResolver resolver=methodMethodCacheMapperMap.get(id(proceedingJoinPoint)).args(proceedingJoinPoint.getArgs());

            return resolver;
        } else {
            methodMethodCacheMapperMap.put(id(proceedingJoinPoint), new MethodCacheResolver(proceedingJoinPoint));
            return methodMethodCacheMapperMap.get(id(proceedingJoinPoint));
        }

    }

    private MethodCacheResolver args(Object[] args) {
        this.args = args;
        return this;
    }

    protected MethodCacheResolver(ProceedingJoinPoint proceedingJoinPoint) {
        this.proceedingJoinPoint = proceedingJoinPoint;
        this.args = proceedingJoinPoint.getArgs();
        try {
            method = proceedingJoinPoint.getTarget().getClass().getMethod(proceedingJoinPoint.getSignature().getName(), types());
        } catch (NoSuchMethodException e) {
            throw new CacheException("方法获取失败", e);
        }
        id = id(proceedingJoinPoint);
        methodName = proceedingJoinPoint.getSignature().getName();
//        this.cacheAnnotation = cache;
    }

    public Cache cache(Function function, com.geek.commons.cache.annotations.Cache cache) {
        com.geek.commons.cache.Cache ca = CacheManager.createCache(id, cache, function);
//        ca.params(args);
        this.cache = ca;
        return this.cache;
    }


    public Object cacheKey() {
        this.key = CacheManager.createCacheKey(params());
        return this.key;
    }

    //
    private List params() {
        //=======
        CacheParamsIndex cacheParamsIndex = cacheParamsIndexMap.get(methodName);
        if (cacheParamsIndex != null) {
            return cacheParamsIndex.args(args);
        }
        cacheParamsIndex = new CacheParamsIndex();
        Annotation[][] annotations = method.getParameterAnnotations();
        List params = Lists.newArrayList();
        boolean isPr = false;
        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                if (annotation.annotationType() == Param.class) {
                    params.add(args[i]);
                    cacheParamsIndex.add(i);
                    isPr = true;
                }
            }
        }
        if (!isPr) {
            for (int i = 0; i < annotations.length; i++) {
                params.add(args[i]);
                cacheParamsIndex.add(i);
            }
        }
        cacheParamsIndexMap.put(methodName, cacheParamsIndex);
        return params;
    }

    public Object proceed() throws Throwable {
        Object re = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        this.cache.put(this.key, re);
        return re;
    }

    private Class[] types() {
        Class<?>[] argsc = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsc[i] = args[i].getClass();
        }
        return argsc;
    }

    private static class CacheParamsIndex {
        private List<Integer> integers = new ArrayList<>(8);

        public void add(int inx) {
            integers.add(inx);
        }

        public List<Object> args(Object[] args) {
            return integers.stream().map(
                    integer -> args[integer]
            ).collect(Collectors.toList());
        }
    }


}
