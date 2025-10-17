package cloud.catfish.es.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.cluster.HealthResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Elasticsearch 配置测试类
 * 测试 Elasticsearch 8.x 新版客户端连接
 */
@SpringBootTest
public class ElasticsearchConfigTest {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private RestClient restClient;

    /**
     * 测试 ElasticsearchClient 连接
     */
    @Test
    public void testElasticsearchClientConnection() {
        try {
            // 获取集群健康状态
            HealthResponse health = elasticsearchClient.cluster().health();
            assertNotNull(health);
            assertNotNull(health.clusterName());
            System.out.println("ElasticsearchClient 连接成功，集群名称: " + health.clusterName());
        } catch (Exception e) {
            fail("ElasticsearchClient 连接失败: " + e.getMessage());
        }
    }

    /**
     * 测试 RestClient 连接
     */
    @Test
    public void testRestClientConnection() {
        try {
            Request request = new Request("GET", "/_cluster/health");
            Response response = restClient.performRequest(request);
            assertEquals(200, response.getStatusLine().getStatusCode());
            System.out.println("RestClient 连接成功，状态码: " + response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            fail("RestClient 连接失败: " + e.getMessage());
        }
    }
}