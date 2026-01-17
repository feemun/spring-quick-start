package cloud.catfish.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

import lombok.*;

@Data
public class UmsMemberTag implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String name;

    @Schema(title = "自动打标签完成订单数量")
    private Integer finishOrderCount;

    @Schema(title = "自动打标签完成订单金额")
    private BigDecimal finishOrderAmount;

    @Serial
    private static final long serialVersionUID = 1L;
}