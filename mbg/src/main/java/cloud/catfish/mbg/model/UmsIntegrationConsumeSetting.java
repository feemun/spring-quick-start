package cloud.catfish.mbg.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
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
public class UmsIntegrationConsumeSetting implements Serializable {
    private Long id;

    @Schema(title = "每一元需要抵扣的积分数量")
    private Integer deductionPerAmount;

    @Schema(title = "每笔订单最高抵用百分比")
    private Integer maxPercentPerOrder;

    @Schema(title = "每次使用积分最小单位100")
    private Integer useUnit;

    @Schema(title = "是否可以和优惠券同用；0->不可以；1->可以")
    private Integer couponStatus;

    @Serial
    private static final long serialVersionUID = 1L;
}