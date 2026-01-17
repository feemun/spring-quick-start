package cloud.catfish.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmsResourceUpdateParam {
    @NotNull
    @Schema(title = "资源分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @NotBlank
    @Schema(title = "资源名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank
    @Schema(title = "资源路径", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    @Schema(title = "描述")
    private String description;
}

