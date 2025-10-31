# WebSocket模块使用说明文档

## 📋 目录
- [模块概述](#模块概述)
- [架构设计](#架构设计)
- [已支持功能](#已支持功能)
- [配置说明](#配置说明)
- [使用指南](#使用指南)
- [安全机制](#安全机制)
- [性能优化](#性能优化)
- [尚未支持的功能](#尚未支持的功能)
- [故障排除](#故障排除)
- [最佳实践](#最佳实践)

---

## 🎯 模块概述

WebSocket模块是基于Spring Boot 3.2.2和Spring Security 6.x构建的实时通信解决方案，提供了完整的WebSocket支持，包括STOMP协议、消息代理、安全认证和连接管理等功能。

### 核心特性
- ✅ **STOMP协议支持** - 基于WebSocket的消息传输协议
- ✅ **JWT认证集成** - 安全的用户身份验证
- ✅ **消息代理** - 支持点对点和发布/订阅模式
- ✅ **SockJS降级** - 自动降级到轮询等传输方式
- ✅ **连接生命周期管理** - 完整的连接事件监听
- ✅ **消息拦截器** - 灵活的消息处理机制
- ✅ **任务调度** - 用户级别的定时任务管理

---

## 🏗️ 架构设计

### 模块结构
```
websocket/
├── src/main/java/cloud/catfish/ws/
│   ├── config/                    # 配置类
│   │   ├── WebSocketConfig.java              # 主配置
│   │   └── WebSocketConfigurationProperties.java  # 配置属性
│   ├── event/                     # 事件监听
│   │   └── WebSocketEventListener.java      # 连接事件监听器
│   ├── interceptor/               # 拦截器
│   │   ├── WebSocketChannelInterceptor.java # 消息通道拦截器
│   │   └── WebSocketHandshakeInterceptor.java # 握手拦截器
│   ├── schedule/                  # 任务调度
│   │   ├── MyTaskScheduler.java             # 自定义任务调度器
│   │   └── SchedulerConfig.java             # 调度器配置
│   └── security/                  # 安全模块
│       ├── AuthChannelInterceptorAdapter.java     # 认证拦截器
│       ├── WebSocketAuthenticationSecurityConfig.java # 认证配置
│       ├── WebSocketAuthenticatorService.java     # 认证服务
│       └── WebSocketAuthorizationSecurityConfig.java # 授权配置
└── pom.xml                        # 依赖配置
```

### 依赖关系
- **security模块** - JWT令牌验证
- **common模块** - 通用工具类（可选）
- **spring-boot-starter-websocket** - WebSocket核心支持
- **spring-security-messaging** - 消息安全支持

---

## ✅ 已支持功能

### 1. 连接管理
- **多端点支持**
  - `/ws` - 原生WebSocket端点
  - `/ws-sockjs` - 支持SockJS降级的端点
- **跨域配置** - 可配置的CORS支持
- **连接监控** - 实时连接状态跟踪

### 2. 消息传输
- **应用消息前缀**: `/app`
- **消息代理支持**:
  - `/topic` - 广播消息（一对多）
  - `/queue` - 队列消息（点对点）
- **用户目的地前缀**: `/user` - 用户专属消息

### 3. 安全认证
- **JWT令牌认证** - 基于Authorization头的认证
- **消息级授权** - 细粒度的权限控制
- **连接安全** - 握手阶段的安全验证

### 4. 事件处理
- **连接事件**: 用户连接/断开监听
- **订阅事件**: 主题订阅/取消订阅监听
- **异常处理**: 全局消息异常处理

### 5. 消息拦截
- **握手拦截** - 连接建立前的预处理
- **通道拦截** - 消息传输过程的拦截处理
- **认证拦截** - 自动JWT令牌验证

### 6. 任务调度
- **用户级任务** - 为每个用户创建独立的定时任务
- **任务生命周期** - 用户连接时创建，断开时清理
- **线程池管理** - 可配置的线程池大小

---

## ⚙️ 配置说明

### 应用配置 (application.yml)
```yaml
websocket:
  # 消息大小限制 (字节)
  message-size-limit: 65536          # 64KB
  # 发送缓冲区大小限制 (字节)  
  send-buffer-size-limit: 524288     # 512KB
  # 发送超时时间 (毫秒)
  send-time-limit: 20000             # 20秒
  # 首次消息超时时间 (毫秒)
  time-to-first-message: 30000       # 30秒
  # 心跳间隔时间 (毫秒)
  heartbeat-interval: 10000          # 10秒
  # SockJS心跳时间 (毫秒)
  sockjs-heartbeat-time: 25000       # 25秒
  # SockJS断开连接延迟 (毫秒)
  sockjs-disconnect-delay: 5000      # 5秒
  # 允许的跨域模式
  allowed-origin-patterns: "*"       # 生产环境建议具体配置
```

### 环境配置
```yaml
spring:
  profiles:
    active: dev  # dev环境禁用同源检查，prod环境启用
```

---

## 📖 使用指南

### 1. 前端连接示例

#### JavaScript (使用SockJS + STOMP)
```javascript
// 引入依赖
// <script src="https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js"></script>
// <script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7/bundles/stomp.umd.min.js"></script>

class WebSocketClient {
    constructor(token) {
        this.token = token;
        this.stompClient = null;
    }
    
    connect() {
        // 使用SockJS连接（推荐，支持降级）
        const socket = new SockJS('/ws-sockjs');
        this.stompClient = new StompJs.Client({
            webSocketFactory: () => socket,
            connectHeaders: {
                'Authorization': `Bearer ${this.token}`
            },
            debug: (str) => console.log(str),
            onConnect: (frame) => {
                console.log('Connected: ' + frame);
                this.subscribeToTopics();
            },
            onStompError: (frame) => {
                console.error('Broker reported error: ' + frame.headers['message']);
                console.error('Additional details: ' + frame.body);
            }
        });
        
        this.stompClient.activate();
    }
    
    // 原生WebSocket连接方式
    connectNative() {
        this.stompClient = new StompJs.Client({
            brokerURL: 'ws://localhost:8080/ws',
            connectHeaders: {
                'Authorization': `Bearer ${this.token}`
            },
            onConnect: (frame) => {
                console.log('Connected: ' + frame);
                this.subscribeToTopics();
            }
        });
        
        this.stompClient.activate();
    }
    
    subscribeToTopics() {
        // 订阅公共广播
        this.stompClient.subscribe('/topic/public', (message) => {
            console.log('Public message:', JSON.parse(message.body));
        });
        
        // 订阅个人消息队列
        this.stompClient.subscribe('/queue/notifications', (message) => {
            console.log('Personal notification:', JSON.parse(message.body));
        });
        
        // 订阅用户专属消息
        this.stompClient.subscribe('/user/queue/notifications', (message) => {
            console.log('User specific message:', JSON.parse(message.body));
        });
    }
    
    sendMessage(destination, message) {
        if (this.stompClient && this.stompClient.connected) {
            this.stompClient.publish({
                destination: destination,
                body: JSON.stringify(message)
            });
        }
    }
    
    disconnect() {
        if (this.stompClient) {
            this.stompClient.deactivate();
        }
    }
}

// 使用示例
const wsClient = new WebSocketClient('your-jwt-token');
wsClient.connect();
```

### 2. 后端消息发送示例

#### 在Controller中发送消息
```java
@RestController
@RequestMapping("/api/websocket")
public class WebSocketController {
    
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    
    // 发送广播消息
    @PostMapping("/broadcast")
    public ResponseEntity<?> sendBroadcast(@RequestBody MessageDto message) {
        messagingTemplate.convertAndSend("/topic/public", message);
        return ResponseEntity.ok("Message sent");
    }
    
    // 发送个人消息
    @PostMapping("/send-to-user")
    public ResponseEntity<?> sendToUser(
            @RequestParam String username, 
            @RequestBody MessageDto message) {
        messagingTemplate.convertAndSendToUser(
            username, "/queue/notifications", message);
        return ResponseEntity.ok("Message sent to user");
    }
}
```

### 3. 消息处理器示例

#### 创建消息处理Controller
```java
@Controller
public class WebSocketMessageController {
    
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    
    // 处理客户端发送的消息
    @MessageMapping("/chat")
    @SendTo("/topic/public")
    public MessageDto handleChatMessage(MessageDto message) {
        return message;
    }
    
    // 处理私聊消息
    @MessageMapping("/private")
    public void handlePrivateMessage(
            @Payload MessageDto message,
            Principal principal) {
        messagingTemplate.convertAndSendToUser(
            message.getTargetUser(), 
            "/queue/notifications", 
            message);
    }
}
```

---

## 🔒 安全机制

### 1. JWT认证流程
```
1. 客户端连接时在Authorization头中携带JWT令牌
2. AuthChannelInterceptorAdapter拦截CONNECT消息
3. WebSocketAuthenticatorService验证JWT令牌
4. 验证成功后设置用户认证信息
5. 后续消息基于认证信息进行授权检查
```

### 2. 权限控制
- **CONNECT/DISCONNECT/HEARTBEAT**: 需要认证
- **`/app/**`**: 应用消息需要认证
- **`/user/**`, `/queue/**`**: 订阅需要认证
- **`/topic/**`**: 订阅需要认证
- **其他消息**: 默认拒绝

### 3. 安全配置建议
```yaml
# 生产环境配置
websocket:
  allowed-origin-patterns: "https://yourdomain.com,https://www.yourdomain.com"

spring:
  profiles:
    active: prod  # 启用同源检查
```

---

## 🚀 性能优化

### 1. 连接池配置
```java
@Configuration
public class WebSocketPerformanceConfig {
    
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(20);  // 根据并发需求调整
        scheduler.setThreadNamePrefix("websocket-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        return scheduler;
    }
}
```

### 2. 消息大小优化
```yaml
websocket:
  message-size-limit: 32768      # 32KB，根据实际需求调整
  send-buffer-size-limit: 262144 # 256KB
```

### 3. 心跳优化
```yaml
websocket:
  heartbeat-interval: 30000      # 30秒，减少网络开销
  sockjs-heartbeat-time: 60000   # 60秒
```

---

## ❌ 尚未支持的功能

### 1. 高级消息功能
- **消息持久化** - 离线消息存储和重发
- **消息确认机制** - 消息送达确认
- **消息优先级** - 不同优先级的消息处理
- **消息过期** - 消息TTL支持

### 2. 集群支持
- **多实例消息同步** - 跨服务器实例的消息广播
- **负载均衡** - WebSocket连接的负载均衡
- **会话粘性** - 用户会话绑定到特定服务器

### 3. 高级安全功能
- **消息加密** - 端到端消息加密
- **速率限制** - 防止消息洪水攻击
- **IP白名单** - 基于IP的访问控制
- **设备指纹** - 设备级别的认证

### 4. 监控和管理
- **连接监控面板** - 实时连接状态监控
- **消息统计** - 消息发送/接收统计
- **性能指标** - 延迟、吞吐量等指标
- **管理API** - 强制断开连接等管理功能

### 5. 消息路由
- **复杂路由规则** - 基于内容的消息路由
- **消息转换** - 自动消息格式转换
- **消息聚合** - 多个消息的聚合处理

---

## 🔧 故障排除

### 1. 常见问题

#### 连接失败
```
问题: WebSocket连接建立失败
原因: JWT令牌无效或过期
解决: 检查Authorization头格式，确保令牌有效
```

#### 消息发送失败
```
问题: 消息无法发送到指定用户
原因: 用户未连接或目标地址错误
解决: 检查用户连接状态和目标地址格式
```

#### 跨域问题
```
问题: 浏览器报CORS错误
原因: 跨域配置不正确
解决: 配置allowed-origin-patterns参数
```

### 2. 调试技巧

#### 启用详细日志
```yaml
logging:
  level:
    cloud.catfish.ws: DEBUG
    org.springframework.messaging: DEBUG
    org.springframework.web.socket: DEBUG
```

#### 监控连接状态
```java
@EventListener
public void handleConnectEvent(SessionConnectedEvent event) {
    logger.info("User connected: {}", event.getUser().getName());
    // 添加连接统计逻辑
}
```

---

## 💡 最佳实践

### 1. 连接管理
- **连接池大小**: 根据预期并发用户数配置线程池
- **心跳设置**: 合理设置心跳间隔，平衡实时性和资源消耗
- **优雅断开**: 客户端主动断开连接，避免资源泄露

### 2. 消息设计
- **消息大小**: 控制单个消息大小，避免网络阻塞
- **消息频率**: 避免高频消息发送，考虑消息合并
- **错误处理**: 实现完善的错误处理和重试机制

### 3. 安全考虑
- **令牌刷新**: 实现JWT令牌自动刷新机制
- **权限检查**: 在消息处理前进行权限验证
- **输入验证**: 对所有输入消息进行验证和清理

### 4. 性能优化
- **消息批处理**: 对于高频消息，考虑批量处理
- **缓存策略**: 合理使用缓存减少数据库查询
- **异步处理**: 使用异步方式处理耗时操作

### 5. 监控和维护
- **连接监控**: 实时监控连接数和消息流量
- **日志记录**: 记录关键操作和异常信息
- **性能测试**: 定期进行压力测试和性能调优

---

## 📞 技术支持

如有问题或建议，请联系开发团队或提交Issue。

**文档版本**: v1.0  
**最后更新**: 2024年1月  
**适用版本**: Spring Boot 3.2.2, Spring Security 6.x