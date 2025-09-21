package cloud.catfish.kafka.service;

import cloud.catfish.kafka.handler.KafkaMessagePersistenceHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka告警消息持久化消费者服务
 * 负责将告警消息持久化存储，便于回溯查询
 */
@Service
public class KafkaAlertPersistenceService {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaAlertPersistenceService.class);
    
    @Autowired
    private KafkaMessagePersistenceHandler persistenceHandler;
    
    /**
     * Kafka消息监听器 - 持久化专用
     * 消费topic_alert主题的消息并进行持久化存储
     */
    @KafkaListener(
        topics = "topic_alert", 
        groupId = "alert-persistence-group", 
        autoStartup = "true",
        properties = {
            "auto.offset.reset=earliest"  // 从上次消费位置继续，确保数据不丢失
        }
    )
    public void consumeAlertMessage(ConsumerRecord<String, String> record) {
        try {
            String message = record.value();
            logger.debug("持久化组接收到Kafka告警消息: {}", message);
            
            // 委托给持久化处理器处理
            persistenceHandler.persistAlertMessage(message);
            
            logger.info("告警消息持久化成功: offset={}, partition={}", record.offset(), record.partition());
            
        } catch (Exception e) {
            logger.error("持久化Kafka告警消息失败: offset={}, partition={}", record.offset(), record.partition(), e);
        }
    }
}