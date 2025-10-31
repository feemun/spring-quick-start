package cloud.catfish.admin.controller.websocket;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * WebSocket消息处理控制器
 * 处理WebSocket消息的发送、接收和广播
 * Created for WebSocket demo
 */
@Tag(name = "WebSocketMessageController", description = "WebSocket消息处理")
@Controller
@RequestMapping("/websocket")
@Slf4j
public class WebSocketMessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 处理发送到 /app/send 的消息
     * 接收消息并回显给发送者
     */
    @MessageMapping("/send")
    @SendTo("/queue/messages")
    public Map<String, Object> handleMessage(@Payload String message, Principal principal) {
        log.info("收到来自用户 {} 的消息: {}", principal.getName(), message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "echo");
        response.put("originalMessage", message);
        response.put("sender", principal.getName());
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("response", "服务器已收到您的消息: " + message);
        
        return response;
    }

    /**
     * 处理广播消息
     * 发送到 /app/broadcast 的消息会广播给所有订阅 /topic/public 的用户
     */
    @MessageMapping("/broadcast")
    @SendTo("/topic/public")
    public Map<String, Object> handleBroadcast(@Payload String message, Principal principal) {
        log.info("用户 {} 发送广播消息: {}", principal.getName(), message);
        
        Map<String, Object> broadcast = new HashMap<>();
        broadcast.put("type", "broadcast");
        broadcast.put("message", message);
        broadcast.put("sender", principal.getName());
        broadcast.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return broadcast;
    }

    /**
     * 处理通知消息
     * 发送个人通知给特定用户
     */
    @MessageMapping("/notify")
    public void handleNotification(@Payload Map<String, Object> payload, Principal principal) {
        String targetUser = (String) payload.get("targetUser");
        String message = (String) payload.get("message");

        log.info("用户 {} 向用户 {} 发送通知: {}", principal.getName(), targetUser, message);
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "notification");
        notification.put("message", message);
        notification.put("from", principal.getName());
        notification.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // 发送给特定用户
        if (targetUser != null && !targetUser.isEmpty()) {
            messagingTemplate.convertAndSendToUser(targetUser, "/queue/notifications", notification);
        } else {
            // 如果没有指定目标用户，发送给发送者自己
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/notifications", notification);
        }
    }

    /**
     * 处理心跳消息
     */
    @MessageMapping("/heartbeat")
    public void handleHeartbeat(Principal principal) {
        log.debug("收到来自用户 {} 的心跳", principal.getName());
        
        Map<String, Object> heartbeatResponse = new HashMap<>();
        heartbeatResponse.put("type", "heartbeat");
        heartbeatResponse.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        heartbeatResponse.put("status", "alive");
        
        messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/notifications", heartbeatResponse);
    }

    /**
     * REST API: 发送系统通知给所有在线用户
     */
    @Operation(summary = "发送系统通知")
    @PostMapping("/system/notify")
    @ResponseBody
    public Map<String, Object> sendSystemNotification(@RequestBody Map<String, Object> payload) {
        String message = (String) payload.get("message");
        String level = (String) payload.getOrDefault("level", "info");

        log.info("发送系统通知: {} (级别: {})", message, level);
        
        Map<String, Object> systemNotification = new HashMap<>();
        systemNotification.put("type", "system");
        systemNotification.put("message", message);
        systemNotification.put("level", level);
        systemNotification.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // 广播给所有用户
        messagingTemplate.convertAndSend("/topic/public", systemNotification);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "系统通知已发送");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return response;
    }

    /**
     * REST API: 发送个人消息
     */
    @Operation(summary = "发送个人消息")
    @PostMapping("/user/send")
    @ResponseBody
    public Map<String, Object> sendUserMessage(@RequestBody Map<String, Object> payload) {
        String targetUser = (String) payload.get("targetUser");
        String message = (String) payload.get("message");
        
        if (targetUser == null || targetUser.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "目标用户不能为空");
            return errorResponse;
        }

        log.info("发送个人消息给用户 {}: {}", targetUser, message);
        
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("type", "personal");
        userMessage.put("message", message);
        userMessage.put("from", "system");
        userMessage.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        messagingTemplate.convertAndSendToUser(targetUser, "/queue/notifications", userMessage);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "个人消息已发送给用户: " + targetUser);
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return response;
    }

    /**
     * 获取在线用户统计信息
     */
    @Operation(summary = "获取WebSocket连接统计")
    @PostMapping("/stats")
    @ResponseBody
    public Map<String, Object> getWebSocketStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        stats.put("serverStatus", "running");
        stats.put("message", "WebSocket服务正常运行");
        
        // 这里可以添加更多统计信息，如在线用户数等
        // 需要结合SessionRegistry或自定义的用户会话管理来实现
        
        return stats;
    }
}