package cloud.catfish.security.config;

import cloud.catfish.security.component.DynamicAuthorizationManager;
import cloud.catfish.security.component.RestAuthenticationEntryPoint;
import cloud.catfish.security.component.RestfulAccessDeniedHandler;
import org.springframework.security.config.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;


/**
 * SpringSecurity相关配置，仅用于配置SecurityFilterChain
 */
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

    @Bean
    @ConditionalOnBean(DynamicAuthorizationManager.class)
    SecurityFilterChain filterChainDynamic(
            HttpSecurity httpSecurity,
            IgnoreUrlsConfig ignoreUrlsConfig,
            RestfulAccessDeniedHandler restfulAccessDeniedHandler,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            DynamicAuthorizationManager dynamicAuthorizationManager
    ) throws Exception {
        httpSecurity
                .authorizeHttpRequests(registry -> {
                    for (String url : ignoreUrlsConfig.urls()) {
                        registry.requestMatchers(PathPatternRequestMatcher.pathPattern(url)).permitAll();
                    }
                    registry.requestMatchers(PathPatternRequestMatcher.pathPattern(HttpMethod.OPTIONS, "/**")).permitAll();
                    registry.anyRequest().access(dynamicAuthorizationManager);
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(restfulAccessDeniedHandler)
                        .authenticationEntryPoint(restAuthenticationEntryPoint))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restfulAccessDeniedHandler)
                );
        return httpSecurity.build();
    }

    @Bean
    @ConditionalOnMissingBean(DynamicAuthorizationManager.class)
    SecurityFilterChain filterChain(
            HttpSecurity httpSecurity,
            IgnoreUrlsConfig ignoreUrlsConfig,
            RestfulAccessDeniedHandler restfulAccessDeniedHandler,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint
    ) throws Exception {
        httpSecurity
                .authorizeHttpRequests(registry -> {
                    for (String url : ignoreUrlsConfig.urls()) {
                        registry.requestMatchers(PathPatternRequestMatcher.pathPattern(url)).permitAll();
                    }
                    registry.requestMatchers(PathPatternRequestMatcher.pathPattern(HttpMethod.OPTIONS, "/**")).permitAll();
                    registry.anyRequest().access(AuthenticatedAuthorizationManager.authenticated());
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(restfulAccessDeniedHandler)
                        .authenticationEntryPoint(restAuthenticationEntryPoint))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restfulAccessDeniedHandler)
                );
        return httpSecurity.build();
    }
}
