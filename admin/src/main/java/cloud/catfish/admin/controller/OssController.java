package cloud.catfish.admin.controller;


import cloud.catfish.admin.dto.OssCallbackResult;
import cloud.catfish.admin.dto.OssPolicyResult;
import cloud.catfish.admin.service.OssService;
import cloud.catfish.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Oss对象存储管理Controller
 * Created by macro on 2018/4/26.
 */
@RestController
@Tag(name = "OssController", description = "Oss对象存储管理")
@RequestMapping("/aliyun/oss")
public class OssController {
    @Autowired
    private OssService ossService;

    @Operation(summary = "Oss上传签名生成")
    @GetMapping(value = "/policy")
    public CommonResult<OssPolicyResult> policy() {
        OssPolicyResult result = ossService.policy();
        return CommonResult.success(result);
    }

    @Operation(summary = "Oss上传成功回调")
    @PostMapping(value = "callback")
    public CommonResult<OssCallbackResult> callback(HttpServletRequest request) {
        OssCallbackResult ossCallbackResult = ossService.callback(request);
        return CommonResult.success(ossCallbackResult);
    }

}
