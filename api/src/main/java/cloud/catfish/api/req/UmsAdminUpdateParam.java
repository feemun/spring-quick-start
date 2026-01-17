package cloud.catfish.api.req;

import cloud.catfish.api.common.BaseRequestParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UmsAdminUpdateParam extends BaseRequestParam {
    @Schema(title = "用户头像")
    private String icon;

    @Email
    @Schema(title = "邮箱")
    private String email;

    @Schema(title = "用户昵称")
    private String nickName;

    @Schema(title = "备注")
    private String note;
}

