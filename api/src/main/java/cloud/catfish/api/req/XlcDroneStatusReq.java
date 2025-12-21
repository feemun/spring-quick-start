package cloud.catfish.api.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "XlcDroneStatus Request Parameters")
public class XlcDroneStatusReq extends BaseRequestParam {

    @Schema(description = "设备唯一端口标识")
    private Long uavPort;

    @Schema(description = "无人机当前海拔高度（单位：米）")
    private Double altitude;

    @Schema(description = "无人机剩余电量百分比（0~100）")
    private Double batteryRemain;

    @Schema(description = "无人机自定义名称")
    @Size(max = 255, message = "droneName cannot exceed 255 characters")
    private String droneName;

    @Schema(description = "GPS 定位类型：0=无定位, 1=2D, 2=3D, 3=DGPS, 4=RTK")
    private Integer fixType;

    @Schema(description = "航迹角（水平航向角，0~359°，正北为0°，顺时针）")
    private Double hdg;

    @Schema(description = "机头朝向角（0~359°）")
    private Double heading;

    @Schema(description = "纬度（北纬为正）")
    private Double latitude;

    @Schema(description = "经度（东经为正）")
    private Double longitude;

    @Schema(description = "已连接 GPS 卫星数量")
    private Integer satellites;

    @Schema(description = "X 轴速度（东向，单位：m/s）")
    private Double vx;

    @Schema(description = "Y 轴速度（北向，单位：m/s）")
    private Double vy;

    @Schema(description = "Z 轴速度（垂直方向，单位：m/s；负值表示下降）")
    private Double vz;

    @Schema(description = "最后更新时间 start range")
    private LocalDateTime updatedAtStart;

    @Schema(description = "最后更新时间 end range")
    private LocalDateTime updatedAtEnd;

}
