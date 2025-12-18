package cloud.catfish.elasticsearch9.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Resource
    private ObjectMapper objectMapper;


    @Bean
    public ElasticsearchClient elasticsearchClient() {

        // 1. Jackson ObjectMapper
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);

        // 2. 底层 REST Client（HTTP）
        RestClient restClient = RestClient.builder(
                new HttpHost("10.0.0.6", 19200, "http")
        ).build();

        // 3. Transport
        ElasticsearchTransport transport =
                new RestClientTransport(restClient, jsonpMapper);

        // 4. ElasticsearchClient
        return new ElasticsearchClient(transport);
    }

}
