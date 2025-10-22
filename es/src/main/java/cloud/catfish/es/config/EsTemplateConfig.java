package cloud.catfish.es.config;

import cloud.catfish.es.template.EsDslTemplate;
import cloud.catfish.es.template.EsSqlTemplate;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ES 模板配置类
 * 配置 DSL 和 SQL 模板，使用 Elasticsearch 8.x 新版客户端
 */
@Configuration
public class EsTemplateConfig {

    /**
     * 配置 DSL 模板
     * 使用 restClient 进行 DSL 查询操作
     */
    @Bean
    public EsDslTemplate esDslTemplate(RestClient restClient) {
        return new EsDslTemplate(restClient);
    }

    /**
     * 配置 SQL 模板
     * 使用 RestClient 进行 SQL 转 DSL 查询操作
     */
    @Bean
    public EsSqlTemplate esSqlTemplate(RestClient restClient) {
        return new EsSqlTemplate(restClient);
    }
}