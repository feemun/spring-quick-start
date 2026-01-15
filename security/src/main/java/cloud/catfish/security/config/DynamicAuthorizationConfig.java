package cloud.catfish.security.config;

import cloud.catfish.security.component.DynamicAuthorizationManager;
import cloud.catfish.security.component.DynamicSecurityMetadataSource;
import cloud.catfish.security.component.DynamicSecurityService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(DynamicSecurityService.class)
public class DynamicAuthorizationConfig {
    @Bean
    public DynamicSecurityMetadataSource dynamicSecurityMetadataSource(DynamicSecurityService dynamicSecurityService) {
        return new DynamicSecurityMetadataSource(dynamicSecurityService);
    }

    @Bean
    public DynamicAuthorizationManager dynamicAuthorizationManager(DynamicSecurityMetadataSource securityDataSource, IgnoreUrlsConfig ignoreUrlsConfig) {
        return new DynamicAuthorizationManager(securityDataSource, ignoreUrlsConfig);
    }
}

