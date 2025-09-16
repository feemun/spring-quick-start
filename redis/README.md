# Redis模块

本模块展示了如何在 Spring Boot 项目中集成和使用 Redis，重点展示**实际项目中常用的缓存模式**：
- **Spring Cache 注解** - 声明式缓存，简化开发
- **RedisTemplate** - 灵活的Redis操作，支持复杂场景

## 功能特性

### 核心功能
- ✅ Redis 连接配置和连接池优化
- ✅ Spring Cache 注解的声明式缓存
- ✅ RedisTemplate 的灵活操作
- ✅ 数据序列化配置（JSON格式）
- ✅ 缓存管理和监控

### 高级功能
- ✅ 分布式锁实现
- ✅ 计数器和限流器
- ✅ 批量操作优化
- ✅ 发布订阅模式
- ✅ Lua脚本执行
- ✅ 缓存预热和清理

## 技术栈

- Spring Boot 3.2.2
- Spring Data Redis
- Jackson JSON处理
- Lettuce连接池
- Lombok

## 快速开始

### 1. 启动Redis服务

确保Redis服务正在运行：
```bash
# 使用Docker启动Redis
docker run -d --name redis -p 6379:6379 redis:latest

# 或使用本地Redis服务
redis-server
```

### 2. 配置Redis连接

在 `application.yml` 中配置Redis连接信息：
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    password: # 如果有密码请填写
```

### 3. 启动应用

应用将在 `http://localhost:8083/redis` 启动

## API接口

### Redis测试接口

#### 测试Redis连接
```http
GET /api/redis/test/ping
```

#### 设置键值对
```http
POST /api/redis/test/set?key=testKey&value=testValue&expire=3600
```

#### 获取值
```http
GET /api/redis/test/get?key=testKey
```

#### 删除键
```http
DELETE /api/redis/test/delete?key=testKey
```

#### 检查键是否存在
```http
GET /api/redis/test/exists?key=testKey
```

#### 获取键的过期时间
```http
GET /api/redis/test/ttl?key=testKey
```

### 用户管理接口 (RedisTemplate方式)

#### 创建用户
```http
POST /api/redis/users
Content-Type: application/json

{
  "username": "张三",
  "email": "zhangsan@example.com",
  "age": 25
}
```

#### 获取用户
```http
GET /api/redis/users/{id}
```

#### 获取所有用户
```http
GET /api/redis/users
```

#### 更新用户
```http
PUT /api/redis/users/{id}
Content-Type: application/json

{
  "username": "李四",
  "email": "lisi@example.com",
  "age": 30
}
```

#### 删除用户
```http
DELETE /api/redis/users/{id}
```

#### 检查用户是否存在
```http
GET /api/redis/users/{id}/exists
```

#### 设置用户过期时间
```http
POST /api/redis/users/{id}/expire?timeout=3600
```

### Redis高级功能接口

#### 分布式锁演示
```http
POST /api/redis/advanced/lock
Content-Type: application/json

{
    "resource": "user_update_123"
}
```

#### 计数器操作
```http
POST /api/redis/advanced/counter/page_views?delta=1
```

#### 限流检查
```http
POST /api/redis/advanced/rate-limit/user123
```

#### 批量设置用户
```http
POST /api/redis/advanced/batch/users
Content-Type: application/json

[
    {"id": 1, "username": "user1", "email": "user1@example.com", "age": 25},
    {"id": 2, "username": "user2", "email": "user2@example.com", "age": 30}
]
```

#### 批量获取用户
```http
POST /api/redis/advanced/batch/users/get
Content-Type: application/json

[1, 2, 3]
```

#### 发布消息
```http
POST /api/redis/advanced/publish
Content-Type: application/json

{
    "channel": "user_events",
    "message": {"type": "user_created", "userId": 123}
}
```

## 响应格式

所有API接口都返回统一的JSON格式：

### 成功响应
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "id": 1234567890123,
    "username": "张三",
    "email": "zhangsan@example.com",
    "age": 25,
    "createTime": "2024-01-15 10:30:00",
    "updateTime": "2024-01-15 10:30:00"
  }
}
```

### 错误响应
```json
{
  "success": false,
  "message": "错误信息描述"
}
```

## 使用示例

### 1. 测试Redis连接
```bash
curl -X GET "http://localhost:8083/redis/api/redis/test/ping"
```

### 2. 创建用户
```bash
curl -X POST "http://localhost:8083/redis/api/redis/users" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "张三",
    "email": "zhangsan@example.com",
    "age": 25
  }'
```

### 3. 获取用户
```bash
curl -X GET "http://localhost:8083/redis/api/redis/users/1234567890123"
```

### 4. 更新用户
```bash
curl -X PUT "http://localhost:8083/redis/api/redis/users/1234567890123" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "李四",
    "email": "lisi@example.com",
    "age": 30
  }'
```

### 5. 删除用户
```bash
curl -X DELETE "http://localhost:8083/redis/api/redis/users/1234567890123"
```

## 最佳实践指南

### 1. 缓存策略选择

#### 优先使用 Spring Cache 注解
```java
// 推荐：声明式缓存，简洁易维护
@Cacheable(value = "users", key = "#id")
public User getUserById(Long id) {
    return userRepository.findById(id);
}

@CacheEvict(value = "users", key = "#id")
public boolean deleteUser(Long id) {
    return userRepository.deleteById(id);
}
```

#### 复杂场景使用 RedisTemplate
```java
// 复杂逻辑：分布式锁、批量操作、Lua脚本
public boolean acquireLock(String lockKey, String lockValue, long expireTime) {
    return redisTemplate.opsForValue()
        .setIfAbsent(lockKey, lockValue, Duration.ofSeconds(expireTime));
}
```

### 2. 缓存设计原则

#### 缓存Key设计
- 使用有意义的前缀：`user:123`、`order:456`
- 避免Key冲突：包含业务标识
- 合理设置过期时间：根据数据更新频率

#### 缓存更新策略
- **Cache-Aside**：应用程序管理缓存（推荐）
- **Write-Through**：写入时同步更新缓存
- **Write-Behind**：异步更新缓存

### 3. 性能优化

#### 连接池配置
```yaml
spring:
  redis:
    lettuce:
      pool:
        max-active: 20    # 最大连接数
        max-idle: 10      # 最大空闲连接
        min-idle: 5       # 最小空闲连接
        max-wait: 2000ms  # 最大等待时间
```

#### 序列化优化
- 使用 JSON 序列化（可读性好）
- 避免 JDK 序列化（性能差）
- 考虑压缩大对象

#### 批量操作
```java
// 使用Pipeline提高批量操作性能
redisTemplate.executePipelined((connection) -> {
    for (User user : users) {
        redisTemplate.opsForValue().set("user:" + user.getId(), user);
    }
    return null;
});
```

## 企业级使用建议

### 1. 架构设计
- **分层缓存**：L1(本地缓存) + L2(Redis缓存) + L3(数据库)
- **缓存隔离**：不同业务使用不同的Redis实例或数据库
- **读写分离**：读操作使用从节点，写操作使用主节点

### 2. 安全考虑
- 启用Redis认证
- 配置防火墙规则
- 禁用危险命令
- 定期备份数据

### 3. 高可用部署
- Redis Sentinel（哨兵模式）
- Redis Cluster（集群模式）
- 主从复制配置
- 故障自动切换

### 4. 开发规范
- 统一缓存Key命名规范
- 合理设置过期时间
- 避免大Key存储
- 定期清理无用缓存

### 5. 注意事项
- **数据一致性**：缓存与数据库的最终一致性
- **内存管理**：避免内存溢出，设置合理的淘汰策略
- **网络延迟**：考虑Redis与应用的网络延迟
- **序列化开销**：选择合适的序列化方式
- **异常处理**：Redis不可用时的降级策略

## 扩展功能

可以基于此模块扩展以下功能：
- 分布式锁（已集成Redisson）
- 消息发布订阅
- 缓存注解（@Cacheable等）
- Redis集群支持
- 数据持久化策略
- 缓存预热和更新策略