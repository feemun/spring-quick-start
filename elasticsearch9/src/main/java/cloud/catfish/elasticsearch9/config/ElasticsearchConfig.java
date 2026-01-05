package cloud.catfish.elasticsearch9.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.ssl.SSLContextBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

import javax.net.ssl.SSLContext;
import java.util.List;

@Configuration
public class ElasticsearchConfig {

    @Autowired
    @Qualifier("customJacksonObjectMapper")
    private ObjectMapper objectMapper;


    @Value("${elasticsearch.hosts}")
    private List<String> hosts;

    @Value("${elasticsearch.username:}")
    private String username;

    @Value("${elasticsearch.password:}")
    private String password;

    @Value("${elasticsearch.apiKey:}")
    private String apiKey;

    @Value("${elasticsearch.connection-timeout:3000}")
    private int connectionTimeout;

    @Value("${elasticsearch.socket-timeout:30000}")
    private int socketTimeout;

    @Value("${elasticsearch.max-retry-timeout:60000}")
    private int maxRetryTimeout;

    @Value("${elasticsearch.max-conn-total:200}")
    private int maxConnTotal;

    @Value("${elasticsearch.max-conn-per-route:50}")
    private int maxConnPerRoute;

    @Value("${elasticsearch.verify-hostname:true}")
    private boolean verifyHostname;

    @Value("${elasticsearch.trust-all-cert:false}")
    private boolean trustAllCert;

    private RestClient lowLevelClient;

    @Bean
    public ElasticsearchClient elasticsearchClient() throws Exception {

        HttpHost[] httpHosts = hosts.stream()
                .map(HttpHost::create)
                .toArray(HttpHost[]::new);

        RestClientBuilder builder = RestClient.builder(httpHosts);

        // =======================
        // 连接池和超时配置
        // =======================
        builder.setRequestConfigCallback(config -> config
                .setConnectTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
        );

        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder
                    .setMaxConnTotal(maxConnTotal)
                    .setMaxConnPerRoute(maxConnPerRoute);

            // =======================
            // 身份认证
            // =======================
            if (apiKey != null && !apiKey.isEmpty()) {
                httpClientBuilder.addInterceptorLast((org.apache.http.HttpRequest request, org.apache.http.protocol.HttpContext context) -> {
                    request.addHeader("Authorization", "ApiKey " + apiKey);
                });
            } else if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(username, password));
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }

            // =======================
            // SSL 配置
            // =======================
            try {
                if (trustAllCert) {
                    SSLContextBuilder sslBuilder = new SSLContextBuilder();
                    // 信任全部证书 (不安全，仅测试环境用)
                    sslBuilder.loadTrustMaterial(null, (x, y) -> true);
                    SSLContext sslContext = sslBuilder.build();
                    httpClientBuilder.setSSLContext(sslContext);
                }

                if (!verifyHostname) {
                    httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return httpClientBuilder;
        });

        lowLevelClient = builder.build();

        // 使用注入的 objectMapper，保持与 Spring 全局配置一致
        ElasticsearchTransport transport =
                new RestClientTransport(lowLevelClient, new JacksonJsonpMapper(objectMapper));

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
