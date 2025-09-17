package cloud.catfish.kafka.handler;

/**
 * Kafka消息持久化处理器接口
 * 用于处理从Kafka消费的消息并进行持久化存储
 */
public interface KafkaMessagePersistenceHandler {
    
    /**
     * 持久化告警消息
     * @param message 告警消息内容
     */
    void persistAlertMessage(String message);
}