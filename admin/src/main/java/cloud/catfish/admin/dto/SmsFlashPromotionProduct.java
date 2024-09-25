package cloud.catfish.admin.dto;

import cloud.catfish.mbg.model.PmsProduct;
import cloud.catfish.mbg.model.SmsFlashPromotionProductRelation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 限时购商品信息封装
 * Created by macro on 2018/11/16.
 */
public class SmsFlashPromotionProduct extends SmsFlashPromotionProductRelation{
    @Getter
    @Setter
    @Schema(title = "关联商品")
    private PmsProduct product;
}
