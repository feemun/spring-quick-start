package cloud.catfish.admin.ws;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * WebSocket配置属性类
 * 用于外部化配置WebSocket相关参数
 */
@Component
@ConfigurationProperties(prefix = "websocket")
public class WebSocketConfigurationProperties {
    
    /**
     * 消息大小限制 (字节)
     */
    private int messageSizeLimit = 64 * 1024; // 64KB
    
    /**
     * 发送缓冲区大小限制 (字节)
     */
    private int sendBufferSizeLimit = 512 * 1024; // 512KB
    
    /**
     * 发送超时时间 (毫秒)
     */
    private int sendTimeLimit = 20000; // 20秒
    
    /**
     * 首次消息超时时间 (毫秒)
     */
    private int timeToFirstMessage = 30000; // 30秒
    
    /**
     * 心跳间隔时间 (毫秒)
     */
    private long heartbeatInterval = 10000; // 10秒
    
    /**
     * SockJS心跳时间 (毫秒)
     */
    private long sockjsHeartbeatTime = 25000; // 25秒
    
    /**
     * SockJS断开连接延迟 (毫秒)
     */
    private long sockjsDisconnectDelay = 5000; // 5秒
    
    /**
     * 允许的跨域模式
     */
    private String allowedOriginPatterns = "*";
    
    // Getters and Setters
    public int getMessageSizeLimit() {
        return messageSizeLimit;
    }
    
    public void setMessageSizeLimit(int messageSizeLimit) {
        this.messageSizeLimit = messageSizeLimit;
    }
    
    public int getSendBufferSizeLimit() {
        return sendBufferSizeLimit;
    }
    
    public void setSendBufferSizeLimit(int sendBufferSizeLimit) {
        this.sendBufferSizeLimit = sendBufferSizeLimit;
    }
    
    public int getSendTimeLimit() {
        return sendTimeLimit;
    }
    
    public void setSendTimeLimit(int sendTimeLimit) {
        this.sendTimeLimit = sendTimeLimit;
    }
    
    public int getTimeToFirstMessage() {
        return timeToFirstMessage;
    }
    
    public void setTimeToFirstMessage(int timeToFirstMessage) {
        this.timeToFirstMessage = timeToFirstMessage;
    }
    
    public long getHeartbeatInterval() {
        return heartbeatInterval;
    }
    
    public void setHeartbeatInterval(long heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
    
    public long getSockjsHeartbeatTime() {
        return sockjsHeartbeatTime;
    }
    
    public void setSockjsHeartbeatTime(long sockjsHeartbeatTime) {
        this.sockjsHeartbeatTime = sockjsHeartbeatTime;
    }
    
    public long getSockjsDisconnectDelay() {
        return sockjsDisconnectDelay;
    }
    
    public void setSockjsDisconnectDelay(long sockjsDisconnectDelay) {
        this.sockjsDisconnectDelay = sockjsDisconnectDelay;
    }
    
    public String getAllowedOriginPatterns() {
        return allowedOriginPatterns;
    }
    
    public void setAllowedOriginPatterns(String allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }
}