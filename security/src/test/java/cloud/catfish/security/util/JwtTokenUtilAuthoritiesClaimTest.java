package cloud.catfish.security.util;

import cloud.catfish.security.config.JwtConfig;
import cloud.catfish.security.config.SecurityProperties;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import javax.crypto.SecretKey;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtTokenUtilAuthoritiesClaimTest {
    @Test
    void generateToken_includesScopeClaim_whenUserHasAuthorities() {
        SecurityProperties.JwtProperties props = new SecurityProperties.JwtProperties(
                "Authorization",
                "Bearer ",
                "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
                60L,
                60L
        );

        JwtConfig jwtConfig = new JwtConfig();
        SecretKey secretKey = jwtConfig.jwtSecretKey(props);
        JwtDecoder jwtDecoder = jwtConfig.jwtDecoder(secretKey);
        JwtTokenUtil jwtTokenUtil = jwtConfig.jwtTokenUtil(props, jwtConfig.jwtEncoder(secretKey), jwtDecoder);

        User user = (User) User.withUsername("alice")
                .password("N/A")
                .authorities("p1", "p2")
                .build();

        String token = jwtTokenUtil.generateToken(user);
        Jwt jwt = jwtDecoder.decode(token);

        Object rawScope = jwt.getClaims().get("scope");
        assertThat(rawScope).isInstanceOf(String.class);
        assertThat(rawScope.toString().split("\\s+"))
                .containsExactlyInAnyOrder("p1", "p2");
    }
}
