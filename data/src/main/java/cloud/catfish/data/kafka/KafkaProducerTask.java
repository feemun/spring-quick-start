package cloud.catfish.data.kafka;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerTask {

    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private JsonNode testData;
    private final AtomicInteger index = new AtomicInteger(0);
    private static final String TOPIC = "data-import-topic";

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("test_network_logs.json");
            byte[] bytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String jsonStr = new String(bytes, StandardCharsets.UTF_8);
            testData = objectMapper.readTree(jsonStr);
            log.info("Loaded {} test records from test_network_logs.json", testData.size());
        } catch (IOException e) {
            log.error("Failed to load test data: {}", e.getMessage());
        }
    }

    @Scheduled(fixedRate = 5000)
    public void sendTestMessage() {
        if (testData == null || testData.size() == 0) {
            return;
        }

        int i = index.getAndIncrement() % testData.size();
        JsonNode node = testData.get(i);
        String message = node.toString();
        
        // 模拟提取协议作为Key，Consumer会根据Key过滤
        String protocol = node.has("protocol") ? node.get("protocol").asText() : "HTTP";

        kafkaTemplate.send(TOPIC, protocol, message);
        log.info("Sent test message to Kafka [{}]: key={}, value={}", TOPIC, protocol, message);
    }
}
