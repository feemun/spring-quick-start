package cloud.catfish.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "IpTagRule")
public record IpTagRuleVO(
    @Schema(description = "雪花ID")
    Long id,
    @Schema(description = "网段 (CIDR格式)")
    String cidr,
    @Schema(description = "站点ID")
    String stationId,
    @Schema(description = "站点名称")
    String stationName,
    @Schema(description = "描述信息")
    String description,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    LocalDateTime createTime,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    LocalDateTime updateTime
) {}
