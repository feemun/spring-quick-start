package cloud.catfish.elasticsearch9.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchConfig {

    @Bean(destroyMethod = "close")
    public RestClient lowLevelClient(ElasticsearchProperties properties) {
        HttpHost[] httpHosts = properties.hosts().stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);
        return RestClient.builder(httpHosts).build();
    }

    @Bean
    public ElasticsearchTransport elasticsearchTransport(RestClient lowLevelClient) {
        return new RestClientTransport(lowLevelClient, new JacksonJsonpMapper());
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }
}
