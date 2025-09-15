package cloud.catfish.redis.service.impl;

import cloud.catfish.redis.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Redis消息发布订阅服务实现类
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisMessageListenerContainer messageListenerContainer;
    private final ObjectMapper objectMapper;

    // 存储订阅的监听器，用于取消订阅
    private final Map<String, MessageListener> channelListeners = new ConcurrentHashMap<>();
    private final Map<String, MessageListener> patternListeners = new ConcurrentHashMap<>();

    @Override
    public void publish(String channel, Object message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            stringRedisTemplate.convertAndSend(channel, jsonMessage);
            log.debug("发布消息到频道 {}: {}", channel, jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("序列化消息失败: {}", message, e);
            throw new RuntimeException("发布消息失败", e);
        } catch (Exception e) {
            log.error("发布消息到频道 {} 失败: {}", channel, message, e);
            throw new RuntimeException("发布消息失败", e);
        }
    }

    @Override
    public void publishString(String channel, String message) {
        try {
            stringRedisTemplate.convertAndSend(channel, message);
            log.debug("发布字符串消息到频道 {}: {}", channel, message);
        } catch (Exception e) {
            log.error("发布字符串消息到频道 {} 失败: {}", channel, message, e);
            throw new RuntimeException("发布消息失败", e);
        }
    }

    @Override
    public void subscribe(String channel, Consumer<String> listener) {
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String messageBody = new String(message.getBody());
                    listener.accept(messageBody);
                    log.debug("接收到频道 {} 的消息: {}", channel, messageBody);
                } catch (Exception e) {
                    log.error("处理频道 {} 的消息失败", channel, e);
                }
            }
        };

        Topic topic = new PatternTopic(channel);
        messageListenerContainer.addMessageListener(messageListener, topic);
        channelListeners.put(channel, messageListener);
        
        log.info("订阅频道: {}", channel);
    }

    @Override
    public <T> void subscribe(String channel, Consumer<T> listener, Class<T> clazz) {
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    String messageBody = new String(message.getBody());
                    T obj = objectMapper.readValue(messageBody, clazz);
                    listener.accept(obj);
                    log.debug("接收到频道 {} 的对象消息: {}", channel, messageBody);
                } catch (Exception e) {
                    log.error("处理频道 {} 的对象消息失败", channel, e);
                }
            }
        };

        Topic topic = new PatternTopic(channel);
        messageListenerContainer.addMessageListener(messageListener, topic);
        channelListeners.put(channel, messageListener);
        
        log.info("订阅频道（对象消息）: {}", channel);
    }

    @Override
    public void subscribePattern(String pattern, Consumer<String> listener) {
        MessageListener messageListener = new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] patternBytes) {
                try {
                    String messageBody = new String(message.getBody());
                    String channelName = new String(message.getChannel());
                    listener.accept(messageBody);
                    log.debug("接收到模式 {} 匹配频道 {} 的消息: {}", pattern, channelName, messageBody);
                } catch (Exception e) {
                    log.error("处理模式 {} 的消息失败", pattern, e);
                }
            }
        };

        Topic topic = new PatternTopic(pattern);
        messageListenerContainer.addMessageListener(messageListener, topic);
        patternListeners.put(pattern, messageListener);
        
        log.info("订阅模式: {}", pattern);
    }

    @Override
    public void unsubscribe(String channel) {
        MessageListener listener = channelListeners.remove(channel);
        if (listener != null) {
            Topic topic = new PatternTopic(channel);
            messageListenerContainer.removeMessageListener(listener, topic);
            log.info("取消订阅频道: {}", channel);
        } else {
            log.warn("未找到频道 {} 的订阅", channel);
        }
    }

    @Override
    public void unsubscribePattern(String pattern) {
        MessageListener listener = patternListeners.remove(pattern);
        if (listener != null) {
            Topic topic = new PatternTopic(pattern);
            messageListenerContainer.removeMessageListener(listener, topic);
            log.info("取消订阅模式: {}", pattern);
        } else {
            log.warn("未找到模式 {} 的订阅", pattern);
        }
    }

    @Override
    public Long getSubscriberCount(String channel) {
        try {
            // 使用 PUBSUB NUMSUB 命令获取订阅者数量
            List<Object> result = stringRedisTemplate.execute(connection -> {
                return connection.execute("PUBSUB", "NUMSUB".getBytes(), channel.getBytes());
            });
            
            if (result != null && result.size() >= 2) {
                return Long.valueOf(result.get(1).toString());
            }
            return 0L;
        } catch (Exception e) {
            log.error("获取频道 {} 订阅者数量失败", channel, e);
            return 0L;
        }
    }

    @Override
    public List<String> getActiveChannels() {
        try {
            // 使用 PUBSUB CHANNELS 命令获取活跃频道
            return stringRedisTemplate.execute(connection -> {
                Object result = connection.execute("PUBSUB", "CHANNELS".getBytes());
                if (result instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<byte[]> channels = (List<byte[]>) result;
                    return channels.stream()
                            .map(String::new)
                            .toList();
                }
                return List.of();
            });
        } catch (Exception e) {
            log.error("获取活跃频道失败", e);
            return List.of();
        }
    }

    @Override
    public List<String> getChannelsByPattern(String pattern) {
        try {
            // 使用 PUBSUB CHANNELS pattern 命令获取匹配模式的频道
            return stringRedisTemplate.execute(connection -> {
                Object result = connection.execute("PUBSUB", "CHANNELS".getBytes(), pattern.getBytes());
                if (result instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<byte[]> channels = (List<byte[]>) result;
                    return channels.stream()
                            .map(String::new)
                            .toList();
                }
                return List.of();
            });
        } catch (Exception e) {
            log.error("获取模式 {} 匹配的频道失败", pattern, e);
            return List.of();
        }
    }
}