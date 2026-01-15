package cloud.catfish.security.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;

@Configuration
@ConditionalOnBean(UserDetailsService.class)
public class JwtAuthenticationConverterConfig {
    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthenticationConverter(UserDetailsService userDetailsService) {
        return new UserDetailsJwtAuthenticationConverter(userDetailsService);
    }
}

