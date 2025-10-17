package cloud.catfish.ws.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket握手拦截器
 * 在WebSocket连接建立前进行拦截处理
 */
@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        logger.info("WebSocket握手开始，请求URI: {}", request.getURI());
        
        // 获取客户端IP地址
        String clientIp = getClientIpAddress(request);
        attributes.put("clientIp", clientIp);
        
        // 记录握手时间
        attributes.put("handshakeTime", System.currentTimeMillis());
        
        logger.info("WebSocket握手预处理完成，客户端IP: {}", clientIp);
        
        return true; // 返回true允许握手继续
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        
        if (exception != null) {
            logger.error("WebSocket握手失败: {}", exception.getMessage(), exception);
        } else {
            logger.info("WebSocket握手成功完成，请求URI: {}", request.getURI());
        }
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(ServerHttpRequest request) {
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        // 如果没有代理，直接获取远程地址
        return request.getRemoteAddress() != null ? 
               request.getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }
}