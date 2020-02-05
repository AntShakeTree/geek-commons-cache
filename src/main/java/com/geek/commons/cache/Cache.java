package com.geek.commons.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-24 20:19
 */
public interface Cache {

    Map<Object, Object> paramsMap = new ConcurrentHashMap<>();

    /**
     * @return The identifier of this cache
     */
    String getId();

    public <K, V> V getValue(K key);

    default void args(Object key, Object[] params) {
        paramsMap.put(key, params);
    }

    default Object args(Object key) {
        if (paramsMap.get(key) == null) {
            return key;
        } else {
            return paramsMap.get(key);
        }
    }


    public <K, V> void put(K key, V value);

    public default <K, V> void put(K key, V value, long times, TimeUnit timeUnit) {
        this.put(key, value);
    }

    /**
     * Clears this cache instance
     */
    void clear();

    /**
     * @param key The key
     */
    void remove(Object key);

    /**
     * The number of elements stored in the cache (not its capacity).
     *
     * @return
     */
    int size();
//
//    /**
//     * Sets the set of parameters needed for the calculation
//     *
//     * @param o
//     * @return
//     */
//    Object params(Object o);

    public Function refreshFu();

    public void setRefreshFu(Function function);


    public default <K, V> void putIfAbsent(K key, V value) {
        if (getValue(key) == null) {
            this.put(key, value);
        }
    }

    public default Object refresh(java.util.function.Function function, Object key) {

        return function.apply(paramsMap.get(key));
    }

    boolean contain(Object key);
}
