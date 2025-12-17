package cloud.catfish.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class IpTagRule implements Serializable {
    @Schema(title = "雪花ID")
    private Long id;

    @Schema(title = "网段 (CIDR格式)")
    private String cidr;

    @Schema(title = "站点ID")
    private String stationId;

    @Schema(title = "站点名称")
    private String stationName;

    @Schema(title = "描述信息")
    private String description;

    @Schema(title = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(title = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}