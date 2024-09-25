package cloud.catfish.admin.dto;

import cloud.catfish.mbg.model.OmsOrder;
import cloud.catfish.mbg.model.OmsOrderItem;
import cloud.catfish.mbg.model.OmsOrderOperateHistory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 订单详情信息
 * Created by macro on 2018/10/11.
 */
public class OmsOrderDetail extends OmsOrder {
    @Getter
    @Setter
    @Schema(title = "订单商品列表")
    private List<OmsOrderItem> orderItemList;
    @Getter
    @Setter
    @Schema(title = "订单操作记录列表")
    private List<OmsOrderOperateHistory> historyList;
}
