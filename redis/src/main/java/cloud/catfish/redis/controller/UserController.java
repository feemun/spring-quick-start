package cloud.catfish.redis.controller;

import cloud.catfish.redis.entity.User;
import cloud.catfish.redis.service.UserCacheService;
import cloud.catfish.redis.service.UserRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 * 展示实际项目中常用的缓存模式：Cache注解 + RedisTemplate组合使用
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/redis/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserCacheService userCacheService;
    
    @Autowired
    private UserRedisService userRedisService;
    
    /**
     * 创建用户 - 使用Cache注解自动缓存
     * 
     * @param user 用户信息
     * @return 创建结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 使用Cache注解服务，自动处理缓存
            User createdUser = userCacheService.createUser(user);
            result.put("success", true);
            result.put("message", "用户创建成功");
            result.put("data", createdUser);
            log.info("用户创建成功，ID: {}, 用户名: {}", createdUser.getId(), createdUser.getUsername());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "用户创建失败: " + e.getMessage());
            log.error("用户创建失败", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 根据ID获取用户 - 自动缓存查询结果
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 使用@Cacheable注解，自动缓存查询结果
            User user = userCacheService.getUserById(id);
            if (user != null) {
                result.put("success", true);
                result.put("message", "获取用户成功");
                result.put("data", user);
                result.put("fromCache", true); // 标识可能来自缓存
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户失败: " + e.getMessage());
            log.error("获取用户失败，ID: {}", id, e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 获取所有用户 - 缓存用户列表
     * 
     * @return 用户列表
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 使用@Cacheable注解缓存用户列表
            List<User> users = userCacheService.getAllUsers();
            result.put("success", true);
            result.put("message", "获取用户列表成功");
            result.put("data", users);
            result.put("total", users.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户列表失败: " + e.getMessage());
            log.error("获取用户列表失败", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 更新用户 - 自动更新缓存
     * 
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            user.setId(id);
            // 使用@CachePut注解，更新数据的同时更新缓存
            User updatedUser = userCacheService.updateUser(user);
            if (updatedUser != null) {
                result.put("success", true);
                result.put("message", "用户更新成功");
                result.put("data", updatedUser);
                log.info("用户更新成功，ID: {}", id);
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "用户更新失败: " + e.getMessage());
            log.error("用户更新失败，ID: {}", id, e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 删除用户 - 自动清除缓存
     * 
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 使用@CacheEvict注解，删除数据的同时清除缓存
            boolean deleted = userCacheService.deleteUser(id);
            if (deleted) {
                result.put("success", true);
                result.put("message", "用户删除成功");
                log.info("用户删除成功，ID: {}", id);
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "用户删除失败: " + e.getMessage());
            log.error("用户删除失败，ID: {}", id, e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 检查用户是否存在
     * 
     * @param id 用户ID
     * @return 检查结果
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Object>> checkUserExists(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean exists = userRedisService.existsUser(id);
            result.put("success", true);
            result.put("message", "检查完成");
            result.put("exists", exists);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查失败: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 根据用户名获取用户 - 展示复合key缓存
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @GetMapping("/search/username")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@RequestParam String username) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 使用复合key的缓存查询
            User user = userCacheService.getUserByUsername(username);
            if (user != null) {
                result.put("success", true);
                result.put("message", "获取用户成功");
                result.put("data", user);
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "用户不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户失败: " + e.getMessage());
            log.error("根据用户名获取用户失败，用户名: {}", username, e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 批量获取用户 - 展示缓存复用
     * 
     * @param ids 用户ID列表
     * @return 用户列表
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> getUsersByIds(@RequestBody List<Long> ids) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 利用单个用户的缓存进行批量查询
            List<User> users = userCacheService.getUsersByIds(ids);
            result.put("success", true);
            result.put("message", "批量获取用户成功");
            result.put("data", users);
            result.put("total", users.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量获取用户失败: " + e.getMessage());
            log.error("批量获取用户失败", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 获取用户统计信息 - 长期缓存
     * 
     * @return 统计信息
     */
    @GetMapping("/statistics/count")
    public ResponseEntity<Map<String, Object>> getUserCount() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 使用独立的缓存空间存储统计信息
            long count = userCacheService.getUserCount();
            result.put("success", true);
            result.put("message", "获取用户统计成功");
            result.put("count", count);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户统计失败: " + e.getMessage());
            log.error("获取用户统计失败", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 清除所有缓存 - 管理接口
     * 
     * @return 清除结果
     */
    @DeleteMapping("/cache/clear")
    public ResponseEntity<Map<String, Object>> clearAllCache() {
        Map<String, Object> result = new HashMap<>();
        try {
            userCacheService.clearAllCache();
            result.put("success", true);
            result.put("message", "清除所有缓存成功");
            log.info("管理员清除了所有用户缓存");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "清除缓存失败: " + e.getMessage());
            log.error("清除缓存失败", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
    
    /**
     * 预热缓存 - 管理接口
     * 
     * @return 预热结果
     */
    @PostMapping("/cache/warmup")
    public ResponseEntity<Map<String, Object>> warmUpCache() {
        Map<String, Object> result = new HashMap<>();
        try {
            userCacheService.warmUpCache();
            result.put("success", true);
            result.put("message", "缓存预热成功");
            log.info("管理员执行了缓存预热操作");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "缓存预热失败: " + e.getMessage());
            log.error("缓存预热失败", e);
            return ResponseEntity.badRequest().body(result);
        }
    }
}