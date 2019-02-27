package com.tkp.tkpole.starter.utils.mybatis.cache;

import com.google.common.base.Charsets;
import com.tkyl.bigcache.BigCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Mybatis二级缓存的Redis实现
 * 
 * <p> 创建时间：2018/7/5
 * 
 * @author guojy24
 * @version 1.0
 * */
@Slf4j
public class RedisCache implements Cache {

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void putObject(Object key, Object value) {
        bigCache.setBytes( RedisCache.PREFIX + new String( redisSerializer.serialize( key), Charsets.UTF_8) , redisSerializer.serialize( value), RedisCache.EXPIRED);
    }

    @Override
    public Object getObject(Object key) {
        Optional<byte[]> optional = bigCache.getBytes( RedisCache.PREFIX + new String( redisSerializer.serialize( key), Charsets.UTF_8));
        return optional.isPresent() ? redisSerializer.deserialize( optional.get()) : null;
    }

    @Override
    public Object removeObject(Object key) {
        bigCache.del( RedisCache.PREFIX + new String( redisSerializer.serialize( key), Charsets.UTF_8));
        return null;
    }

    @Override
    public void clear() {
        bigCache.doInJedis( redis ->  { redis.del( redis.keys( RedisCache.PREFIX + "*").toArray( new String[]{})); });
    }

    @Override
    public int getSize() {
        return bigCache.doInJedis( redis -> { return redis.keys( RedisCache.PREFIX + "*").size(); });
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return this.readWriteLock;
    }

    public RedisCache(String id) {
        this.id = id;
    }

    //==== 华丽的分割线 === 私有资源

//    @Autowired

    private BigCache bigCache;
    private String id = null;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final RedisSerializer<Object> redisSerializer = new JdkSerializationRedisSerializer();
    private static final String PREFIX = "tkpole-util-mybatis";
    private static final int EXPIRED = 60;

}