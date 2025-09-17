package cloud.catfish.websocket.handler;

import cloud.catfish.kafka.handler.KafkaMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket Kafka消息处理器实现
 * 负责将Kafka消息通过WebSocket广播给客户端
 */
@Component
public class WebSocketKafkaMessageHandler implements KafkaMessageHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketKafkaMessageHandler.class);
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * 处理告警消息
     * 将消息广播到/queue/broadcast/alert主题
     * 
     * @param message 告警消息内容
     */
    @Override
    public void handleAlertMessage(String message) {
        try {
            logger.info("准备广播告警消息到WebSocket客户端: {}", message);
            
            // 广播消息到/queue/broadcast/alert主题
            messagingTemplate.convertAndSend("/queue/broadcast/alert", message);
            
            logger.info("告警消息已成功广播到WebSocket客户端");
        } catch (Exception e) {
            logger.error("广播告警消息到WebSocket客户端时发生错误: {}", e.getMessage(), e);
        }
    }
}