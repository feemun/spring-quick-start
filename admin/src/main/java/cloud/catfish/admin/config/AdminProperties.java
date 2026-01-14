package cloud.catfish.admin.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

public final class AdminProperties {
    private AdminProperties() {
    }

    @ConfigurationProperties(prefix = "jwt")
    public record JwtProperties(
            String tokenHeader,
            String tokenHead,
            String secret,
            Long expiration
    ) {
    }

    @ConfigurationProperties(prefix = "redis")
    public record RedisProperties(
            String database,
            Key key,
            Expire expire
    ) {
        public record Key(
                String admin,
                String resourceList
        ) {
        }

        public record Expire(
                Long common
        ) {
        }
    }

    @ConfigurationProperties(prefix = "minio")
    public record MinioProperties(
            String endpoint,
            String bucketName,
            String accessKey,
            String secretKey
    ) {
    }
}

