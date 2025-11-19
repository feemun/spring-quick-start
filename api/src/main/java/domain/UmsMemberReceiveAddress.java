package domain;

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
public class UmsMemberReceiveAddress implements Serializable {
    private Long id;

    private Long memberId;

    @Schema(title = "收货人名称")
    private String name;

    private String phoneNumber;

    @Schema(title = "是否为默认")
    private Integer defaultStatus;

    @Schema(title = "邮政编码")
    private String postCode;

    @Schema(title = "省份/直辖市")
    private String province;

    @Schema(title = "城市")
    private String city;

    @Schema(title = "区")
    private String region;

    @Schema(title = "详细地址(街道)")
    private String detailAddress;

    @Serial
    private static final long serialVersionUID = 1L;
}