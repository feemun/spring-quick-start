package cloud.catfish.admin.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

//@Service
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void consume(ConsumerRecord<String, String> record) {
        LOGGER.info("Consumed message: key = {}, value = {}, topic = {}, partition = {}",
                record.key(), record.value(), record.topic(), record.partition());
    }
}