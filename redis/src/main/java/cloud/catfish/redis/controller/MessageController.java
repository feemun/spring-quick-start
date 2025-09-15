package cloud.catfish.redis.controller;

import cloud.catfish.redis.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

/**
 * 消息发布订阅控制器
 * 
 * @author catfish
 * @since 1.0.0
 */
@Tag(name = "消息发布订阅", description = "Redis消息队列操作接口")
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "发布消息", description = "向指定频道发布消息")
    @PostMapping("/publish")
    public ResponseEntity<String> publish(
            @Parameter(description = "频道名称") @RequestParam String channel,
            @Parameter(description = "消息内容") @RequestBody String message) {
        Long subscribers = messageService.publish(channel, message);
        return ResponseEntity.ok("消息已发布到 " + subscribers + " 个订阅者");
    }

    @Operation(summary = "订阅频道", description = "订阅指定频道的消息")
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(
            @Parameter(description = "频道名称") @RequestParam String channel) {
        // 创建一个简单的消息监听器
        MessageListener listener = (message, pattern) -> {
            System.out.println("收到消息 - 频道: " + new String(message.getChannel()) + 
                             ", 内容: " + new String(message.getBody()));
        };
        
        messageService.subscribe(listener, channel);
        return ResponseEntity.ok("已订阅频道: " + channel);
    }

    @Operation(summary = "模式订阅", description = "使用模式订阅匹配的频道")
    @PostMapping("/pattern-subscribe")
    public ResponseEntity<String> patternSubscribe(
            @Parameter(description = "频道模式") @RequestParam String pattern) {
        // 创建一个简单的消息监听器
        MessageListener listener = (message, patternBytes) -> {
            System.out.println("收到模式消息 - 模式: " + new String(patternBytes) + 
                             ", 频道: " + new String(message.getChannel()) + 
                             ", 内容: " + new String(message.getBody()));
        };
        
        messageService.patternSubscribe(listener, pattern);
        return ResponseEntity.ok("已订阅模式: " + pattern);
    }

    @Operation(summary = "取消订阅", description = "取消订阅指定频道")
    @PostMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(
            @Parameter(description = "频道名称") @RequestParam String channel) {
        messageService.unsubscribe(channel);
        return ResponseEntity.ok("已取消订阅频道: " + channel);
    }

    @Operation(summary = "取消模式订阅", description = "取消模式订阅")
    @PostMapping("/pattern-unsubscribe")
    public ResponseEntity<String> patternUnsubscribe(
            @Parameter(description = "频道模式") @RequestParam String pattern) {
        messageService.patternUnsubscribe(pattern);
        return ResponseEntity.ok("已取消订阅模式: " + pattern);
    }

    @Operation(summary = "获取订阅者数量", description = "获取指定频道的订阅者数量")
    @GetMapping("/subscribers-count")
    public ResponseEntity<Long> getSubscribersCount(
            @Parameter(description = "频道名称") @RequestParam String channel) {
        Long count = messageService.getSubscribersCount(channel);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "获取活跃频道", description = "获取当前活跃的频道列表")
    @GetMapping("/active-channels")
    public ResponseEntity<Collection<String>> getActiveChannels() {
        Collection<String> channels = messageService.getActiveChannels();
        return ResponseEntity.ok(channels);
    }

    @Operation(summary = "获取匹配频道", description = "获取匹配指定模式的频道列表")
    @GetMapping("/channels-by-pattern")
    public ResponseEntity<Collection<String>> getChannelsByPattern(
            @Parameter(description = "频道模式") @RequestParam String pattern) {
        Collection<String> channels = messageService.getChannelsByPattern(pattern);
        return ResponseEntity.ok(channels);
    }

    @Operation(summary = "批量发布消息", description = "向多个频道批量发布消息")
    @PostMapping("/batch-publish")
    public ResponseEntity<String> batchPublish(
            @Parameter(description = "频道列表，逗号分隔") @RequestParam String channels,
            @Parameter(description = "消息内容") @RequestBody String message) {
        String[] channelArray = channels.split(",");
        long totalSubscribers = 0;
        
        for (String channel : channelArray) {
            Long subscribers = messageService.publish(channel.trim(), message);
            totalSubscribers += subscribers;
        }
        
        return ResponseEntity.ok("消息已发布到 " + channelArray.length + " 个频道，共 " + totalSubscribers + " 个订阅者");
    }

    @Operation(summary = "发布延迟消息", description = "发布延迟消息（模拟实现）")
    @PostMapping("/publish-delayed")
    public ResponseEntity<String> publishDelayed(
            @Parameter(description = "频道名称") @RequestParam String channel,
            @Parameter(description = "消息内容") @RequestBody String message,
            @Parameter(description = "延迟时间（秒）") @RequestParam long delaySeconds) {
        
        // 使用异步方式实现延迟发布
        new Thread(() -> {
            try {
                Thread.sleep(delaySeconds * 1000);
                messageService.publish(channel, message);
                System.out.println("延迟消息已发布到频道: " + channel);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("延迟消息发布被中断");
            }
        }).start();
        
        return ResponseEntity.ok("延迟消息已安排，将在 " + delaySeconds + " 秒后发布到频道: " + channel);
    }
}