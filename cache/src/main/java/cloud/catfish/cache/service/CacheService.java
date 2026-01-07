package cloud.catfish.cache.service;

import cloud.catfish.cache.config.CacheCodec;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.Collection;
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

    public <T> T get(String key, TypeReference<T> typeRef) {
        String json = redis.opsForValue().get(key);
        return json == null ? null : codec.decode(json, typeRef);
    }

    public void delete(String key) {
        redis.delete(key);
    }

    public void delete(Iterable<String> keys) {
        if (keys instanceof Collection<String> collection) {
            redis.delete(collection);
            return;
        }
        ArrayList<String> list = new ArrayList<>();
        for (String key : keys) {
            list.add(key);
        }
        redis.delete(list);
    }
}

