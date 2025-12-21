package cloud.catfish.api.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XlcSignalDataStatus implements Serializable {
    @Schema(title = "设备唯一端口标识（与无人机 uav_port 关联）")
    private Long uavPort;

    @Schema(title = "信号方位角（0~359°，正北为0°，顺时针）")
    private Double azimuth;

    @Schema(title = "信号频率（单位：Hz）")
    private Double frequency;

    @Schema(title = "信号采集点纬度")
    private Double latitude;

    @Schema(title = "信号采集点经度")
    private Double longitude;

    @Schema(title = "信号采集设备俯仰角（单位：度）")
    private Double pitch;

    @Schema(title = "信号功率（单位：dBm 或任意功率单位）")
    private Double power;

    @Schema(title = "信号采集时间戳")
    private LocalDateTime time;

    @Schema(title = "最后更新时间")
    private LocalDateTime updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;
}