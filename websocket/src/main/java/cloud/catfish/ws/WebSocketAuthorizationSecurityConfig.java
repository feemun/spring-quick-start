package cloud.catfish.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketAuthorizationSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        // 配置消息级别的授权
        messages
                // 连接和断开连接消息允许已认证用户
                .simpTypeMatchers(org.springframework.messaging.simp.SimpMessageType.CONNECT,
                                  org.springframework.messaging.simp.SimpMessageType.DISCONNECT,
                                  org.springframework.messaging.simp.SimpMessageType.HEARTBEAT).authenticated()
                // 应用消息需要认证
                .simpDestMatchers("/app/**").authenticated()
                // 用户私有队列和订阅需要认证
                .simpSubscribeDestMatchers("/user/**", "/queue/**").authenticated()
                // 公共频道允许已认证用户访问
                .simpSubscribeDestMatchers("/topic/**").authenticated()
                // 其他所有消息拒绝访问
                .anyMessage().denyAll();
    }

    // 生产环境建议启用同源检查以增强安全性
    // 开发环境可以设置为true，生产环境应设置为false
    @Override
    protected boolean sameOriginDisabled() {
        // 可以通过环境变量或配置文件控制
        String env = System.getProperty("spring.profiles.active", "dev");
        return "dev".equals(env) || "test".equals(env);
    }
}