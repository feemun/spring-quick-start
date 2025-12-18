package cloud.catfish.elasticsearch9.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.Resource;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;

@Configuration
public class ElasticsearchConfig {

    @Resource
    private ObjectMapper objectMapper;


    @Bean
    public ElasticsearchClient elasticsearchClient() {

        // 1. Jackson ObjectMapper（非常重要）
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper();

        // 2. 底层 REST Client（HTTP）
        RestClient restClient = RestClient.builder(
                new HttpHost("10.0.0.6", 9200, "http")
        ).build();

        // 3. Transport（ES 9 的“传输层抽象”）
        ElasticsearchTransport transport =
                new RestClientTransport(restClient, jsonpMapper);

        // 4. ElasticsearchClient
        return new ElasticsearchClient(transport);
    }

}
