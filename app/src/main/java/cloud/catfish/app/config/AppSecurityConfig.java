package cloud.catfish.app.config;

import cloud.catfish.security.config.SecurityConfig;
import org.springframework.context.annotation.Configuration;

/**
 * App模块安全配置
 * 继承基础安全配置，可以在这里自定义app模块特有的安全策略
 * 
 * @author catfish
 * @since 1.0.0
 */
@Configuration
public class AppSecurityConfig extends SecurityConfig {
    
    // 如果需要自定义安全配置，可以在这里重写相关方法
    // 目前使用父类的默认配置即可
}