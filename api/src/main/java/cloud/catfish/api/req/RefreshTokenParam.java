package cloud.catfish.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RefreshTokenParam {
    @Schema(name = "")
    private String refreshToken;
}

