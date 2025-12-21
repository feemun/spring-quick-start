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
public class XlcDroneStatus implements Serializable {
    @Schema(title = "设备唯一端口标识")
    private Long uavPort;

    @Schema(title = "无人机当前海拔高度（单位：米）")
    private Double altitude;

    @Schema(title = "无人机剩余电量百分比（0~100）")
    private Double batteryRemain;

    @Schema(title = "无人机自定义名称")
    private String droneName;

    @Schema(title = "GPS 定位类型：0=无定位, 1=2D, 2=3D, 3=DGPS, 4=RTK")
    private Integer fixType;

    @Schema(title = "航迹角（水平航向角，0~359°，正北为0°，顺时针）")
    private Double hdg;

    @Schema(title = "机头朝向角（0~359°）")
    private Double heading;

    @Schema(title = "纬度（北纬为正）")
    private Double latitude;

    @Schema(title = "经度（东经为正）")
    private Double longitude;

    @Schema(title = "已连接 GPS 卫星数量")
    private Integer satellites;

    @Schema(title = "X 轴速度（东向，单位：m/s）")
    private Double vx;

    @Schema(title = "Y 轴速度（北向，单位：m/s）")
    private Double vy;

    @Schema(title = "Z 轴速度（垂直方向，单位：m/s；负值表示下降）")
    private Double vz;

    @Schema(title = "最后更新时间")
    private LocalDateTime updatedAt;

    @Serial
    private static final long serialVersionUID = 1L;
}