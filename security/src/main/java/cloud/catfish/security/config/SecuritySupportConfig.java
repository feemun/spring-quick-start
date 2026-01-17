package cloud.catfish.security.config;

import cloud.catfish.security.component.RestAuthenticationEntryPoint;
import cloud.catfish.security.component.RestfulAccessDeniedHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tools.jackson.databind.ObjectMapper;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({IgnoreUrlsConfig.class, SecurityProperties.JwtProperties.class})
public class SecuritySupportConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestfulAccessDeniedHandler restfulAccessDeniedHandler(ObjectMapper objectMapper) {
        return new RestfulAccessDeniedHandler(objectMapper);
    }

    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint(ObjectMapper objectMapper) {
        return new RestAuthenticationEntryPoint(objectMapper);
    }
}

