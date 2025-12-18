package cloud.catfish.common.config;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 全局 Jackson 3 配置
 * <p>
 * Spring Boot 4.0 默认使用 Jackson 3 (tools.jackson)。
 * </p>
 */
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper jacksonJsonMapper() {
        return JsonMapper.builder()
                // 忽略 JSON 中存在但 Java 对象中不存在的字段
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
    }
}
