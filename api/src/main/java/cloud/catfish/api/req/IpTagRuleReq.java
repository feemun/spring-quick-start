package cloud.catfish.api.req;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Schema(description = "IpTagRule Request Parameters")
public class IpTagRuleReq extends BaseRequestParam{

    @Schema(description = "雪花ID")
    private Long id;

    @Schema(description = "网段 (CIDR格式)")
    @Size(max = 255, message = "cidr cannot exceed 255 characters")
    private String cidr;

    @Schema(description = "站点ID")
    @Size(max = 255, message = "stationId cannot exceed 255 characters")
    private String stationId;

    @Schema(description = "站点名称")
    @Size(max = 255, message = "stationName cannot exceed 255 characters")
    private String stationName;

    @Schema(description = "描述信息")
    @Size(max = 255, message = "description cannot exceed 255 characters")
    private String description;

    @Schema(description = "创建时间 start range")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间 end range")
    private LocalDateTime createTimeEnd;

    @Schema(description = "更新时间 start range")
    private LocalDateTime updateTimeStart;

    @Schema(description = "更新时间 end range")
    private LocalDateTime updateTimeEnd;

}
