package cloud.catfish.redis.service;

import cloud.catfish.redis.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 用户Redis服务类
 * 提供用户数据的Redis缓存操作
 * 
 * @author catfish
 * @since 1.0.0
 */
@Service
public class UserRedisService {
    
    private static final String USER_KEY_PREFIX = "user:";
    private static final String USER_LIST_KEY = "users:list";
    private static final long DEFAULT_EXPIRE_TIME = 3600; // 1小时
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 保存用户
     * 
     * @param user 用户对象
     * @return 保存的用户
     */
    public User saveUser(User user) {
        if (user.getId() == null) {
            user.setId(generateUserId());
        }
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        String key = USER_KEY_PREFIX + user.getId();
        redisTemplate.opsForValue().set(key, user, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
        
        // 添加到用户列表
        redisTemplate.opsForSet().add(USER_LIST_KEY, user.getId().toString());
        
        return user;
    }
    
    /**
     * 根据ID获取用户
     * 
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    public User getUserById(Long id) {
        String key = USER_KEY_PREFIX + id;
        Object obj = redisTemplate.opsForValue().get(key);
        return obj != null ? (User) obj : null;
    }
    
    /**
     * 更新用户
     * 
     * @param user 用户对象
     * @return 更新后的用户，不存在返回null
     */
    public User updateUser(User user) {
        if (user.getId() == null) {
            return null;
        }
        
        String key = USER_KEY_PREFIX + user.getId();
        User existingUser = getUserById(user.getId());
        if (existingUser == null) {
            return null;
        }
        
        // 保留创建时间，更新修改时间
        user.setCreateTime(existingUser.getCreateTime());
        user.setUpdateTime(LocalDateTime.now());
        
        redisTemplate.opsForValue().set(key, user, DEFAULT_EXPIRE_TIME, TimeUnit.SECONDS);
        return user;
    }
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 是否删除成功
     */
    public boolean deleteUser(Long id) {
        String key = USER_KEY_PREFIX + id;
        Boolean deleted = redisTemplate.delete(key);
        
        // 从用户列表中移除
        redisTemplate.opsForSet().remove(USER_LIST_KEY, id.toString());
        
        return Boolean.TRUE.equals(deleted);
    }
    
    /**
     * 获取所有用户
     * 
     * @return 用户列表
     */
    public List<User> getAllUsers() {
        Set<Object> userIds = redisTemplate.opsForSet().members(USER_LIST_KEY);
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<User> users = new ArrayList<>();
        for (Object userIdObj : userIds) {
            Long userId = Long.valueOf(userIdObj.toString());
            User user = getUserById(userId);
            if (user != null) {
                users.add(user);
            }
        }
        
        return users;
    }
    
    /**
     * 检查用户是否存在
     * 
     * @param id 用户ID
     * @return 是否存在
     */
    public boolean existsUser(Long id) {
        String key = USER_KEY_PREFIX + id;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    /**
     * 设置用户过期时间
     * 
     * @param id 用户ID
     * @param timeout 过期时间（秒）
     * @return 是否设置成功
     */
    public boolean expireUser(Long id, long timeout) {
        String key = USER_KEY_PREFIX + id;
        return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, TimeUnit.SECONDS));
    }
    
    /**
     * 生成用户ID
     * 
     * @return 用户ID
     */
    private Long generateUserId() {
        return System.currentTimeMillis();
    }
}