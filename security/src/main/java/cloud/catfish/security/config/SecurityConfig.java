package cloud.catfish.security.config;

import cloud.catfish.security.component.DynamicAuthorizationManager;
import cloud.catfish.security.component.RestAuthenticationEntryPoint;
import cloud.catfish.security.component.RestfulAccessDeniedHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.oauth2.jwt.Jwt;


/**
 * SpringSecurity相关配置，仅用于配置SecurityFilterChain
 * Created by macro on 2019/11/5.
 */
@Configuration
public class SecurityConfig {

    private final IgnoreUrlsConfig ignoreUrlsConfig;
    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final DynamicAuthorizationManager dynamicAuthorizationManager;
    private final Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter;

    public SecurityConfig(
            IgnoreUrlsConfig ignoreUrlsConfig,
            RestfulAccessDeniedHandler restfulAccessDeniedHandler,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            ObjectProvider<Converter<Jwt, ? extends AbstractAuthenticationToken>> jwtAuthenticationConverter,
            ObjectProvider<DynamicAuthorizationManager> dynamicAuthorizationManager
    ) {
        this.ignoreUrlsConfig = ignoreUrlsConfig;
        this.restfulAccessDeniedHandler = restfulAccessDeniedHandler;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter.getIfAvailable();
        this.dynamicAuthorizationManager = dynamicAuthorizationManager.getIfAvailable();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(registry -> {
                    for (String url : ignoreUrlsConfig.urls()) {
                        registry.requestMatchers(PathPatternRequestMatcher.pathPattern(url)).permitAll();
                    }
                    registry.requestMatchers(PathPatternRequestMatcher.pathPattern(HttpMethod.OPTIONS, "/**")).permitAll();
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
                .oauth2ResourceServer(oauth2 -> {
                    oauth2
                            .authenticationEntryPoint(restAuthenticationEntryPoint)
                            .accessDeniedHandler(restfulAccessDeniedHandler);
                    oauth2.jwt(jwt -> {
                        if (jwtAuthenticationConverter != null) {
                            jwt.jwtAuthenticationConverter(jwtAuthenticationConverter);
                        }
                    });
                });
        return httpSecurity.build();
    }

}
