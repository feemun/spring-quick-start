package cloud.catfish.dataprocessing.controller;

import cloud.catfish.common.api.CommonResult;
import cloud.catfish.dataprocessing.config.DataProcessingProperties;
import cloud.catfish.dataprocessing.service.DataProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 数据处理模块API控制器
 * 提供任务监控和管理功能
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/data-processing")
@Tag(name = "数据处理模块", description = "数据处理任务监控和管理API")
public class DataProcessingController {

    @Autowired
    private DataProcessingProperties properties;

    @Autowired
    private DataProcessingService dataProcessingService;

    @Autowired
    private ThreadPoolExecutor taskExecutor;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取模块状态信息
     */
    @GetMapping("/status")
    @Operation(summary = "获取模块状态", description = "获取数据处理模块的运行状态信息")
    public CommonResult<Map<String, Object>> getStatus() {
        try {
            Map<String, Object> status = new HashMap<>();
            
            // 模块基本信息
            status.put("enabled", properties.isEnabled());
            status.put("batchSize", properties.getBatchSize());
            status.put("currentTime", LocalDateTime.now().format(FORMATTER));
            
            // 线程池状态
            Map<String, Object> threadPoolStatus = new HashMap<>();
            threadPoolStatus.put("corePoolSize", taskExecutor.getCorePoolSize());
            threadPoolStatus.put("maximumPoolSize", taskExecutor.getMaximumPoolSize());
            threadPoolStatus.put("activeCount", taskExecutor.getActiveCount());
            threadPoolStatus.put("taskCount", taskExecutor.getTaskCount());
            threadPoolStatus.put("completedTaskCount", taskExecutor.getCompletedTaskCount());
            threadPoolStatus.put("queueSize", taskExecutor.getQueue().size());
            status.put("threadPool", threadPoolStatus);
            
            // 各功能模块状态
            Map<String, Object> modules = new HashMap<>();
            modules.put("dataClean", properties.getDataClean());
            modules.put("dataSync", properties.getDataSync());
            modules.put("dataStats", properties.getDataStats());
            status.put("modules", modules);
            
            return CommonResult.success(status);
            
        } catch (Exception e) {
            log.error("获取模块状态失败", e);
            return CommonResult.failed("获取模块状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取统计数据
     */
    @GetMapping("/stats/{tableName}")
    @Operation(summary = "获取统计数据", description = "获取指定表的统计数据")
    public CommonResult<Map<String, Object>> getStats(
            @Parameter(description = "表名") @PathVariable String tableName,
            @Parameter(description = "统计类型：daily/weekly/monthly") @RequestParam(defaultValue = "daily") String type) {
        try {
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String statsKey = "data_stats:" + tableName + ":" + type + ":" + date;
            
            String statsJson = redisTemplate.opsForValue().get(statsKey);
            if (statsJson == null) {
                return CommonResult.failed("未找到统计数据，请先执行统计任务");
            }
            
            Map<String, Object> stats = objectMapper.readValue(statsJson, Map.class);
            return CommonResult.success(stats);
            
        } catch (Exception e) {
            log.error("获取统计数据失败: tableName={}, type={}", tableName, type, e);
            return CommonResult.failed("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有统计数据键
     */
    @GetMapping("/stats/keys")
    @Operation(summary = "获取统计数据键", description = "获取所有可用的统计数据键")
    public CommonResult<Set<String>> getStatsKeys() {
        try {
            Set<String> keys = redisTemplate.keys("data_stats:*");
            return CommonResult.success(keys);
            
        } catch (Exception e) {
            log.error("获取统计数据键失败", e);
            return CommonResult.failed("获取统计数据键失败: " + e.getMessage());
        }
    }

    /**
     * 获取同步状态
     */
    @GetMapping("/sync/status")
    @Operation(summary = "获取同步状态", description = "获取数据同步的状态信息")
    public CommonResult<Map<String, Object>> getSyncStatus() {
        try {
            Set<String> syncKeys = redisTemplate.keys("data_sync:last_time:*");
            Map<String, Object> syncStatus = new HashMap<>();
            
            for (String key : syncKeys) {
                String lastSyncTime = redisTemplate.opsForValue().get(key);
                String syncPair = key.replace("data_sync:last_time:", "");
                syncStatus.put(syncPair, lastSyncTime);
            }
            
            return CommonResult.success(syncStatus);
            
        } catch (Exception e) {
            log.error("获取同步状态失败", e);
            return CommonResult.failed("获取同步状态失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发数据清理
     */
    @PostMapping("/clean")
    @Operation(summary = "手动触发数据清理", description = "手动触发指定表的数据清理任务")
    public CommonResult<String> triggerDataClean(
            @Parameter(description = "表名") @RequestParam String tableName,
            @Parameter(description = "保留天数") @RequestParam(defaultValue = "30") int retentionDays) {
        try {
            if (!properties.getDataClean().isEnabled()) {
                return CommonResult.failed("数据清理功能未启用");
            }
            
            int cleanedCount = dataProcessingService.cleanExpiredData(tableName, retentionDays);
            String result = String.format("数据清理完成，表[%s]删除了%d条记录", tableName, cleanedCount);
            
            log.info("手动触发数据清理: {}", result);
            return CommonResult.success(result);
            
        } catch (Exception e) {
            log.error("手动触发数据清理失败: tableName={}, retentionDays={}", tableName, retentionDays, e);
            return CommonResult.failed("数据清理失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发数据同步
     */
    @PostMapping("/sync")
    @Operation(summary = "手动触发数据同步", description = "手动触发指定表的数据同步任务")
    public CommonResult<String> triggerDataSync(
            @Parameter(description = "源表名") @RequestParam String sourceTable,
            @Parameter(description = "目标表名") @RequestParam String targetTable) {
        try {
            if (!properties.getDataSync().isEnabled()) {
                return CommonResult.failed("数据同步功能未启用");
            }
            
            // 获取上次同步时间，默认为24小时前
            LocalDateTime lastSyncTime = LocalDateTime.now().minusDays(1);
            
            int syncedCount = dataProcessingService.syncData(sourceTable, targetTable, lastSyncTime);
            String result = String.format("数据同步完成，从[%s]同步到[%s]共%d条记录", 
                sourceTable, targetTable, syncedCount);
            
            log.info("手动触发数据同步: {}", result);
            return CommonResult.success(result);
            
        } catch (Exception e) {
            log.error("手动触发数据同步失败: sourceTable={}, targetTable={}", sourceTable, targetTable, e);
            return CommonResult.failed("数据同步失败: " + e.getMessage());
        }
    }

    /**
     * 手动触发数据统计
     */
    @PostMapping("/stats")
    @Operation(summary = "手动触发数据统计", description = "手动触发指定表的数据统计任务")
    public CommonResult<Map<String, Object>> triggerDataStats(
            @Parameter(description = "表名") @RequestParam String tableName,
            @Parameter(description = "统计小时数") @RequestParam(defaultValue = "24") int hours) {
        try {
            if (!properties.getDataStats().isEnabled()) {
                return CommonResult.failed("数据统计功能未启用");
            }
            
            LocalDateTime endTime = LocalDateTime.now();
            LocalDateTime startTime = endTime.minusHours(hours);
            
            Map<String, Object> stats = dataProcessingService.generateStats(tableName, startTime, endTime);
            
            // 保存到Redis
            String statsKey = "data_stats:" + tableName + ":" + hours + "h:" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String statsJson = objectMapper.writeValueAsString(stats);
            redisTemplate.opsForValue().set(statsKey, statsJson);
            
            log.info("手动触发数据统计: tableName={}, hours={}", tableName, hours);
            return CommonResult.success(stats);
            
        } catch (Exception e) {
            log.error("手动触发数据统计失败: tableName={}, hours={}", tableName, hours, e);
            return CommonResult.failed("数据统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取表记录数
     */
    @GetMapping("/table/{tableName}/count")
    @Operation(summary = "获取表记录数", description = "获取指定表的记录总数")
    public CommonResult<Long> getTableCount(
            @Parameter(description = "表名") @PathVariable String tableName) {
        try {
            long count = dataProcessingService.getTableCount(tableName);
            return CommonResult.success(count);
            
        } catch (Exception e) {
            log.error("获取表记录数失败: tableName={}", tableName, e);
            return CommonResult.failed("获取表记录数失败: " + e.getMessage());
        }
    }
}