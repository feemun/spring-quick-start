package cloud.catfish.es.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * Elasticsearch配置类
 * 
 * @author catfish
 * @since 1.0.0
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "cloud.catfish.es.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Value("${elasticsearch.username:}")
    private String username;

    @Value("${elasticsearch.password:}")
    private String password;

    @Value("${elasticsearch.connection-timeout:10s}")
    private String connectionTimeout;

    @Value("${elasticsearch.socket-timeout:30s}")
    private String socketTimeout;

    @Override
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration.ClientConfigurationBuilder builder = ClientConfiguration.builder()
                .connectedTo(host + ":" + port)
                .withConnectTimeout(java.time.Duration.parse("PT" + connectionTimeout.toUpperCase()))
                .withSocketTimeout(java.time.Duration.parse("PT" + socketTimeout.toUpperCase()));

        // 如果配置了用户名和密码，则添加认证
        if (!username.isEmpty() && !password.isEmpty()) {
            builder.withBasicAuth(username, password);
        }

        return builder.build();
    }
}