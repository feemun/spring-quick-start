package cloud.catfish.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AdminLoginVO {

    @Schema(name = "")
    private String token;

    @Schema(name = "")
    private String tokenHead;

    @Schema(name = "")
    private String refreshToken;

}
