# WebSocket + Kafka 极端情况处理方案

## 问题描述
当仅有一个客户端频繁断开和建立连接时，可能导致：
- Kafka消费者频繁启停，浪费资源
- 系统不稳定，影响性能
- 连接状态判断不准确

## 解决方案

### 1. 防抖机制 (Debounce)
- **延迟时间**: 5秒防抖延迟
- **工作原理**: 连接数变化后，等待5秒再执行Kafka启停操作
- **优势**: 避免频繁操作，提高系统稳定性

### 2. 连接波动监控
- **波动计数**: 记录连接数变化次数
- **统计周期**: 可通过API重置统计
- **监控指标**: 实时显示连接波动情况

### 3. 强制控制机制
- **强制启动**: 绕过防抖机制，立即启动Kafka消费者
- **强制停止**: 绕过防抖机制，立即停止Kafka消费者
- **应用场景**: 紧急情况或测试需要

## 技术实现

### 核心组件
1. **WebSocketConnectionManager**: 连接管理和防抖控制
2. **ScheduledExecutorService**: 延迟任务调度
3. **AtomicInteger**: 线程安全的计数器

### 关键参数
```java
private static final long DEBOUNCE_DELAY_MS = 5000; // 5秒防抖延迟
private final AtomicInteger connectionFluctuations = new AtomicInteger(0);
private volatile ScheduledFuture<?> pendingKafkaStart;
private volatile ScheduledFuture<?> pendingKafkaStop;
```

### 防抖逻辑
```java
private void scheduleKafkaOperation(boolean shouldStart) {
    // 取消之前的调度
    cancelPendingOperations();
    
    // 增加波动计数
    connectionFluctuations.incrementAndGet();
    
    // 延迟执行
    if (shouldStart) {
        pendingKafkaStart = scheduler.schedule(() -> {
            kafkaConsumerService.startConsumer();
            pendingKafkaStart = null;
        }, DEBOUNCE_DELAY_MS, TimeUnit.MILLISECONDS);
    } else {
        pendingKafkaStop = scheduler.schedule(() -> {
            kafkaConsumerService.stopConsumer();
            pendingKafkaStop = null;
        }, DEBOUNCE_DELAY_MS, TimeUnit.MILLISECONDS);
    }
}
```

## API接口

### 状态查询
```
GET /ws/status
响应包含:
- alertTopicConnections: 连接数
- kafkaConsumerRunning: Kafka状态
- connectionFluctuations: 波动次数
- debounceStatus: 防抖状态
```

### 强制控制
```
POST /ws/kafka/force-start  # 强制启动
POST /ws/kafka/force-stop   # 强制停止
GET /ws/debounce/status     # 防抖详情
POST /ws/connections/reset  # 重置统计
```

## 测试页面功能

### 新增显示项
- **连接波动次数**: 实时显示连接变化统计
- **防抖状态**: 显示当前防抖状态（正常/防抖中）

### 新增控制按钮
- **强制启动Kafka**: 立即启动消费者
- **强制停止Kafka**: 立即停止消费者

### 状态颜色
- 绿色: 正常状态
- 黄色: 防抖中
- 红色: 错误状态

## 使用建议

### 正常情况
- 让系统自动管理，防抖机制会处理短暂的连接波动
- 监控连接波动次数，了解客户端连接稳定性

### 极端情况
1. **频繁断连**: 观察防抖状态，系统会自动稳定
2. **紧急启动**: 使用强制启动按钮
3. **测试场景**: 使用强制控制进行精确测试
4. **统计重置**: 定期重置连接统计，保持数据清洁

### 监控指标
- 连接数变化频率
- 防抖触发次数
- Kafka启停频率
- 系统资源使用情况

## 性能优化

### 资源管理
- 使用单线程调度器，避免资源浪费
- 及时取消未执行的任务
- 应用关闭时清理调度器

### 内存优化
- 使用AtomicInteger避免锁竞争
- volatile关键字保证可见性
- 合理的防抖延迟时间

## 故障排查

### 常见问题
1. **Kafka无法启动**: 检查Kafka服务状态和配置
2. **防抖不生效**: 检查调度器是否正常工作
3. **连接统计异常**: 使用重置接口清理数据

### 日志监控
- 连接建立/断开日志
- Kafka启停操作日志
- 防抖调度执行日志
- 异常情况告警日志

---

通过以上机制，系统能够有效处理客户端频繁连接断开的极端情况，保证服务稳定性和资源利用效率。