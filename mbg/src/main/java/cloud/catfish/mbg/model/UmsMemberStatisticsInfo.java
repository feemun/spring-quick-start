package cloud.catfish.mbg.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
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
public class UmsMemberStatisticsInfo implements Serializable {
    private Long id;

    private Long memberId;

    @Schema(title = "累计消费金额")
    private BigDecimal consumeAmount;

    @Schema(title = "订单数量")
    private Integer orderCount;

    @Schema(title = "优惠券数量")
    private Integer couponCount;

    @Schema(title = "评价数")
    private Integer commentCount;

    @Schema(title = "退货数量")
    private Integer returnOrderCount;

    @Schema(title = "登录次数")
    private Integer loginCount;

    @Schema(title = "关注数量")
    private Integer attendCount;

    @Schema(title = "粉丝数量")
    private Integer fansCount;

    private Integer collectProductCount;

    private Integer collectSubjectCount;

    private Integer collectTopicCount;

    private Integer collectCommentCount;

    private Integer inviteFriendCount;

    @Schema(title = "最后一次下订单时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recentOrderTime;

    @Serial
    private static final long serialVersionUID = 1L;
}