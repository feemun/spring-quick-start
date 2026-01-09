package cloud.catfish.cache.service;

import cloud.catfish.cache.config.CacheCodec;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

@Component
public class CacheService {

    private final CacheManager cacheManager;
    private final CacheCodec codec;

    public CacheService(CacheManager cacheManager, CacheCodec codec) {
        this.cacheManager = cacheManager;
        this.codec = codec;
    }

    public <T> void putObject(String cacheName, String key, T value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return;
        }
        cache.put(key, value);
    }

    public <T> T getObject(String cacheName, String key, Class<T> type) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        return cache.get(key, type);
    }

    public <T> void put(String cacheName, String key, T value) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return;
        }
        cache.put(key, codec.encode(value));
    }

    public <T> T get(String cacheName, String key, Class<T> type) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        String json = cache.get(key, String.class);
        return json == null ? null : codec.decode(json, type);
    }

    public <T> T get(String cacheName, String key, TypeReference<T> typeRef) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return null;
        }
        String json = cache.get(key, String.class);
        return json == null ? null : codec.decode(json, typeRef);
    }

    public void evict(String cacheName, String key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return;
        }
        cache.evict(key);
    }

    public void evictAll(String cacheName, Iterable<String> keys) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        for (String key : keys) {
            list.add(key);
        }
        for (String key : list) {
            cache.evict(key);
        }
    }
}

