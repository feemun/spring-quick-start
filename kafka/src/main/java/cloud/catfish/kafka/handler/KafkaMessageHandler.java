package cloud.catfish.kafka.handler;

/**
 * Kafka消息处理器接口
 * 用于处理从Kafka消费的消息
 */
public interface KafkaMessageHandler {
    
    /**
     * 处理告警消息
     * @param message 告警消息内容
     */
    void handleAlertMessage(String message);
}