package domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UmsPermission implements Serializable {
    private Long id;

    @Schema(title = "父级权限id")
    private Long pid;

    @Schema(title = "名称")
    private String name;

    @Schema(title = "权限值")
    private String value;

    @Schema(title = "图标")
    private String icon;

    @Schema(title = "权限类型：0->目录；1->菜单；2->按钮（接口绑定权限）")
    private Integer type;

    @Schema(title = "前端资源路径")
    private String uri;

    @Schema(title = "启用状态；0->禁用；1->启用")
    private Integer status;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(title = "排序")
    private Integer sort;

    @Serial
    private static final long serialVersionUID = 1L;
}