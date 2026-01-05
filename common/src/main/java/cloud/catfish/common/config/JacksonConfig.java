package cloud.catfish.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 全局 Jackson 2 配置
 * <p>
 * Spring Boot 4.0 默认使用 Jackson 3 (tools.jackson)。
 * 本项目为了兼容现有的 DTO 注解和 ES 客户端，采用“共存”策略：
 * 1. 保留 Jackson 3 依赖（供 Spring Boot 内部使用）。
 * 2. 手动配置 Jackson 2 Bean（供业务代码和第三方库使用）。
 * 3. 配置 Spring MVC 优先使用 Jackson 2 处理 HTTP 请求。
 * </p>
 */
@Configuration
public class JacksonConfig implements WebMvcConfigurer {

    @Bean("customJacksonObjectMapper")
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // 注册 JavaTimeModule 以支持 LocalDateTime
        mapper.registerModule(new JavaTimeModule());
        // 禁用将日期序列化为时间戳
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 忽略 JSON 中存在但 Java 对象中不存在的字段
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * 配置 Spring MVC 使用 Jackson 2
     * 将 Jackson 2 转换器添加到列表首位，确保优先级高于默认的 Jackson 3 转换器
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(0, new MappingJackson2HttpMessageConverter(objectMapper()));
    }
}
