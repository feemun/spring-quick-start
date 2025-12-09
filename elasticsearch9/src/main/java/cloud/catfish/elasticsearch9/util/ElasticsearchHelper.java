//package cloud.catfish.elasticsearch9.util;
//
//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.elasticsearch._types.Refresh;
//import co.elastic.clients.elasticsearch.core.BulkRequest;
//import co.elastic.clients.elasticsearch.core.BulkResponse;
//import co.elastic.clients.elasticsearch.core.IndexRequest;
//import co.elastic.clients.elasticsearch.core.IndexResponse;
//import co.elastic.clients.elasticsearch.core.SearchRequest;
//import co.elastic.clients.elasticsearch.core.SearchResponse;
//import co.elastic.clients.elasticsearch.sql.SqlQueryRequest;
//import co.elastic.clients.elasticsearch.sql.SqlQueryResponse;
//import co.elastic.clients.elasticsearch.sql.query.Column;
//import co.elastic.clients.json.JsonData;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.io.StringReader;
//import java.lang.reflect.Field;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.*;
//
///**
// * 统一 Elasticsearch 工具类
// * - 支持 SQL 查询
// * - 支持普通 Query DSL 查询
// * - 支持 Index / Bulk / Mapping / Template 操作
// * - 内置 APM 日志
// */
//@Slf4j
//@Component
//public class ElasticsearchHelper {
//
//    private final ElasticsearchClient client;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    public ElasticsearchHelper(ElasticsearchClient client) {
//        this.client = client;
//    }
//
//    // =================== ES|QL 查询 (New) ===================
//
//    /**
//     * 执行 ES|QL 查询 (支持管道语法 FROM index | LIMIT 10)
//     * Requires Elasticsearch 8.11+ and Java Client 8.13+
//     *
//     * 注意：由于 ObjectsEsqlAdapter 可能不可用，此处采用 JSON 解析模式
//     */
//    public <T> List<T> esqlQuery(String esql, Map<String, Object> params, Class<T> clazz) throws Exception {
//        String parsedEsql = parseSqlWithParams(esql, params);
//        Instant start = Instant.now();
//
//        // 直接获取 BinaryResponse 并手动解析 JSON
//        // format="json" 返回对象数组 [{"field": val}, ...]
//        co.elastic.clients.transport.endpoints.BinaryResponse response = client.esql().query(q -> q
//                .format("json")
//                .query(parsedEsql)
//        );
//
//        List<T> list = objectMapper.readValue(response.content(),
//                objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
//
//        Instant end = Instant.now();
//        log.info("[ES-APM] ES|QL executed in {}ms: {}", Duration.between(start, end).toMillis(), parsedEsql);
//        return list;
//    }
//
//    public List<Map<String, Object>> esqlQuery(String esql, Map<String, Object> params) throws Exception {
//        // 使用 Map.class 接收动态结果
//        // 注意：ObjectsEsqlAdapter 可能将结果映射为 LinkedHashMap
//        return (List) esqlQuery(esql, params, Map.class);
//    }
//
//    // =================== 普通 Query DSL 查询 ===================
//
//    public SearchResponse<JsonData> queryDsl(String index, String queryJson) throws Exception {
//        Instant start = Instant.now();
//        // 修正：使用 withJson 而非 raw，且需要 StringReader
//        SearchRequest request = SearchRequest.of(r -> r
//                .index(index)
//                .query(q -> q.withJson(new StringReader(queryJson)))
//        );
//        SearchResponse<JsonData> response = client.search(request, JsonData.class);
//        Instant end = Instant.now();
//        log.info("[ES-APM] DSL executed in {}ms, index: {}", Duration.between(start, end).toMillis(), index);
//        return response;
//    }
//
//    // =================== Index / Bulk ===================
//
//    public <T> IndexResponse index(String index, String id, T document) throws Exception {
//        IndexRequest<T> request = IndexRequest.of(r -> r.index(index).id(id).document(document).refresh(Refresh.True));
//        return client.index(request);
//    }
//
//    public BulkResponse bulkIndex(String index, List<Map<String, Object>> documents) throws Exception {
//        BulkRequest.Builder br = new BulkRequest.Builder();
//        for (Map<String, Object> doc : documents) {
//            br.operations(op -> op.index(idx -> idx.index(index).document(doc)));
//        }
//        return client.bulk(br.build());
//    }
//
//    // =================== 私有方法 ===================
//
//    private String parseSqlWithParams(String sql, Map<String, Object> params) {
//        if (params == null || params.isEmpty()) return sql;
//        String parsed = sql;
//        for (Map.Entry<String, Object> entry : params.entrySet()) {
//            String key = ":" + entry.getKey();
//            Object value = entry.getValue();
//            // 简单防注入处理（并不完美，生产建议使用预编译语句如果驱动支持）
//            String valueStr = value instanceof String ? "'" + value.toString().replace("'", "''") + "'" : value.toString();
//            parsed = parsed.replace(key, valueStr);
//        }
//        return parsed;
//    }
//
//}
