package cloud.catfish.redis.controller;

import cloud.catfish.redis.entity.User;
import cloud.catfish.redis.service.RedisAdvancedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis高级功能控制器
 * 展示RedisTemplate的各种高级用法
 */
@Slf4j
@RestController
@RequestMapping("/api/redis/advanced")
@RequiredArgsConstructor
public class RedisAdvancedController {
    
    private final RedisAdvancedService redisAdvancedService;
    
    /**
     * 分布式锁演示
     * 
     * @param request 锁请求参数
     * @return 操作结果
     */
    @PostMapping("/lock")
    public ResponseEntity<Map<String, Object>> distributedLock(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String lockKey = "lock:" + request.get("resource");
            String lockValue = UUID.randomUUID().toString();
            long expireTime = 30; // 30秒过期
            
            boolean acquired = redisAdvancedService.acquireLock(lockKey, lockValue, expireTime);
            
            if (acquired) {
                try {
                    // 模拟业务处理
                    Thread.sleep(1000);
                    
                    result.put("success", true);
                    result.put("message", "获取锁成功，业务处理完成");
                    result.put("lockKey", lockKey);
                    result.put("lockValue", lockValue);
                } finally {
                    // 释放锁
                    redisAdvancedService.releaseLock(lockKey, lockValue);
                }
            } else {
                result.put("success", false);
                result.put("message", "获取锁失败，资源被占用");
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "分布式锁操作异常: " + e.getMessage());
            log.error("分布式锁操作异常", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 计数器演示
     * 
     * @param counterName 计数器名称
     * @param delta 增量
     * @return 计数结果
     */
    @PostMapping("/counter/{counterName}")
    public ResponseEntity<Map<String, Object>> incrementCounter(
            @PathVariable String counterName,
            @RequestParam(defaultValue = "1") long delta) {
        Map<String, Object> result = new HashMap<>();
        try {
            String counterKey = "counter:" + counterName;
            Long count = redisAdvancedService.incrementCounter(counterKey, delta, 3600); // 1小时过期
            
            result.put("success", true);
            result.put("message", "计数器操作成功");
            result.put("counterName", counterName);
            result.put("currentCount", count);
            result.put("delta", delta);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "计数器操作异常: " + e.getMessage());
            log.error("计数器操作异常", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 限流器演示
     * 
     * @param userId 用户ID
     * @return 限流结果
     */
    @PostMapping("/rate-limit/{userId}")
    public ResponseEntity<Map<String, Object>> checkRateLimit(@PathVariable String userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            String rateLimitKey = "rate_limit:user:" + userId;
            int limit = 10; // 每分钟最多10次
            int windowSeconds = 60; // 60秒窗口
            
            boolean allowed = redisAdvancedService.isAllowed(rateLimitKey, limit, windowSeconds);
            
            result.put("success", true);
            result.put("message", allowed ? "请求通过" : "请求被限流");
            result.put("userId", userId);
            result.put("allowed", allowed);
            result.put("limit", limit);
            result.put("windowSeconds", windowSeconds);
            
            return allowed ? ResponseEntity.ok(result) : ResponseEntity.status(429).body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "限流检查异常: " + e.getMessage());
            log.error("限流检查异常", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 批量操作演示 - 批量设置用户
     * 
     * @param users 用户列表
     * @return 操作结果
     */
    @PostMapping("/batch/users")
    public ResponseEntity<Map<String, Object>> batchSetUsers(@RequestBody List<User> users) {
        Map<String, Object> result = new HashMap<>();
        try {
            redisAdvancedService.batchSetUsers(users, 3600); // 1小时过期
            
            result.put("success", true);
            result.put("message", "批量设置用户成功");
            result.put("count", users.size());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量设置用户异常: " + e.getMessage());
            log.error("批量设置用户异常", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 批量操作演示 - 批量获取用户
     * 
     * @param userIds 用户ID列表
     * @return 用户列表
     */
    @PostMapping("/batch/users/get")
    public ResponseEntity<Map<String, Object>> batchGetUsers(@RequestBody List<Long> userIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<User> users = redisAdvancedService.batchGetUsers(userIds);
            
            result.put("success", true);
            result.put("message", "批量获取用户成功");
            result.put("data", users);
            result.put("requestCount", userIds.size());
            result.put("foundCount", users.size());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量获取用户异常: " + e.getMessage());
            log.error("批量获取用户异常", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 发布订阅演示 - 发布消息
     * 
     * @param request 消息请求
     * @return 发布结果
     */
    @PostMapping("/publish")
    public ResponseEntity<Map<String, Object>> publishMessage(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String channel = (String) request.get("channel");
            Object message = request.get("message");
            
            redisAdvancedService.publishMessage(channel, message);
            
            result.put("success", true);
            result.put("message", "消息发布成功");
            result.put("channel", channel);
            result.put("publishedMessage", message);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "消息发布异常: " + e.getMessage());
            log.error("消息发布异常", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 过期时间管理演示
     * 
     * @param request 过期时间请求
     * @return 操作结果
     */
    @PostMapping("/expire")
    public ResponseEntity<Map<String, Object>> manageExpire(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            String key = (String) request.get("key");
            Object value = request.get("value");
            Integer timeout = (Integer) request.get("timeout");
            String unit = (String) request.getOrDefault("unit", "SECONDS");
            
            TimeUnit timeUnit = TimeUnit.valueOf(unit.toUpperCase());
            redisAdvancedService.setWithExpire(key, value, timeout, timeUnit);
            
            // 获取剩余过期时间
            Long remainingTime = redisAdvancedService.getExpire(key);
            
            result.put("success", true);
            result.put("message", "设置过期时间成功");
            result.put("key", key);
            result.put("timeout", timeout);
            result.put("unit", unit);
            result.put("remainingSeconds", remainingTime);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "过期时间管理异常: " + e.getMessage());
            log.error("过期时间管理异常", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * Key管理演示 - 模糊查询
     * 
     * @param pattern 匹配模式
     * @return 查询结果
     */
    @GetMapping("/keys")
    public ResponseEntity<Map<String, Object>> searchKeys(@RequestParam String pattern) {
        Map<String, Object> result = new HashMap<>();
        try {
            Set<String> keys = redisAdvancedService.getKeysByPattern(pattern);
            
            result.put("success", true);
            result.put("message", "模糊查询完成");
            result.put("pattern", pattern);
            result.put("keys", keys);
            result.put("count", keys.size());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "模糊查询异常: " + e.getMessage());
            log.error("模糊查询异常", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 批量删除Key
     * 
     * @param keys Key列表
     * @return 删除结果
     */
    @DeleteMapping("/keys")
    public ResponseEntity<Map<String, Object>> deleteKeys(@RequestBody List<String> keys) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long deletedCount = redisAdvancedService.deleteKeys(keys);
            
            result.put("success", true);
            result.put("message", "批量删除完成");
            result.put("requestCount", keys.size());
            result.put("deletedCount", deletedCount);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量删除异常: " + e.getMessage());
            log.error("批量删除异常", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * Redis服务器信息
     * 
     * @return 服务器信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getRedisInfo() {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, String> info = redisAdvancedService.getRedisInfo();
            
            result.put("success", true);
            result.put("message", "获取Redis信息成功");
            result.put("info", info);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取Redis信息异常: " + e.getMessage());
            log.error("获取Redis信息异常", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
}