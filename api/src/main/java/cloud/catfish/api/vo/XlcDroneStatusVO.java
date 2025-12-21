package cloud.catfish.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "XlcDroneStatus")
public record XlcDroneStatusVO(
    @Schema(description = "设备唯一端口标识")
    Long uavPort,
    @Schema(description = "无人机当前海拔高度（单位：米）")
    Double altitude,
    @Schema(description = "无人机剩余电量百分比（0~100）")
    Double batteryRemain,
    @Schema(description = "无人机自定义名称")
    String droneName,
    @Schema(description = "GPS 定位类型：0=无定位, 1=2D, 2=3D, 3=DGPS, 4=RTK")
    Integer fixType,
    @Schema(description = "航迹角（水平航向角，0~359°，正北为0°，顺时针）")
    Double hdg,
    @Schema(description = "机头朝向角（0~359°）")
    Double heading,
    @Schema(description = "纬度（北纬为正）")
    Double latitude,
    @Schema(description = "经度（东经为正）")
    Double longitude,
    @Schema(description = "已连接 GPS 卫星数量")
    Integer satellites,
    @Schema(description = "X 轴速度（东向，单位：m/s）")
    Double vx,
    @Schema(description = "Y 轴速度（北向，单位：m/s）")
    Double vy,
    @Schema(description = "Z 轴速度（垂直方向，单位：m/s；负值表示下降）")
    Double vz,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "最后更新时间")
    LocalDateTime updatedAt
) {}
