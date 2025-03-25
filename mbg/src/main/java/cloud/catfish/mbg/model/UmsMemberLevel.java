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
public class UmsMemberLevel implements Serializable {
    private Long id;

    private String name;

    private Integer growthPoint;

    @Schema(title = "是否为默认等级：0->不是；1->是")
    private Integer defaultStatus;

    @Schema(title = "免运费标准")
    private BigDecimal freeFreightPoint;

    @Schema(title = "每次评价获取的成长值")
    private Integer commentGrowthPoint;

    @Schema(title = "是否有免邮特权")
    private Integer priviledgeFreeFreight;

    @Schema(title = "是否有签到特权")
    private Integer priviledgeSignIn;

    @Schema(title = "是否有评论获奖励特权")
    private Integer priviledgeComment;

    @Schema(title = "是否有专享活动特权")
    private Integer priviledgePromotion;

    @Schema(title = "是否有会员价格特权")
    private Integer priviledgeMemberPrice;

    @Schema(title = "是否有生日特权")
    private Integer priviledgeBirthday;

    private String note;

    @Serial
    private static final long serialVersionUID = 1L;
}