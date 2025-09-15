package cloud.catfish.app.controller;

import cloud.catfish.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 需要认证的安全控制器
 * 演示Spring Security保护的接口
 * 
 * @author catfish
 * @since 1.0.0
 */
@Tag(name = "安全接口", description = "需要认证才能访问的接口")
@RestController
@RequestMapping("/api/secure")
public class SecureController {

    @Operation(summary = "获取用户信息", description = "需要JWT认证")
    @GetMapping("/user-info")
    public CommonResult<Map<String, Object>> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", authentication.getName());
        userInfo.put("authorities", authentication.getAuthorities());
        userInfo.put("authenticated", authentication.isAuthenticated());
        userInfo.put("timestamp", LocalDateTime.now());
        
        return CommonResult.success(userInfo);
    }

    @Operation(summary = "受保护的数据", description = "需要JWT认证")
    @GetMapping("/protected-data")
    public CommonResult<Map<String, Object>> getProtectedData() {
        Map<String, Object> data = new HashMap<>();
        data.put("message", "这是受保护的数据，只有认证用户才能访问");
        data.put("timestamp", LocalDateTime.now());
        data.put("level", "PROTECTED");
        
        return CommonResult.success(data);
    }

    @Operation(summary = "更新用户设置", description = "需要JWT认证")
    @PostMapping("/settings")
    public CommonResult<Map<String, Object>> updateSettings(@RequestBody Map<String, Object> settings) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "设置更新成功");
        result.put("user", authentication.getName());
        result.put("updatedSettings", settings);
        result.put("timestamp", LocalDateTime.now());
        
        return CommonResult.success(result);
    }
}