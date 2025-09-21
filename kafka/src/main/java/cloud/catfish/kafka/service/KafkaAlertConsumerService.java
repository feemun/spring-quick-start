package cloud.catfish.kafka.service;

import cloud.catfish.kafka.handler.KafkaMessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka告警消息消费者服务 - WebSocket广播专用
 * 负责实时广播告警消息，保障实时性
 */
@Service
public class KafkaAlertConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaAlertConsumerService.class);
    
    @Autowired
    private KafkaMessageHandler messageHandler;
    
    /**
     * Kafka消息监听器 - WebSocket广播专用
     * 消费topic_alert主题的消息并进行实时广播
     */
    @KafkaListener(
        topics = "topic_alert", 
        groupId = "websocket-broadcast-group", 
        autoStartup = "true",
        properties = {
            "auto.offset.reset=latest"  // 每次启动消费最新消息，丢弃未消费的旧数据
        }
    )
    public void consumeAlertMessage(ConsumerRecord<String, String> record) {
        try {
            String message = record.value();
            logger.debug("WebSocket广播组接收到Kafka告警消息: {}", message);
            
            // 委托给消息处理器处理
            messageHandler.handleAlertMessage(message);
            
        } catch (Exception e) {
            logger.error("处理Kafka告警消息失败", e);
        }
    }
    

}