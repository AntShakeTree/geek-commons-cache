package com.geek.commons.cache.decorders;

import com.geek.commons.cache.Cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-25 14:45
 */

public class LruCache implements Cache {

    private final Cache delegate;
    private Map<Object, Object> keyMap;
    private Object eldestKey;
    private Function function;
    

    public LruCache(Cache delegate, int maxSize) {
        this.delegate = delegate;
        setSize(maxSize);
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public int size() {
        return delegate.size();
    }


    @Override
    public Function refresh() {
        return function;
    }

    @Override
    public void setRefresh(Function function) {
        this.function = function;
        delegate.setRefresh(null);
    }

    public void setSize(final int size) {
        keyMap = new LinkedHashMap<Object, Object>(size, .75F, true) {
            private static final long serialVersionUID = 4267176411845948333L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                boolean tooBig = size() > size;
                if (tooBig) {
                    eldestKey = eldest.getKey();
                }
                return tooBig;
            }
        };
    }

    @Override
    public <K, V> void put(K key, V value) {
        delegate.put(key, value);
        cycleKeyList(key);
    }

    @Override
    public <K, V> V getValue(K key) {
        V v = delegate.getValue(key);
        if (v != null) {
            return v;
        }
        return delegate.getValue(key);
    }

    @Override
    public void remove(Object key) {
        delegate.remove(key);
    }

    @Override
    public void clear() {
        delegate.clear();
        keyMap.clear();
    }


    private void cycleKeyList(Object key) {
        keyMap.put(key, key);
        if (eldestKey != null) {
            delegate.remove(eldestKey);
            eldestKey = null;
        }
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LoggingCache) {
            return delegate.equals(((LoggingCache) obj).delegate);
        }
        return delegate.equals(obj);
    }
}
