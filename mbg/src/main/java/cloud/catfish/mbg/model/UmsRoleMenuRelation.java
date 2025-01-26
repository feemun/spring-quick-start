package cloud.catfish.mbg.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UmsRoleMenuRelation implements Serializable {
    private Long id;

    @Schema(title = "角色ID")
    private Long roleId;

    @Schema(title = "菜单ID")
    private Long menuId;

    private static final long serialVersionUID = 1L;
}