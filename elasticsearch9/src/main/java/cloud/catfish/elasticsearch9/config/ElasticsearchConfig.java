package cloud.catfish.elasticsearch9.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

import java.util.Arrays;

@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.hosts}")
    private String hosts;

    private RestClient lowLevelClient;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        String[] hostArray = Arrays.stream(hosts.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        HttpHost[] httpHosts = Arrays.stream(hostArray)
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);

        lowLevelClient = RestClient.builder(httpHosts).build();
        ElasticsearchTransport transport = new RestClientTransport(lowLevelClient,
                new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

    @PreDestroy
    public void close() {
        try {
            if (lowLevelClient != null) {
                lowLevelClient.close();
            }
        } catch (Exception ignored) {}
    }
}
