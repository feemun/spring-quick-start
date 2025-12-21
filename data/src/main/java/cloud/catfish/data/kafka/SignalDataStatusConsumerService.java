package cloud.catfish.data.kafka;

import cloud.catfish.api.domain.XlcSignalDataStatus;
import cloud.catfish.mbg.mapper.XlcSignalDataStatusMapper;
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
public class SignalDataStatusConsumerService {

    private final XlcSignalDataStatusMapper xlcSignalDataStatusMapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "bbb", groupId = "signal-data-status-group")
    public void consume(ConsumerRecord<String, String> record) {
        String value = record.value();
        if (!StringUtils.hasText(value)) {
            return;
        }

        try {
            List<XlcSignalDataStatus> statusList = objectMapper.readValue(value, new TypeReference<List<XlcSignalDataStatus>>() {});
            if (statusList != null && !statusList.isEmpty()) {
                for (XlcSignalDataStatus status : statusList) {
                    try {
                        xlcSignalDataStatusMapper.updateByPrimaryKey(status);
                    } catch (Exception e) {
                        log.error("Failed to update signal data status: {}", status, e);
                    }
                }
                log.info("Processed {} signal data status records from topic bbb", statusList.size());
            }
        } catch (Exception e) {
            log.error("Failed to parse signal data status message: {}", value, e);
        }
    }
}
