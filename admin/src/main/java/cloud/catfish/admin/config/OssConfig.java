package cloud.catfish.admin.config;

import com.aliyun.oss.OSSClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OssConfig.OssProperties.class)
public class OssConfig {
    @Bean
    public OSSClient ossClient(OssProperties properties){
        return new OSSClient(properties.endpoint(), properties.accessKeyId(), properties.accessKeySecret());
    }

    @ConfigurationProperties(prefix = "aliyun.oss")
    public record OssProperties(
            String endpoint,
            String accessKeyId,
            String accessKeySecret,
            Policy policy,
            String maxSize,
            String callback,
            String bucketName,
            Dir dir
    ) {
        public record Policy(Integer expire) {}
        public record Dir(String prefix) {}
    }
}
