package cloud.catfish.security.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

public final class SecurityProperties {
    private SecurityProperties() {
    }

    @Validated
    @ConfigurationProperties(prefix = "jwt")
    public record JwtProperties(
            @NotBlank String tokenHeader,
            @NotBlank String tokenHead,
            @NotBlank String secret,
            @NotNull Long expiration
    ) {
    }
}

