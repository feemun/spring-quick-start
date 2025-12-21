package cloud.catfish.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "XlcSignalDataStatus")
public record XlcSignalDataStatusVO(
    @Schema(description = "设备唯一端口标识（与无人机 uav_port 关联）")
    Long uavPort,
    @Schema(description = "信号方位角（0~359°，正北为0°，顺时针）")
    Double azimuth,
    @Schema(description = "信号频率（单位：Hz）")
    Double frequency,
    @Schema(description = "信号采集点纬度")
    Double latitude,
    @Schema(description = "信号采集点经度")
    Double longitude,
    @Schema(description = "信号采集设备俯仰角（单位：度）")
    Double pitch,
    @Schema(description = "信号功率（单位：dBm 或任意功率单位）")
    Double power,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "信号采集时间戳")
    LocalDateTime time,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后更新时间")
    LocalDateTime updatedAt
) {}
