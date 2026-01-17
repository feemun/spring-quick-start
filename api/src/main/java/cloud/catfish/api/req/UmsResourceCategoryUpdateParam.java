package cloud.catfish.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmsResourceCategoryUpdateParam {
    @NotBlank
    @Schema(title = "分类名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(title = "排序")
    private Integer sort;
}

