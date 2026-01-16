package cloud.catfish.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmsRoleUpdateParam {
    @NotBlank
    @Schema(title = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(title = "描述")
    private String description;

    @Schema(title = "启用状态：0->禁用；1->启用", example = "1")
    private Integer status;

    @Schema(title = "排序")
    private Integer sort;
}

