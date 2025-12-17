package cloud.catfish.data.kafka;

import cn.hutool.core.util.IdUtil;
import cloud.catfish.api.domain.IpTagRule;
import cloud.catfish.api.enums.NetworkProtocolEnum;
import cloud.catfish.common.util.IpUtil;
import cloud.catfish.elasticsearch9.service.NetworkLogService;
import cloud.catfish.mbg.example.IpTagRuleExample;
import cloud.catfish.mbg.mapper.IpTagRuleMapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
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

    private final NetworkLogService networkLogService;

    private final IpTagRuleMapper ipTagRuleMapper;
    // 缓存：Key 为网段，Value 为 IpTagRule 对象

    private static final Map<String, IpTagRule> IP_TAG_CACHE = new ConcurrentHashMap<>();

    /**
     * 初始化时加载规则，并定时刷新缓存 (每10分钟)
     */
    @PostConstruct
    @Scheduled(fixedRate = 600000)
    public void refreshRuleCache() {
        log.info("Start refreshing IP tag rule cache...");
        try {
            IpTagRuleExample example = new IpTagRuleExample();
            List<IpTagRule> rules = ipTagRuleMapper.selectByExample(example);

            if (rules != null && !rules.isEmpty()) {
                Map<String, IpTagRule> newCache = new HashMap<>();
                for (IpTagRule rule : rules) {
                    if (rule.getCidr() != null && !rule.getCidr().isEmpty()) {
                        newCache.put(rule.getCidr(), rule);
                    }
                }
                IP_TAG_CACHE.clear();
                IP_TAG_CACHE.putAll(newCache);
                log.info("Refreshed IP tag rule cache. Total rules: {}", IP_TAG_CACHE.size());
            } else {
                log.warn("No IP tag rules found in database.");
            }
        } catch (Exception e) {
            log.error("Failed to refresh IP tag rule cache", e);
        }
    }

    private boolean shouldProcess(String key) {
        if (key == null) {
            return false; // 默认处理 key 为空的
        }
        NetworkProtocolEnum protocol = NetworkProtocolEnum.getByProtocol(key);
        // 如果协议被标记为 skip，则返回 false (不处理)
        // 否则返回 true (处理)
        return !protocol.isSkip();
    }

    private void processData(String value) {
        try {
            // 1. 使用 FastJson 解析 JSON
            JSONObject json = JSON.parseObject(value);
            if (json == null) {
                log.warn("Value is not valid JSON: {}", value);
                return;
            }

            // 2. 获取srcIp，destIp
            String srcIp = json.getString("srcIp");
            String destIp = json.getString("destIp");

            if (srcIp == null && destIp == null) {
                log.warn("Both srcIp and destIp are missing in JSON: {}", value);
                return;
            }

            // 初始化必须的字段
            json.put("id", IdUtil.getSnowflakeNextId());
            json.put("src_ip", srcIp);
            json.put("dest_ip", destIp);
            
            // 3. 根据ip对数据打标
            if (srcIp != null) {
                tagIp(srcIp, json, "src");
            }
            if (destIp != null) {
                tagIp(destIp, json, "dest");
            }

            // 4. 打标完成后，数据写入到es
            networkLogService.createDocument(json);
            log.info("Document saved to ES: {}", json);

        } catch (IOException e) {
            log.error("Failed to save document to ES", e);
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }

    private void tagIp(String ip, JSONObject json, String prefix) {
        for (Map.Entry<String, IpTagRule> entry : IP_TAG_CACHE.entrySet()) {
            String cidr = entry.getKey();
            IpTagRule rule = entry.getValue();

            try {
                if (IpUtil.isInRange(ip, cidr)) {
                    json.put(prefix + "_cidr", cidr);
                    json.put(prefix + "_station_id", rule.getStationId());
                    json.put(prefix + "_station_name", rule.getStationName());
                    break;
                }
            } catch (Exception e) {
                // Ignore invalid IP or CIDR format
            }
        }
    }
}
