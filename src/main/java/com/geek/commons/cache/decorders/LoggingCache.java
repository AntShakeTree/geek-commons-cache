package com.geek.commons.cache.decorders;

import com.geek.commons.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * @Description: geek-data-platform
 * @Author: Captain.Ma
 * @Date: 2018-10-25 15:17
 */
public class LoggingCache implements Cache {

    protected final Cache delegate;
    protected int requests = 0;
    protected int hits = 0;
    Logger logger = LoggerFactory.getLogger(LoggingCache.class);


    public LoggingCache(final Cache cache) {
        this.delegate = cache;
    }

    /**
     * @return The identifier of this cache
     */
    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public <K, V> V getValue(K key) {
        V v = delegate.getValue(key);
        requests++;
        if (v != null) {
            hits++;

        }
        logger.info("Cache " + this.getId() + " Hit Ratio [" + this.getHitRatio() + "]");
        return v;
    }

    @Override
    public void args(Object... params) {
        delegate.args(params);
    }

    @Override
    public Object[] args() {
        return delegate.args();
    }

    @Override
    public <K, V> void put(K key, V value) {
        delegate.put(key, value);
    }

    /**
     * Clears this cache instance
     */
    @Override
    public void clear() {
        this.delegate.clear();
    }


    /**
     * @param key The key
     */
    @Override
    public void remove(Object key) {
        this.delegate.remove(key);
    }

    /**
     * The number of elements stored in the cache (not its capacity).
     *
     * @return
     */
    @Override
    public int size() {
        return this.delegate.size();
    }


    @Override
    public Function refresh() {
        return delegate.refresh();
    }

    @Override
    public void setRefresh(Function function) {
        delegate.setRefresh(function);
    }

    @Override
    public boolean contain(Object key) {
        return this.delegate.contain(key);
    }

    private double getHitRatio() {
        return (double) hits / (double) requests;
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
