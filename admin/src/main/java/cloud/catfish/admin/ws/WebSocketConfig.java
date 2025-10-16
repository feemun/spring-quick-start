package cloud.catfish.admin.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket配置类
 * 配置STOMP消息代理和端点
 * Created by rajeevkumarsingh on 24/07/17.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketConfigurationProperties webSocketProperties;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点，支持SockJS降级
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(webSocketProperties.getAllowedOriginPatterns()) // 从配置文件读取
                .withSockJS()
                .setHeartbeatTime(webSocketProperties.getSockjsHeartbeatTime()) // 设置心跳间隔
                .setDisconnectDelay(webSocketProperties.getSockjsDisconnectDelay()); // 设置断开连接延迟
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 设置应用消息前缀
        registry.setApplicationDestinationPrefixes("/app");
        
        // 启用简单消息代理，并配置心跳
        registry.enableSimpleBroker("/public", "/queue")
                .setHeartbeatValue(new long[]{
                    webSocketProperties.getHeartbeatInterval(), 
                    webSocketProperties.getHeartbeatInterval()
                }) // 服务器发送心跳间隔，客户端期望心跳间隔
                .setTaskScheduler(null); // 可以配置自定义任务调度器
        
        // 设置用户目的地前缀，用于点对点消息
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        // 配置WebSocket传输参数，从配置属性读取
        registry.setMessageSizeLimit(webSocketProperties.getMessageSizeLimit()) // 消息大小限制
                .setSendBufferSizeLimit(webSocketProperties.getSendBufferSizeLimit()) // 发送缓冲区大小
                .setSendTimeLimit(webSocketProperties.getSendTimeLimit()) // 发送超时
                .setTimeToFirstMessage(webSocketProperties.getTimeToFirstMessage()); // 首次消息超时
    }
}