package cloud.catfish.api.domain;

import java.io.Serial;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

@Data
public class UmsAdminRoleRelation implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long adminId;

    private Long roleId;

    @Serial
    private static final long serialVersionUID = 1L;
}