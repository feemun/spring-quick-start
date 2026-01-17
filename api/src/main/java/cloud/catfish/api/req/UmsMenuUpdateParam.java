package cloud.catfish.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmsMenuUpdateParam {
    @NotBlank
    @Schema(title = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(title = "前端名称")
    private String name;

    @Schema(title = "前端图标")
    private String icon;

    @Schema(title = "前端隐藏", description = "true=隐藏,false=显示")
    private Boolean hidden;

    @Schema(title = "菜单排序")
    private Integer sort;
}

