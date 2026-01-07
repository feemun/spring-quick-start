package cloud.catfish.cache.service;

import cloud.catfish.cache.config.CacheCodec;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CacheService {

    private final StringRedisTemplate redis;
    private final CacheCodec codec;

    public CacheService(StringRedisTemplate redis, CacheCodec codec) {
        this.redis = redis;
        this.codec = codec;
    }

    public <T> void set(String key, T value, Duration ttl) {
        redis.opsForValue().set(key, codec.encode(value), ttl);
    }

    public <T> T get(String key, Class<T> type) {
        String json = redis.opsForValue().get(key);
        return json == null ? null : codec.decode(json, type);
    }

    public void delete(String key) {
        redis.delete(key);
    }
}

