package cloud.catfish.es.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch 配置类
 * 使用 Elasticsearch 8.x 新版客户端配置连接和认证信息
 */
@Configuration
public class ElasticsearchConfig {

    @Value("${elasticsearch.host:10.0.0.6}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Value("${elasticsearch.scheme:http}")
    private String scheme;

    @Value("${elasticsearch.username:elastic}")
    private String username;

    @Value("${elasticsearch.password:Elastic_HH8k4F}")
    private String password;

    @Value("${elasticsearch.connection-timeout:5000}")
    private int connectionTimeout;

    @Value("${elasticsearch.socket-timeout:30000}")
    private int socketTimeout;

    /**
     * 配置 RestClient
     * 用于低级别的 Elasticsearch REST API 操作
     */
    @Bean
    public RestClient restClient() {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
        );

        return RestClient.builder(
                new HttpHost(host, port, scheme)
        )
        .setHttpClientConfigCallback(httpClientBuilder ->
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
        )
        .setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                        .setConnectTimeout(connectionTimeout)
                        .setSocketTimeout(socketTimeout)
        )
        .build();
    }

    /**
     * 配置 ElasticsearchClient
     * 用于高级别的 Elasticsearch 操作（替代 RestHighLevelClient）
     */
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {
        // 创建传输层
        ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());
        
        // 创建客户端
        return new ElasticsearchClient(transport);
    }
}