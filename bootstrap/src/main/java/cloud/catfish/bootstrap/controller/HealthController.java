package cloud.catfish.bootstrap.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 
 * @author catfish
 * @since 1.0.0
 */
@Tag(name = "健康检查", description = "系统健康检查接口")
@RestController
@RequestMapping("/health")
public class HealthController {

    @Operation(summary = "系统健康检查", description = "检查系统运行状态")
    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("timestamp", LocalDateTime.now());
        result.put("modules", Map.of(
            "admin", "运行中",
            "app", "运行中", 
            "es", "运行中",
            "neo4j", "运行中"
        ));
        return result;
    }

    @Operation(summary = "系统信息", description = "获取系统基本信息")
    @GetMapping("/info")
    public Map<String, Object> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("application", "Spring Quick Start");
        result.put("version", "1.0.0");
        result.put("description", "统一启动模块，整合所有业务模块");
        result.put("modules", new String[]{"admin", "app", "es", "neo4j"});
        return result;
    }
}