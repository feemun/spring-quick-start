package cloud.catfish.admin.controller.ums;


import cloud.catfish.admin.service.OssService;
import cloud.catfish.api.common.R;
import cloud.catfish.api.common.ResultCode;
import cloud.catfish.api.dto.OssCallbackResult;
import cloud.catfish.api.dto.OssPolicyResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Oss对象存储管理Controller
 * Created by macro on 2018/4/26.
 */
@RestController
@Tag(name = "OssController", description = "Oss对象存储管理")
@RequestMapping("/aliyun/oss")
@RequiredArgsConstructor
public class OssController {
    private final OssService ossService;

    @Operation(summary = "Oss上传签名生成")
    @GetMapping(value = "/policy")
    public R<OssPolicyResult> policy() {
        OssPolicyResult result = ossService.policy();
        return R.ok(result);
    }

    @Operation(summary = "Oss上传成功回调")
    @PostMapping(value = "callback")
    public R<OssCallbackResult> callback(HttpServletRequest request) {
        OssCallbackResult ossCallbackResult = ossService.callback(request);
        return R.ok(ossCallbackResult);
    }

}
