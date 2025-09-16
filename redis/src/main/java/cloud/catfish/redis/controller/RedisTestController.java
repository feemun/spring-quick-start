package cloud.catfish.redis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis测试控制器
 * 提供基本的Redis操作测试接口
 * 
 * @author catfish
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/redis/test")
@CrossOrigin(origins = "*")
public class RedisTestController {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 测试Redis连接
     * 
     * @return 测试结果
     */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        Map<String, Object> result = new HashMap<>();
        try {
            String pong = redisTemplate.getConnectionFactory().getConnection().ping();
            result.put("success", true);
            result.put("message", "Redis连接正常");
            result.put("response", pong);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Redis连接失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 设置键值对
     * 
     * @param key 键
     * @param value 值
     * @param expire 过期时间（秒），可选
     * @return 设置结果
     */
    @PostMapping("/set")
    public Map<String, Object> setValue(@RequestParam String key, 
                                       @RequestParam String value,
                                       @RequestParam(required = false) Long expire) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (expire != null && expire > 0) {
                redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            result.put("success", true);
            result.put("message", "设置成功");
            result.put("key", key);
            result.put("value", value);
            if (expire != null) {
                result.put("expire", expire + "秒");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "设置失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取值
     * 
     * @param key 键
     * @return 获取结果
     */
    @GetMapping("/get")
    public Map<String, Object> getValue(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            Object value = redisTemplate.opsForValue().get(key);
            result.put("success", true);
            result.put("key", key);
            result.put("value", value);
            result.put("exists", value != null);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 删除键
     * 
     * @param key 键
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Map<String, Object> deleteKey(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            Boolean deleted = redisTemplate.delete(key);
            result.put("success", true);
            result.put("key", key);
            result.put("deleted", Boolean.TRUE.equals(deleted));
            result.put("message", Boolean.TRUE.equals(deleted) ? "删除成功" : "键不存在");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 检查键是否存在
     * 
     * @param key 键
     * @return 检查结果
     */
    @GetMapping("/exists")
    public Map<String, Object> keyExists(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            Boolean exists = redisTemplate.hasKey(key);
            result.put("success", true);
            result.put("key", key);
            result.put("exists", Boolean.TRUE.equals(exists));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "检查失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取键的剩余过期时间
     * 
     * @param key 键
     * @return 过期时间结果
     */
    @GetMapping("/ttl")
    public Map<String, Object> getTimeToLive(@RequestParam String key) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            result.put("success", true);
            result.put("key", key);
            result.put("ttl", ttl);
            if (ttl == -1) {
                result.put("message", "键永不过期");
            } else if (ttl == -2) {
                result.put("message", "键不存在");
            } else {
                result.put("message", "剩余" + ttl + "秒过期");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取过期时间失败: " + e.getMessage());
        }
        return result;
    }
}