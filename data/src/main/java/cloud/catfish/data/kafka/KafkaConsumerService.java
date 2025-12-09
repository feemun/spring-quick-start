package cloud.catfish.data.kafka;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cloud.catfish.elasticsearch9.model.NetworkLogDocument;
import cloud.catfish.elasticsearch9.service.NetworkLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NetworkLogService networkLogService;

    // 模拟字典：Key 为网段，Value 为标签信息
    private static final Map<String, Object> IP_TAG_DICTIONARY = new HashMap<>();

    static {
        IP_TAG_DICTIONARY.put("192.168.1.0/24", "Internal-Office");
        IP_TAG_DICTIONARY.put("10.0.0.0/8", "Data-Center");
        IP_TAG_DICTIONARY.put("172.16.0.0/12", "Cloud-VPC");
    }

    @KafkaListener(topics = "data-import-topic")
    public void consume(ConsumerRecord<String, String> record) {
        String key = record.key();
        String value = record.value();

        log.info("Received Kafka message - Key: {}, Value: {}", key, value);

        if (shouldProcess(key)) {
            processData(value);
        } else {
            log.info("Skipping message with key: {}", key);
        }
    }

    private boolean shouldProcess(String key) {
        return "PROCESS".equalsIgnoreCase(key);
    }

    private void processData(String value) {
        try {
            // 1. 把字符串转化为json格式
            if (!JSONUtil.isTypeJSON(value)) {
                log.warn("Value is not valid JSON: {}", value);
                return;
            }
            JSONObject json = JSONUtil.parseObj(value);

            // 2. 获取srcIp，destIp
            String srcIp = json.getStr("srcIp");
            String destIp = json.getStr("destIp");

            if (srcIp == null || destIp == null) {
                log.warn("Missing srcIp or destIp in JSON: {}", value);
                return;
            }

            // 3. 根据ip对数据打标
            List<String> tags = new ArrayList<>();
            tagIp(srcIp, tags);
            tagIp(destIp, tags);

            // 4. 打标完成后，数据写入到es
            NetworkLogDocument document = new NetworkLogDocument();
            document.setId(IdUtil.fastSimpleUUID());
            document.setSrcIp(srcIp);
            document.setDestIp(destIp);
            document.setTags(tags);
            document.setExtraInfo(json);

            networkLogService.createDocument(document);
            log.info("Document saved to ES: {}", document);

        } catch (IOException e) {
            log.error("Failed to save document to ES", e);
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }

    private void tagIp(String ip, List<String> tags) {
        for (Map.Entry<String, Object> entry : IP_TAG_DICTIONARY.entrySet()) {
            String cidr = entry.getKey();
            Object tag = entry.getValue();

            try {
                if (NetUtil.isInRange(ip, cidr)) {
                    tags.add(tag.toString());
                }
            } catch (Exception e) {
                // Ignore invalid IP or CIDR format
            }
        }
    }
}
