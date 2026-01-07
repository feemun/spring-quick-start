package cloud.catfish.security.config;

import cloud.catfish.security.component.DynamicAuthorizationManager;
import cloud.catfish.security.component.JwtAuthenticationTokenFilter;
import cloud.catfish.security.component.RestAuthenticationEntryPoint;
import cloud.catfish.security.component.RestfulAccessDeniedHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


/**
 * SpringSecurity相关配置，仅用于配置SecurityFilterChain
 * Created by macro on 2019/11/5.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final IgnoreUrlsConfig ignoreUrlsConfig;
    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    private final DynamicAuthorizationManager dynamicAuthorizationManager;

    public SecurityConfig(
            IgnoreUrlsConfig ignoreUrlsConfig,
            RestfulAccessDeniedHandler restfulAccessDeniedHandler,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter,
            ObjectProvider<DynamicAuthorizationManager> dynamicAuthorizationManager
    ) {
        this.ignoreUrlsConfig = ignoreUrlsConfig;
        this.restfulAccessDeniedHandler = restfulAccessDeniedHandler;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
        this.dynamicAuthorizationManager = dynamicAuthorizationManager.getIfAvailable();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(registry -> {
                    for (String url : ignoreUrlsConfig.getUrls()) {
                        registry.requestMatchers(url).permitAll();
                    }
                    registry.requestMatchers(HttpMethod.OPTIONS).permitAll();
                    registry.anyRequest().access(
                            dynamicAuthorizationManager == null
                                    ? AuthenticatedAuthorizationManager.authenticated()
                                    : dynamicAuthorizationManager
                    );
                })
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(restfulAccessDeniedHandler)
                        .authenticationEntryPoint(restAuthenticationEntryPoint))
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
