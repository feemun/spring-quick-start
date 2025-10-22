package cloud.catfish.es.template;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Elasticsearch DSL查询模板
 * 专门为@EsDSL注解提供DSL查询执行功能
 * 
 * 支持的查询类型：
 * - SEARCH: 搜索查询，返回文档列表
 * - COUNT: 计数查询，返回匹配文档数量
 * - AGGREGATION: 聚合查询，返回聚合结果
 */
@Slf4j
@Component
public class EsDslTemplate extends BaseTemplate {

    public EsDslTemplate(RestClient restClient) {
        super(restClient);
    }

    /**
     * 搜索查询 - @EsDSL SEARCH类型使用
     * 执行DSL查询并返回结果列表
     */
    public <T> T search(String index, String dslJson, Class<T> clazz) {
        try {
            String endpoint = "/" + index + "/_search";
            String result = executePostRequest(endpoint, dslJson);
            Map<String, Object> response = objectMapper.readValue(result, Map.class);
            return objectMapper.convertValue(response, clazz);
        } catch (Exception e) {
            log.error("DSL搜索查询失败 - index: {}, dsl: {}", index, dslJson, e);
            throw new RuntimeException("DSL搜索查询执行失败", e);
        }
    }

    /**
     * 分页搜索查询 - @EsDSL SEARCH类型带分页使用
     * 执行DSL查询并返回分页结果
     */
    public <T> T searchWithPagination(String index, String dslJson, int from, int size, Class<T> clazz) {
        try {
            // 解析原始DSL并添加分页参数
            Map<String, Object> dslMap = objectMapper.readValue(dslJson, Map.class);
            dslMap.put("from", from);
            dslMap.put("size", size);
            
            String pagedDslJson = objectMapper.writeValueAsString(dslMap);
            String endpoint = "/" + index + "/_search";
            String result = executePostRequest(endpoint, pagedDslJson);
            Map<String, Object> response = objectMapper.readValue(result, Map.class);
            return objectMapper.convertValue(response, clazz);
        } catch (Exception e) {
            log.error("DSL分页搜索查询失败 - index: {}, from: {}, size: {}, dsl: {}", index, from, size, dslJson, e);
            throw new RuntimeException("DSL分页搜索查询执行失败", e);
        }
    }

    /**
     * 计数查询 - @EsDSL COUNT类型使用
     * 执行DSL查询并返回匹配文档数量
     */
    public <T> T count(String index, String dslJson, Class<T> clazz) {
        try {
            // 解析DSL获取查询部分用于计数
            Map<String, Object> dslMap = objectMapper.readValue(dslJson, Map.class);
            Object queryObj = dslMap.get("query");
            
            String countJson = queryObj != null ? 
                objectMapper.writeValueAsString(Map.of("query", queryObj)) : "{}";
            
            String endpoint = "/" + index + "/_count";
            String result = executePostRequest(endpoint, countJson);
            
            Map<String, Object> response = objectMapper.readValue(result, Map.class);
            Long count = ((Number) response.get("count")).longValue();
            
            // 使用Jackson转换为指定的实体类型
            return objectMapper.convertValue(count, clazz);
        } catch (Exception e) {
            log.error("DSL计数查询失败 - index: {}, dsl: {}", index, dslJson, e);
            throw new RuntimeException("DSL计数查询执行失败", e);
        }
    }

    /**
     * 聚合查询 - @EsDSL AGGREGATION类型使用
     * 执行DSL聚合查询并返回聚合结果
     */
    public <T> T aggregation(String index, String dslJson, Class<T> clazz) {
        try {
            // 为聚合查询设置size=0，只返回聚合结果
            Map<String, Object> dslMap = objectMapper.readValue(dslJson, Map.class);
            dslMap.put("size", 0);
            String aggDslJson = objectMapper.writeValueAsString(dslMap);
            
            String endpoint = "/" + index + "/_search";
            String result = executePostRequest(endpoint, aggDslJson);
            
            Map<String, Object> response = objectMapper.readValue(result, Map.class);
            Map<String, Object> aggregations = (Map<String, Object>) response.get("aggregations");
            
            // 使用Jackson转换为指定的实体类型
            return objectMapper.convertValue(aggregations, clazz);
        } catch (Exception e) {
            log.error("DSL聚合查询失败 - index: {}, dsl: {}", index, dslJson, e);
            throw new RuntimeException("DSL聚合查询执行失败", e);
        }
    }

}