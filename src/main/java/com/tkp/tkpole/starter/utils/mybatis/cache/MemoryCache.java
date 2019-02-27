package com.tkp.tkpole.starter.utils.mybatis.cache;

import com.google.common.cache.CacheBuilder;
import org.apache.ibatis.cache.Cache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Mybatis二级缓存的内存实现
 *
 * <p> 创建时间：2018/7/5
 *
 * @author guojy24
 * @version 1.0
 * */
public class MemoryCache implements Cache {

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject( Object key, Object value) {
        this.cache.put( key, value);
    }

    @Override
    public Object getObject( Object key) {
        return this.cache.getIfPresent( key);
    }

    @Override
    public Object removeObject( Object key) {
        this.cache.invalidate( key);
        return null;
    }

    @Override
    public void clear() {
        this.cache.cleanUp();
    }

    @Override
    public int getSize() {
        return (int)this.cache.size();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

    public MemoryCache(String id) {
        this.id = id;
    }

    //==== 华丽的分割线 === 私有资源

    private String id = null;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final com.google.common.cache.Cache<Object,Object> cache = CacheBuilder.newBuilder()
            .concurrencyLevel(10)
            .initialCapacity(10)
            .maximumSize(300)
            .expireAfterWrite( MemoryCache.EXPIRED, TimeUnit.SECONDS)
            .build();
    private static final int EXPIRED = 60;
}
