package cloud.catfish.elasticsearch9.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.sql.SqlQueryRequest;
import co.elastic.clients.elasticsearch.sql.SqlQueryResponse;
import co.elastic.clients.elasticsearch.sql.query.Column;
import co.elastic.clients.json.JsonData;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * 统一 Elasticsearch 工具类
 * - 支持 ES|QL 查询
 * - 支持普通 Query DSL 查询
 * - 支持 Index / Bulk / Mapping / Template 操作
 * - 内置 APM 日志
 */
@Component
public class ElasticsearchHelper {

    private final ElasticsearchClient client;

    public ElasticsearchHelper(ElasticsearchClient client) {
        this.client = client;
    }

    // =================== ES|QL 查询 ===================

    public List<Map<String, Object>> sqlQuery(String sql, Map<String, Object> params, int fetchSize) throws Exception {
        String parsedSql = parseSqlWithParams(sql, params);
        Instant start = Instant.now();

        List<Map<String, Object>> results = new ArrayList<>();
        SqlQueryRequest request = SqlQueryRequest.of(r -> r.query(parsedSql).fetchSize(fetchSize));
        SqlQueryResponse response = client.sql().query(request);

        List<Column> columns = response.columns();
        addRowsToResult(response.rows(), columns, results);

        String cursor = response.cursor();
        while (cursor != null && !cursor.isEmpty()) {
            SqlQueryResponse nextPage = client.sql().query(q -> q.cursor(cursor));
            addRowsToResult(nextPage.rows(), columns, results);
            cursor = nextPage.cursor();
        }

        Instant end = Instant.now();
        System.out.println("[ES-APM] SQL executed in " + Duration.between(start, end).toMillis() + "ms: " + parsedSql);
        return results;
    }

    public <T> List<T> sqlQueryToDto(String sql, Map<String, Object> params, Class<T> clazz) throws Exception {
        List<Map<String, Object>> results = sqlQuery(sql, params, 1000);
        List<T> dtoList = new ArrayList<>();
        for (Map<String, Object> row : results) {
            T obj = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (row.containsKey(field.getName())) {
                    Object value = row.get(field.getName());
                    field.set(obj, convertValue(value, field.getType()));
                }
            }
            dtoList.add(obj);
        }
        return dtoList;
    }

    // =================== 普通 Query DSL 查询 ===================

    public SearchResponse<JsonData> queryDsl(String index, String queryJson) throws Exception {
        Instant start = Instant.now();
        SearchRequest request = SearchRequest.of(r -> r.index(index).query(q -> q.raw(queryJson)));
        SearchResponse<JsonData> response = client.search(request, JsonData.class);
        Instant end = Instant.now();
        System.out.println("[ES-APM] DSL executed in " + Duration.between(start, end).toMillis() + "ms, index: " + index);
        return response;
    }

    // =================== Index / Bulk ===================

    public <T> IndexResponse index(String index, String id, T document) throws Exception {
        IndexRequest<T> request = IndexRequest.of(r -> r.index(index).id(id).document(document).refresh(Refresh.True));
        return client.index(request);
    }

    public BulkResponse bulkIndex(String index, List<Map<String, Object>> documents) throws Exception {
        BulkRequest.Builder br = new BulkRequest.Builder();
        for (Map<String, Object> doc : documents) {
            br.operations(op -> op.index(idx -> idx.index(index).document(doc)));
        }
        return client.bulk(br.build());
    }

    // =================== 自动索引 / Template / Mapping ===================

    public void createIndexIfNotExists(String index, String mappingJson) throws Exception {
        boolean exists = client.indices().exists(e -> e.index(index)).value();
        if (!exists) {
            client.indices().create(c -> c.index(index).mappings(m -> m.withJson(mappingJson)));
        }
    }

    public void putIndexTemplate(String templateName, String pattern, String mappingJson) throws Exception {
        client.indices().putTemplate(t -> t
                .name(templateName)
                .indexPatterns(pattern)
                .template(temp -> temp.mappings(m -> m.withJson(mappingJson)))
        );
    }

    // =================== 私有方法 ===================

    private String parseSqlWithParams(String sql, Map<String, Object> params) {
        if (params == null || params.isEmpty()) return sql;
        String parsed = sql;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = ":" + entry.getKey();
            Object value = entry.getValue();
            String valueStr = value instanceof String ? "'" + value + "'" : value.toString();
            parsed = parsed.replace(key, valueStr);
        }
        return parsed;
    }

    private void addRowsToResult(List<List<Object>> rows, List<Column> columns, List<Map<String, Object>> results) {
        for (List<Object> row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                map.put(columns.get(i).name(), row.get(i));
            }
            results.add(map);
        }
    }

    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;
        if (targetType.isAssignableFrom(value.getClass())) return value;
        if (targetType == String.class) return value.toString();
        if (targetType == Integer.class || targetType == int.class) return ((Number)value).intValue();
        if (targetType == Long.class || targetType == long.class) return ((Number)value).longValue();
        if (targetType == Double.class || targetType == double.class) return ((Number)value).doubleValue();
        if (targetType == Float.class || targetType == float.class) return ((Number)value).floatValue();
        if (targetType == Boolean.class || targetType == boolean.class) return Boolean.parseBoolean(value.toString());
        return value;
    }
}
