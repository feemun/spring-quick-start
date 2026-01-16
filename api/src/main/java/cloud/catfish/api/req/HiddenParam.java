package cloud.catfish.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HiddenParam {
    @NotNull
    @Schema(title = "是否隐藏", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean hidden;
}

