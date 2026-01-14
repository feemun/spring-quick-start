package cloud.catfish.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * SpringSecurity白名单资源路径配置
 * Created by macro on 2018/11/5.
 */
@ConfigurationProperties(prefix = "secure.ignored")
public record IgnoreUrlsConfig(List<String> urls) {
    public IgnoreUrlsConfig {
        urls = (urls == null) ? List.of() : List.copyOf(urls);
    }
}
