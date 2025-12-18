package cloud.catfish.data.kafka;

import cn.hutool.core.util.IdUtil;
import cloud.catfish.api.domain.IpTagRule;
import cloud.catfish.api.enums.NetworkProtocolEnum;
import cloud.catfish.common.util.IpUtil;
import cloud.catfish.elasticsearch9.dto.NetworkLogDto;
import cloud.catfish.elasticsearch9.service.NetworkLogService;
import cloud.catfish.mbg.example.IpTagRuleExample;
import cloud.catfish.mbg.mapper.IpTagRuleMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class KafkaConsumerService {

    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private NetworkLogService networkLogService;
    @Resource
    private IpTagRuleMapper ipTagRuleMapper;

    // 缓存：Key 为网段，Value 为 IpTagRule 对象
    private static final Map<String, IpTagRule> IP_TAG_CACHE = new ConcurrentHashMap<>();

    @KafkaListener(topics = "data-import-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        log.info("Received message: key={}, value={}", record.key(), record.value());
        if (shouldProcess(record.key())) {
            processData(record.value());
        } else {
            log.info("Skipped message with key: {}", record.key());
        }
    }


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
            // 1. 使用 Jackson 解析 JSON 到 DTO
            NetworkLogDto logDto = objectMapper.readValue(value, NetworkLogDto.class);
            if (logDto == null) {
                log.warn("Parsed DTO is null: {}", value);
                return;
            }

            // 2. 获取srcIp，destIp
            String srcIp = logDto.getSrcIp();
            String destIp = logDto.getDestIp();

            if (srcIp == null && destIp == null) {
                log.warn("Both srcIp and destIp are missing in JSON: {}", value);
                return;
            }

            // 初始化必须的字段
            if (logDto.getId() == null) {
                logDto.setId(String.valueOf(IdUtil.getSnowflakeNextId()));
            }

            // 3. 根据ip对数据打标
            if (srcIp != null) {
                tagIp(srcIp, logDto, true);
            }
            if (destIp != null) {
                tagIp(destIp, logDto, false);
            }

            // 4. 打标完成后，数据写入到es
            networkLogService.createDocument(logDto);
            log.info("Document saved to ES: {}", logDto);

        } catch (IOException e) {
            log.error("Failed to save document to ES", e);
        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }

    private void tagIp(String ip, NetworkLogDto dto, boolean isSrc) {
        for (Map.Entry<String, IpTagRule> entry : IP_TAG_CACHE.entrySet()) {
            String cidr = entry.getKey();
            IpTagRule rule = entry.getValue();

            try {
                if (IpUtil.isInRange(ip, cidr)) {
                    if (isSrc) {
                        dto.setSrcCidr(cidr);
                        dto.setSrcStationId(rule.getStationId());
                        dto.setSrcStationName(rule.getStationName());
                    } else {
                        dto.setDestCidr(cidr);
                        dto.setDestStationId(rule.getStationId());
                        dto.setDestStationName(rule.getStationName());
                    }
                    break;
                }
            } catch (Exception e) {
                // Ignore invalid IP or CIDR format
            }
        }
    }
}
