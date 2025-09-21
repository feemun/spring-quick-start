package cloud.catfish.redis.service;

import cloud.catfish.redis.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis高级操作服务类
 * 展示RedisTemplate的各种高级用法，包括：
 * 1. 分布式锁
 * 2. 计数器和限流
 * 3. 发布订阅
 * 4. 批量操作
 * 5. Lua脚本
 * 6. 过期策略
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisAdvancedService {
    
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Lua脚本：分布式锁释放
    private static final String UNLOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "    return redis.call('del', KEYS[1]) " +
        "else " +
        "    return 0 " +
        "end";
    
    // Lua脚本：限流器
    private static final String RATE_LIMIT_SCRIPT = 
        "local key = KEYS[1] " +
        "local limit = tonumber(ARGV[1]) " +
        "local window = tonumber(ARGV[2]) " +
        "local current = redis.call('incr', key) " +
        "if current == 1 then " +
        "    redis.call('expire', key, window) " +
        "end " +
        "return current";
    
    /**
     * 分布式锁 - 获取锁
     * 
     * @param lockKey 锁的key
     * @param lockValue 锁的值（通常是UUID）
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean acquireLock(String lockKey, String lockValue, long expireTime) {
        try {
            // 使用SET NX EX命令实现分布式锁
            Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue, Duration.ofSeconds(expireTime));
            
            if (Boolean.TRUE.equals(result)) {
                log.info("获取分布式锁成功: {}", lockKey);
                return true;
            } else {
                log.warn("获取分布式锁失败: {}", lockKey);
                return false;
            }
        } catch (Exception e) {
            log.error("获取分布式锁异常: {}", lockKey, e);
            return false;
        }
    }
    
    /**
     * 分布式锁 - 释放锁
     * 
     * @param lockKey 锁的key
     * @param lockValue 锁的值
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        try {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(UNLOCK_SCRIPT);
            script.setResultType(Long.class);
            
            Long result = redisTemplate.execute(script, 
                Collections.singletonList(lockKey), lockValue);
            
            if (result != null && result == 1L) {
                log.info("释放分布式锁成功: {}", lockKey);
                return true;
            } else {
                log.warn("释放分布式锁失败: {}", lockKey);
                return false;
            }
        } catch (Exception e) {
            log.error("释放分布式锁异常: {}", lockKey, e);
            return false;
        }
    }
    
    /**
     * 计数器 - 原子递增
     * 
     * @param counterKey 计数器key
     * @param delta 增量
     * @param expireSeconds 过期时间（秒）
     * @return 递增后的值
     */
    public Long incrementCounter(String counterKey, long delta, long expireSeconds) {
        try {
            Long result = redisTemplate.opsForValue().increment(counterKey, delta);
            
            // 设置过期时间（仅在第一次创建时）
            if (result != null && result == delta) {
                redisTemplate.expire(counterKey, Duration.ofSeconds(expireSeconds));
            }
            
            log.info("计数器递增: {} = {}", counterKey, result);
            return result;
        } catch (Exception e) {
            log.error("计数器递增异常: {}", counterKey, e);
            return null;
        }
    }
    
    /**
     * 限流器 - 滑动窗口限流
     * 
     * @param rateLimitKey 限流key
     * @param limit 限制次数
     * @param windowSeconds 时间窗口（秒）
     * @return 是否允许通过
     */
    public boolean isAllowed(String rateLimitKey, int limit, int windowSeconds) {
        try {
            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptText(RATE_LIMIT_SCRIPT);
            script.setResultType(Long.class);
            
            Long current = redisTemplate.execute(script,
                Collections.singletonList(rateLimitKey),
                String.valueOf(limit), String.valueOf(windowSeconds));
            
            boolean allowed = current != null && current <= limit;
            log.info("限流检查: {} 当前次数: {} 限制: {} 结果: {}", 
                rateLimitKey, current, limit, allowed ? "通过" : "拒绝");
            
            return allowed;
        } catch (Exception e) {
            log.error("限流检查异常: {}", rateLimitKey, e);
            return false;
        }
    }
    
    /**
     * 批量操作 - 批量设置用户
     * 
     * @param users 用户列表
     * @param expireSeconds 过期时间（秒）
     */
    public void batchSetUsers(List<User> users, long expireSeconds) {
        try {
            // 使用Pipeline提高批量操作性能
            redisTemplate.executePipelined((connection) -> {
                for (User user : users) {
                    String key = "user:" + user.getId();
                    redisTemplate.opsForValue().set(key, user, Duration.ofSeconds(expireSeconds));
                }
                return null;
            });
            
            log.info("批量设置用户完成，数量: {}", users.size());
        } catch (Exception e) {
            log.error("批量设置用户异常", e);
        }
    }
    
    /**
     * 批量操作 - 批量获取用户
     * 
     * @param userIds 用户ID列表
     * @return 用户列表
     */
    public List<User> batchGetUsers(List<Long> userIds) {
        try {
            List<String> keys = userIds.stream()
                .map(id -> "user:" + id)
                .toList();
            
            List<Object> results = redisTemplate.opsForValue().multiGet(keys);
            List<User> users = new ArrayList<>();
            
            if (results != null) {
                for (Object result : results) {
                    if (result instanceof User) {
                        users.add((User) result);
                    }
                }
            }
            
            log.info("批量获取用户完成，请求: {} 获得: {}", userIds.size(), users.size());
            return users;
        } catch (Exception e) {
            log.error("批量获取用户异常", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 发布消息
     * 
     * @param channel 频道
     * @param message 消息
     */
    public void publishMessage(String channel, Object message) {
        try {
            redisTemplate.convertAndSend(channel, message);
            log.info("发布消息到频道: {} 消息: {}", channel, message);
        } catch (Exception e) {
            log.error("发布消息异常: {}", channel, e);
        }
    }
    
    /**
     * 设置带过期时间的数据
     * 
     * @param key 键
     * @param value 值
     * @param timeout 超时时间
     * @param unit 时间单位
     */
    public void setWithExpire(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.info("设置带过期时间的数据: {} 过期时间: {} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("设置数据异常: {}", key, e);
        }
    }
    
    /**
     * 获取剩余过期时间
     * 
     * @param key 键
     * @return 剩余秒数，-1表示永不过期，-2表示key不存在
     */
    public Long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            log.info("获取过期时间: {} 剩余: {}秒", key, expire);
            return expire;
        } catch (Exception e) {
            log.error("获取过期时间异常: {}", key, e);
            return -2L;
        }
    }
    
    /**
     * 模糊查询key
     * 
     * @param pattern 匹配模式
     * @return key列表
     */
    public Set<String> getKeysByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            log.info("模糊查询key: {} 找到: {}个", pattern, keys != null ? keys.size() : 0);
            return keys != null ? keys : new HashSet<>();
        } catch (Exception e) {
            log.error("模糊查询key异常: {}", pattern, e);
            return new HashSet<>();
        }
    }
    
    /**
     * 检查key是否存在
     * 
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("检查key存在性异常: {}", key, e);
            return false;
        }
    }
    
    /**
     * 删除多个key
     * 
     * @param keys key列表
     * @return 删除的数量
     */
    public Long deleteKeys(Collection<String> keys) {
        try {
            Long deleted = redisTemplate.delete(keys);
            log.info("批量删除key完成，请求: {} 删除: {}", keys.size(), deleted);
            return deleted;
        } catch (Exception e) {
            log.error("批量删除key异常", e);
            return 0L;
        }
    }
    
    /**
     * 获取Redis信息
     * 
     * @return Redis服务器信息
     */
    public Map<String, String> getRedisInfo() {
        try {
            Properties info = redisTemplate.getConnectionFactory()
                .getConnection().info();
            
            Map<String, String> result = new HashMap<>();
            for (String key : info.stringPropertyNames()) {
                result.put(key, info.getProperty(key));
            }
            
            log.info("获取Redis信息成功，条目数: {}", result.size());
            return result;
        } catch (Exception e) {
            log.error("获取Redis信息异常", e);
            return new HashMap<>();
        }
    }
}