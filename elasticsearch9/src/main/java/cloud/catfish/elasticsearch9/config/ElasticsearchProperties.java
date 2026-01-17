package cloud.catfish.elasticsearch9.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "elasticsearch")
public record ElasticsearchProperties(List<String> hosts) {
    public ElasticsearchProperties {
        hosts = (hosts == null) ? List.of() : List.copyOf(hosts);
    }
}

