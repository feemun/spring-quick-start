package cloud.catfish.redis.service;

import cloud.catfish.redis.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户缓存服务类
 * 展示Spring Cache注解的实际使用场景
 * 这是生产环境中最常用的Redis缓存模式
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@Service
@CacheConfig(cacheNames = "user")
public class UserCacheService {
    
    // 模拟数据库存储
    private final ConcurrentHashMap<Long, User> userDatabase = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    /**
     * 根据ID获取用户信息
     * @Cacheable: 如果缓存中有数据则直接返回，否则执行方法并缓存结果
     * 
     * @param id 用户ID
     * @return 用户信息
     */
    @Cacheable(key = "#id", unless = "#result == null")
    public User getUserById(Long id) {
        log.info("从数据库查询用户信息，ID: {}", id);
        // 模拟数据库查询延迟
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return userDatabase.get(id);
    }
    
    /**
     * 根据用户名获取用户信息
     * 使用复合key，支持多参数缓存
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Cacheable(key = "'username:' + #username", unless = "#result == null")
    public User getUserByUsername(String username) {
        log.info("从数据库根据用户名查询用户信息: {}", username);
        return userDatabase.values().stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 创建用户
     * @CachePut: 无论缓存中是否有数据，都执行方法并更新缓存
     * 
     * @param user 用户信息
     * @return 创建的用户
     */
    @CachePut(key = "#result.id")
    public User createUser(User user) {
        Long id = idGenerator.getAndIncrement();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        userDatabase.put(id, user);
        log.info("创建用户成功，ID: {}, 用户名: {}", id, user.getUsername());
        return user;
    }
    
    /**
     * 更新用户信息
     * @CachePut: 更新数据库的同时更新缓存
     * 
     * @param user 用户信息
     * @return 更新后的用户
     */
    @CachePut(key = "#user.id")
    @CacheEvict(key = "'username:' + #user.username", beforeInvocation = true)
    public User updateUser(User user) {
        User existingUser = userDatabase.get(user.getId());
        if (existingUser != null) {
            user.setCreatedAt(existingUser.getCreatedAt());
            user.setUpdatedAt(LocalDateTime.now());
            userDatabase.put(user.getId(), user);
            log.info("更新用户成功，ID: {}", user.getId());
            return user;
        }
        return null;
    }
    
    /**
     * 删除用户
     * @CacheEvict: 删除缓存中的数据
     * 
     * @param id 用户ID
     * @return 是否删除成功
     */
    @CacheEvict(key = "#id")
    public boolean deleteUser(Long id) {
        User user = userDatabase.get(id);
        if (user != null) {
            // 同时删除用户名缓存
            evictUsernameCache(user.getUsername());
            userDatabase.remove(id);
            log.info("删除用户成功，ID: {}", id);
            return true;
        }
        return false;
    }
    
    /**
     * 获取所有用户 - 缓存用户列表
     * 
     * @return 用户列表
     */
    @Cacheable(value = "userList", key = "'all'")
    public List<User> getAllUsers() {
        log.info("从数据源获取所有用户列表");
        // 实际项目中这里会调用数据库查询
        // return userRepository.findAll();
        
        // 模拟从数据库获取所有用户
        List<User> users = new ArrayList<>();
        Set<String> keys = redisTemplate.keys("user:*");
        if (keys != null) {
            for (String key : keys) {
                Object userData = redisTemplate.opsForValue().get(key);
                if (userData instanceof User) {
                    users.add((User) userData);
                }
            }
        }
        return users;
    }
    
    /**
     * 根据用户名获取用户 - 复合key缓存
     * 
     * @param username 用户名
     * @return 用户信息
     */
    @Cacheable(value = "usersByUsername", key = "#username")
    public User getUserByUsername(String username) {
        log.info("根据用户名查询用户: {}", username);
        // 实际项目中这里会调用数据库查询
        // return userRepository.findByUsername(username);
        
        // 模拟根据用户名查询
        Set<String> keys = redisTemplate.keys("user:*");
        if (keys != null) {
            for (String key : keys) {
                Object userData = redisTemplate.opsForValue().get(key);
                if (userData instanceof User) {
                    User user = (User) userData;
                    if (username.equals(user.getUsername())) {
                        return user;
                    }
                }
            }
        }
        return null;
    }
    
    /**
     * 批量获取用户 - 利用单个缓存
     * 
     * @param ids 用户ID列表
     * @return 用户列表
     */
    public List<User> getUsersByIds(List<Long> ids) {
        log.info("批量获取用户，数量: {}", ids.size());
        List<User> users = new ArrayList<>();
        
        // 利用单个用户的缓存，避免重复查询
        for (Long id : ids) {
            User user = getUserById(id); // 这里会利用@Cacheable缓存
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }
    
    /**
     * 获取用户统计 - 长期缓存
     * 
     * @return 用户总数
     */
    @Cacheable(value = "userStats", key = "'count'")
    public long getUserCount() {
        log.info("统计用户总数");
        // 实际项目中这里会调用数据库统计
        // return userRepository.count();
        
        // 模拟统计用户数量
        Set<String> keys = redisTemplate.keys("user:*");
        return keys != null ? keys.size() : 0;
    }
    
    /**
     * 清除所有缓存
     */
    @CacheEvict(value = {"users", "userList", "usersByUsername", "userStats"}, allEntries = true)
    public void clearAllCache() {
        log.info("清除所有用户相关缓存");
    }
    
    /**
     * 缓存预热 - 提前加载热点数据
     */
    public void warmUpCache() {
        log.info("开始缓存预热");
        
        // 预热用户列表缓存
        getAllUsers();
        
        // 预热用户统计缓存
        getUserCount();
        
        // 可以根据业务需要预热更多数据
        log.info("缓存预热完成");
    }
    
    /**
     * 根据用户名删除缓存
     * 
     * @param username 用户名
     */
    @CacheEvict(key = "'username:' + #username")
    public void evictUsernameCache(String username) {
        log.info("清除用户名缓存: {}", username);
    }
    
    /**
     * 获取用户列表
     * 使用条件缓存，只有当列表不为空时才缓存
     * 
     * @return 用户列表
     */
    @Cacheable(key = "'userList'", unless = "#result.isEmpty()")
    public List<User> getAllUsers() {
        log.info("从数据库查询所有用户列表");
        // 模拟数据库查询延迟
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>(userDatabase.values());
    }
    
    /**
     * 清除所有用户缓存
     * @CacheEvict: allEntries=true 清除该缓存空间的所有数据
     */
    @CacheEvict(allEntries = true)
    public void clearAllCache() {
        log.info("清除所有用户缓存");
    }
    
    /**
     * 批量获取用户信息
     * 展示复杂的缓存逻辑处理
     * 
     * @param ids 用户ID列表
     * @return 用户列表
     */
    public List<User> getUsersByIds(List<Long> ids) {
        List<User> users = new ArrayList<>();
        for (Long id : ids) {
            User user = getUserById(id); // 这里会利用单个用户的缓存
            if (user != null) {
                users.add(user);
            }
        }
        return users;
    }
    
    /**
     * 获取用户统计信息
     * 使用自定义缓存key和较长的过期时间
     * 
     * @return 用户总数
     */
    @Cacheable(key = "'userCount'", cacheNames = "statistics")
    public long getUserCount() {
        log.info("统计用户总数");
        return userDatabase.size();
    }
    
    /**
     * 刷新用户统计缓存
     */
    @CacheEvict(key = "'userCount'", cacheNames = "statistics")
    public void refreshUserCount() {
        log.info("刷新用户统计缓存");
    }
    
    /**
     * 预热缓存 - 在应用启动时调用
     * 将热点数据提前加载到缓存中
     */
    public void warmUpCache() {
        log.info("开始预热用户缓存");
        // 预加载前10个用户到缓存
        userDatabase.keySet().stream()
                .limit(10)
                .forEach(this::getUserById);
        log.info("用户缓存预热完成");
    }
}