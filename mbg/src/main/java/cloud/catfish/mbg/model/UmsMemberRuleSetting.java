package cloud.catfish.mbg.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class UmsMemberRuleSetting implements Serializable {
    private Long id;

    @Schema(title = "连续签到天数")
    private Integer continueSignDay;

    @Schema(title = "连续签到赠送数量")
    private Integer continueSignPoint;

    @Schema(title = "每消费多少元获取1个点")
    private BigDecimal consumePerPoint;

    @Schema(title = "最低获取点数的订单金额")
    private BigDecimal lowOrderAmount;

    @Schema(title = "每笔订单最高获取点数")
    private Integer maxPointPerOrder;

    @Schema(title = "类型：0->积分规则；1->成长值规则")
    private Integer type;

    @Serial
    private static final long serialVersionUID = 1L;
}