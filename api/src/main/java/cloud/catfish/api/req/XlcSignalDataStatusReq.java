package cloud.catfish.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "XlcSignalDataStatus Request Parameters")
public class XlcSignalDataStatusReq extends BaseRequestParam {

    @Schema(description = "设备唯一端口标识（与无人机 uav_port 关联）")
    private Long uavPort;

    @Schema(description = "信号方位角（0~359°，正北为0°，顺时针）")
    private Double azimuth;

    @Schema(description = "信号频率（单位：Hz）")
    private Double frequency;

    @Schema(description = "信号采集点纬度")
    private Double latitude;

    @Schema(description = "信号采集点经度")
    private Double longitude;

    @Schema(description = "信号采集设备俯仰角（单位：度）")
    private Double pitch;

    @Schema(description = "信号功率（单位：dBm 或任意功率单位）")
    private Double power;

    @Schema(description = "信号采集时间戳 start range")
    private LocalDateTime timeStart;

    @Schema(description = "信号采集时间戳 end range")
    private LocalDateTime timeEnd;

    @Schema(description = "最后更新时间 start range")
    private LocalDateTime updatedAtStart;

    @Schema(description = "最后更新时间 end range")
    private LocalDateTime updatedAtEnd;

}
