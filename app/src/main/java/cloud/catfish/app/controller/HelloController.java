package cloud.catfish.app.controller;

import cloud.catfish.common.api.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Hello控制器
 * 提供基础的问候和测试接口
 * 
 * @author catfish
 * @since 1.0.0
 */
@Tag(name = "Hello接口", description = "基础问候和测试接口")
@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @Operation(summary = "基础问候接口")
    @GetMapping
    public CommonResult<String> hello() {
        return CommonResult.success("Hello, Welcome to Spring Quick Start App Module!");
    }

    @Operation(summary = "带参数的问候接口")
    @GetMapping("/{name}")
    public CommonResult<String> helloWithName(@PathVariable String name) {
        return CommonResult.success("Hello, " + name + "! Welcome to Spring Quick Start App Module!");
    }

    @Operation(summary = "获取服务信息")
    @GetMapping("/info")
    public CommonResult<Map<String, Object>> getInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Spring Quick Start App Module");
        info.put("version", "1.0.0");
        info.put("description", "用户展示服务模块");
        info.put("timestamp", LocalDateTime.now());
        info.put("author", "catfish");
        
        return CommonResult.success(info);
    }

    @Operation(summary = "健康检查接口")
    @GetMapping("/health")
    public CommonResult<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "app-module");
        health.put("timestamp", LocalDateTime.now().toString());
        
        return CommonResult.success(health);
    }

    @Operation(summary = "POST测试接口")
    @PostMapping("/test")
    public CommonResult<Map<String, Object>> testPost(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "POST请求测试成功");
        response.put("receivedData", requestData);
        response.put("timestamp", LocalDateTime.now());
        
        return CommonResult.success(response);
    }
}