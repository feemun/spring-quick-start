package cloud.catfish.redis.service;

import java.util.function.Consumer;

/**
 * Redis消息发布订阅服务接口
 * 
 * @author catfish
 * @since 1.0.0
 */
public interface MessageService {

    /**
     * 发布消息
     * 
     * @param channel 频道
     * @param message 消息内容
     */
    void publish(String channel, Object message);

    /**
     * 发布消息到指定频道（字符串消息）
     * 
     * @param channel 频道
     * @param message 消息内容
     */
    void publishString(String channel, String message);

    /**
     * 订阅频道
     * 
     * @param channel  频道
     * @param listener 消息监听器
     */
    void subscribe(String channel, Consumer<String> listener);

    /**
     * 订阅频道（对象消息）
     * 
     * @param channel  频道
     * @param listener 消息监听器
     * @param clazz    消息类型
     * @param <T>      泛型
     */
    <T> void subscribe(String channel, Consumer<T> listener, Class<T> clazz);

    /**
     * 订阅模式匹配的频道
     * 
     * @param pattern  频道模式
     * @param listener 消息监听器
     */
    void subscribePattern(String pattern, Consumer<String> listener);

    /**
     * 取消订阅频道
     * 
     * @param channel 频道
     */
    void unsubscribe(String channel);

    /**
     * 取消订阅模式匹配的频道
     * 
     * @param pattern 频道模式
     */
    void unsubscribePattern(String pattern);

    /**
     * 获取频道的订阅者数量
     * 
     * @param channel 频道
     * @return 订阅者数量
     */
    Long getSubscriberCount(String channel);

    /**
     * 获取所有活跃的频道
     * 
     * @return 频道列表
     */
    java.util.List<String> getActiveChannels();

    /**
     * 获取匹配模式的频道
     * 
     * @param pattern 频道模式
     * @return 频道列表
     */
    java.util.List<String> getChannelsByPattern(String pattern);
}