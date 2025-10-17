package cloud.catfish.ws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;
import cloud.catfish.ws.interceptor.WebSocketHandshakeInterceptor;
import cloud.catfish.ws.interceptor.WebSocketChannelInterceptor;

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

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;
    
    @Autowired
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;
    
    @Autowired
    private WebSocketChannelInterceptor webSocketChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册原生WebSocket端点 (不使用SockJS)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(webSocketProperties.getAllowedOriginPatterns())
                .addInterceptors(webSocketHandshakeInterceptor);
        
        // 注册WebSocket端点，支持SockJS降级 (保留兼容性)
        registry.addEndpoint("/ws-sockjs")
                .setAllowedOriginPatterns(webSocketProperties.getAllowedOriginPatterns())
                .addInterceptors(webSocketHandshakeInterceptor)
                .withSockJS()
                .setHeartbeatTime(webSocketProperties.getSockjsHeartbeatTime())
                .setDisconnectDelay(webSocketProperties.getSockjsDisconnectDelay());
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 设置应用消息前缀
        registry.setApplicationDestinationPrefixes("/app");
        
        // 启用简单消息代理，并配置心跳
        registry.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{
                    webSocketProperties.getHeartbeatInterval(), 
                    webSocketProperties.getHeartbeatInterval()
                }) // 服务器发送心跳间隔，客户端期望心跳间隔
                .setTaskScheduler(taskScheduler); // 配置任务调度器以支持心跳功能
        
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
    
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 配置客户端入站通道拦截器
        registration.interceptors(webSocketChannelInterceptor);
    }
}