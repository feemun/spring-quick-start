package cloud.catfish.data.kafka;

import cloud.catfish.api.domain.XlcDroneStatus;
import cloud.catfish.mbg.mapper.XlcDroneStatusMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DroneStatusConsumerService {

    private final XlcDroneStatusMapper xlcDroneStatusMapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "aaa", groupId = "drone-status-group")
    public void consume(ConsumerRecord<String, String> record) {
        String value = record.value();
        if (!StringUtils.hasText(value)) {
            return;
        }

        try {
            List<XlcDroneStatus> statusList = objectMapper.readValue(value, new TypeReference<List<XlcDroneStatus>>() {});
            if (statusList != null && !statusList.isEmpty()) {
                for (XlcDroneStatus status : statusList) {
                    try {
                        xlcDroneStatusMapper.updateByPrimaryKeySelective(status);
                    } catch (Exception e) {
                        log.error("Failed to update drone status: {}", status, e);
                    }
                }
                log.info("Processed {} drone status records from topic aaa", statusList.size());
            }
        } catch (Exception e) {
            log.error("Failed to parse drone status message: {}", value, e);
        }
    }
}
