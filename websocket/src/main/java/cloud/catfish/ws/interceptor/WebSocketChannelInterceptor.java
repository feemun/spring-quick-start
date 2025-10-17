package cloud.catfish.ws.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

/**
 * WebSocket消息通道拦截器
 * 拦截和处理WebSocket消息传输
 */
@Component
public class WebSocketChannelInterceptor implements ChannelInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketChannelInterceptor.class);

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null) {
            StompCommand command = accessor.getCommand();
            
            if (command != null) {
                switch (command) {
                    case CONNECT:
                        logger.info("WebSocket CONNECT 消息拦截，会话ID: {}", accessor.getSessionId());
                        handleConnect(accessor);
                        break;
                    case DISCONNECT:
                        logger.info("WebSocket DISCONNECT 消息拦截，会话ID: {}", accessor.getSessionId());
                        handleDisconnect(accessor);
                        break;
                    case SUBSCRIBE:
                        logger.info("WebSocket SUBSCRIBE 消息拦截，目标: {}, 会话ID: {}", 
                                  accessor.getDestination(), accessor.getSessionId());
                        handleSubscribe(accessor);
                        break;
                    case UNSUBSCRIBE:
                        logger.info("WebSocket UNSUBSCRIBE 消息拦截，订阅ID: {}, 会话ID: {}", 
                                  accessor.getSubscriptionId(), accessor.getSessionId());
                        handleUnsubscribe(accessor);
                        break;
                    case SEND:
                        logger.debug("WebSocket SEND 消息拦截，目标: {}, 会话ID: {}", 
                                   accessor.getDestination(), accessor.getSessionId());
                        handleSend(accessor);
                        break;
                    default:
                        logger.debug("WebSocket {} 消息拦截，会话ID: {}", command, accessor.getSessionId());
                        break;
                }
            }
        }
        
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        if (!sent) {
            StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
            if (accessor != null) {
                logger.warn("消息发送失败，命令: {}, 会话ID: {}", 
                          accessor.getCommand(), accessor.getSessionId());
            }
        }
    }

    @Override
    public boolean preReceive(MessageChannel channel) {
        logger.debug("准备接收消息，通道: {}", channel);
        return true;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        if (message != null) {
            logger.debug("消息接收完成，通道: {}", channel);
        }
        return message;
    }

    /**
     * 处理连接事件
     */
    private void handleConnect(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        logger.info("客户端连接建立，会话ID: {}", sessionId);
        
        // 可以在这里添加连接统计、限流等逻辑
    }

    /**
     * 处理断开连接事件
     */
    private void handleDisconnect(StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        logger.info("客户端断开连接，会话ID: {}", sessionId);
        
        // 可以在这里添加清理逻辑
    }

    /**
     * 处理订阅事件
     */
    private void handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        
        logger.info("客户端订阅主题: {}, 会话ID: {}", destination, sessionId);
        
        // 可以在这里添加订阅权限检查、订阅统计等逻辑
    }

    /**
     * 处理取消订阅事件
     */
    private void handleUnsubscribe(StompHeaderAccessor accessor) {
        String subscriptionId = accessor.getSubscriptionId();
        String sessionId = accessor.getSessionId();
        
        logger.info("客户端取消订阅: {}, 会话ID: {}", subscriptionId, sessionId);
    }

    /**
     * 处理发送消息事件
     */
    private void handleSend(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        String sessionId = accessor.getSessionId();
        
        logger.debug("客户端发送消息到: {}, 会话ID: {}", destination, sessionId);
        
        // 可以在这里添加消息过滤、限流等逻辑
    }
}