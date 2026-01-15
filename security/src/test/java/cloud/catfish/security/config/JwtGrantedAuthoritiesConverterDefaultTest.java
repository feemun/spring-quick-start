package cloud.catfish.security.config;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtGrantedAuthoritiesConverterDefaultTest {
    @Test
    void defaultConverter_extractsScopeAndPrefixesWithScopeUnderscore() {
        Jwt jwt = Jwt.withTokenValue("t")
                .header("alg", "none")
                .subject("alice")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .claim("scope", "p1 p2")
                .build();

        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        List<String> authorities = converter.convert(jwt).stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertThat(authorities).containsExactlyInAnyOrder("SCOPE_p1", "SCOPE_p2");
    }
}

