package cloud.catfish.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;

import lombok.*;

@Data
public class UmsRoleResourceRelation implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(title = "角色ID")
    private Long roleId;

    @Schema(title = "资源ID")
    private Long resourceId;

    @Serial
    private static final long serialVersionUID = 1L;
}