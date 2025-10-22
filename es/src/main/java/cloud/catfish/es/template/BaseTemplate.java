package cloud.catfish.es.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

/**
 * Elasticsearch模板基类
 * 提供公共的REST接口调用方法
 */
@Slf4j
public abstract class BaseTemplate {

    protected final RestClient restClient;
    protected final ObjectMapper objectMapper = new ObjectMapper();

    public BaseTemplate(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * 执行POST请求
     * 
     * @param endpoint 请求端点
     * @param jsonBody 请求体JSON字符串
     * @return 响应JSON字符串
     * @throws IOException IO异常
     */
    protected String executePostRequest(String endpoint, String jsonBody) throws IOException {
        Request request = new Request("POST", endpoint);
        request.setJsonEntity(jsonBody);
        
        Response response = restClient.performRequest(request);
        return new String(response.getEntity().getContent().readAllBytes());
    }

    /**
     * 执行GET请求
     * 
     * @param endpoint 请求端点
     * @return 响应JSON字符串
     * @throws IOException IO异常
     */
    protected String executeGetRequest(String endpoint) throws IOException {
        Request request = new Request("GET", endpoint);
        
        Response response = restClient.performRequest(request);
        return new String(response.getEntity().getContent().readAllBytes());
    }

    /**
     * 执行DELETE请求
     * 
     * @param endpoint 请求端点
     * @param jsonBody 请求体JSON字符串（可选）
     * @return 响应JSON字符串
     * @throws IOException IO异常
     */
    protected String executeDeleteRequest(String endpoint, String jsonBody) throws IOException {
        Request request = new Request("DELETE", endpoint);
        if (jsonBody != null) {
            request.setJsonEntity(jsonBody);
        }
        
        Response response = restClient.performRequest(request);
        return new String(response.getEntity().getContent().readAllBytes());
    }

    /**
     * 将SQL转换为DSL
     * 调用Elasticsearch的SQL翻译接口
     * 
     * @param sql SQL查询语句
     * @return DSL查询JSON字符串
     * @throws IOException IO异常
     */
    protected String translateSqlToDsl(String sql) throws IOException {
        String sqlJson = "{\"query\": \"" + sql.replace("\"", "\\\"") + "\"}";
        return executePostRequest("/_sql/translate", sqlJson);
    }

}