package cloud.catfish.mbg.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UmsMember implements Serializable {
    private Long id;

    private Long memberLevelId;

    @Schema(title = "用户名")
    private String username;

    @Schema(title = "密码")
    private String password;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "帐号启用状态:0->禁用；1->启用")
    private Integer status;

    @Schema(title = "注册时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(title = "头像")
    private String icon;

    @Schema(title = "性别：0->未知；1->男；2->女")
    private Integer gender;

    @Schema(title = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime birthday;

    @Schema(title = "所做城市")
    private String city;

    @Schema(title = "职业")
    private String job;

    @Schema(title = "个性签名")
    private String personalizedSignature;

    @Schema(title = "用户来源")
    private Integer sourceType;

    @Schema(title = "积分")
    private Integer integration;

    @Schema(title = "成长值")
    private Integer growth;

    @Schema(title = "剩余抽奖次数")
    private Integer luckeyCount;

    @Schema(title = "历史积分数量")
    private Integer historyIntegration;

    @Serial
    private static final long serialVersionUID = 1L;
}