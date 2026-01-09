package cloud.catfish.cache.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.core.env.Environment;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisSerializer<Object> redisSerializer(ObjectMapper objectMapper) {
        return new GenericJacksonJsonRedisSerializer(objectMapper);
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory,
                                               RedisSerializer<Object> redisSerializer,
                                               Environment environment) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        long ttlSeconds = environment.getProperty("redis.expire.common", Long.class, 86400L);
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer.UTF_8))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(ttlSeconds));
        return RedisCacheManager.builder(redisCacheWriter)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(Environment environment) {
        Config config = new Config();
        String host = environment.getProperty("spring.data.redis.host", "localhost");
        Integer port = environment.getProperty("spring.data.redis.port", Integer.class, 6379);
        Integer database = environment.getProperty("spring.data.redis.database", Integer.class, 0);
        String password = environment.getProperty("spring.data.redis.password");

        String address = "redis://" + host + ":" + port;
        var singleServerConfig = config.useSingleServer().setAddress(address);

        if (password != null && !password.isBlank()) {
            singleServerConfig.setPassword(password);
        }

        singleServerConfig.setDatabase(database);

        return Redisson.create(config);
    }
}
