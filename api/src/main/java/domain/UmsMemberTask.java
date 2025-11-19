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
public class UmsMemberTask implements Serializable {
    private Long id;

    private String name;

    @Schema(title = "赠送成长值")
    private Integer growth;

    @Schema(title = "赠送积分")
    private Integer intergration;

    @Schema(title = "任务类型：0->新手任务；1->日常任务")
    private Integer type;

    @Serial
    private static final long serialVersionUID = 1L;
}