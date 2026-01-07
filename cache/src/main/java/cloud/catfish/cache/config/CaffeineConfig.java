package cloud.catfish.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine本地缓存配置
 */
@Configuration
@EnableCaching
public class CaffeineConfig {

    /**
     * 配置Caffeine缓存管理器
     * 使用 @Primary 标记为默认缓存管理器（如果需要优先使用本地缓存）
     * 或者可以通过 @Cacheable(cacheManager = "caffeineCacheManager") 指定使用
     */
    @Bean("caffeineCacheManager")
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setAllowNullValues(false);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(60, TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000));
        return cacheManager;
    }

    @Bean("twoLevelCacheManager")
    @Primary
    public CacheManager twoLevelCacheManager(
            @Qualifier("caffeineCacheManager") CacheManager caffeineCacheManager,
            @Qualifier("redisCacheManager") RedisCacheManager redisCacheManager
    ) {
        return new TwoLevelCacheManager(caffeineCacheManager, redisCacheManager);
    }

    static final class TwoLevelCacheManager implements CacheManager {
        private final CacheManager l1;
        private final CacheManager l2;

        TwoLevelCacheManager(CacheManager l1, CacheManager l2) {
            this.l1 = l1;
            this.l2 = l2;
        }

        @Override
        @Nullable
        public Cache getCache(String name) {
            Cache cache1 = l1.getCache(name);
            Cache cache2 = l2.getCache(name);
            if (cache1 == null) {
                return cache2;
            }
            if (cache2 == null) {
                return cache1;
            }
            return new TwoLevelCache(cache1, cache2);
        }

        @Override
        public Collection<String> getCacheNames() {
            LinkedHashSet<String> names = new LinkedHashSet<>();
            names.addAll(l1.getCacheNames());
            names.addAll(l2.getCacheNames());
            return names;
        }
    }

    static final class TwoLevelCache implements Cache {
        private final Cache l1;
        private final Cache l2;

        TwoLevelCache(Cache l1, Cache l2) {
            this.l1 = l1;
            this.l2 = l2;
        }

        @Override
        public String getName() {
            return l1.getName();
        }

        @Override
        public Object getNativeCache() {
            return l1.getNativeCache();
        }

        @Override
        @Nullable
        public ValueWrapper get(Object key) {
            ValueWrapper v1 = l1.get(key);
            if (v1 != null) {
                return v1;
            }
            ValueWrapper v2 = l2.get(key);
            if (v2 != null) {
                l1.put(key, v2.get());
            }
            return v2;
        }

        @Override
        @Nullable
        public <T> T get(Object key, @Nullable Class<T> type) {
            ValueWrapper wrapper = get(key);
            Object value = (wrapper != null ? wrapper.get() : null);
            if (value == null) {
                return null;
            }
            if (type != null && !type.isInstance(value)) {
                return null;
            }
            @SuppressWarnings("unchecked")
            T castValue = (T) value;
            return castValue;
        }

        @Override
        @Nullable
        public <T> T get(Object key, Callable<T> valueLoader) {
            ValueWrapper wrapper = get(key);
            if (wrapper != null) {
                @SuppressWarnings("unchecked")
                T cached = (T) wrapper.get();
                return cached;
            }
            try {
                T loaded = valueLoader.call();
                if (loaded != null) {
                    put(key, loaded);
                }
                return loaded;
            } catch (Exception e) {
                throw new ValueRetrievalException(key, valueLoader, e);
            }
        }

        @Override
        public void put(Object key, @Nullable Object value) {
            l2.put(key, value);
            l1.put(key, value);
        }

        @Override
        @Nullable
        public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
            ValueWrapper existing = l2.putIfAbsent(key, value);
            if (existing != null) {
                l1.put(key, existing.get());
                return existing;
            }
            return l1.putIfAbsent(key, value);
        }

        @Override
        public void evict(Object key) {
            l1.evict(key);
            l2.evict(key);
        }

        @Override
        public void clear() {
            l1.clear();
            l2.clear();
        }
    }
}
