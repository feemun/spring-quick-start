package cloud.catfish.es.template;

import cn.hutool.db.sql.Query;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Elasticsearch DSL查询模板
 * 提供基于官方Java客户端的DSL查询功能
 */
@Slf4j
@Component
public class EsDslTemplate {

    private final ElasticsearchClient client;
    private final ObjectMapper mapper = new ObjectMapper();

    public EsDslTemplate(ElasticsearchClient client) {
        this.client = client;
    }

    /**
     * 执行DSL查询（使用Map构建查询）
     */
    public <T> List<T> query(String index, Map<String, Object> dslMap, Class<T> clazz) {
        try {
            String dslJson = mapper.writeValueAsString(dslMap);
            return queryByJson(index, dslJson, clazz);
        } catch (Exception e) {
            log.error("DSL查询失败 - index: {}, dsl: {}", index, dslMap, e);
            throw new RuntimeException("DSL查询执行失败", e);
        }
    }

    /**
     * 执行DSL查询（使用JSON字符串）
     */
    public <T> List<T> queryByJson(String index, String dslJson, Class<T> clazz) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index(index)
                    .withJson(new StringReader(dslJson))
            );

            SearchResponse<T> response = client.search(request, clazz);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("DSL JSON查询失败 - index: {}, json: {}", index, dslJson, e);
            throw new RuntimeException("DSL JSON查询执行失败", e);
        }
    }

    /**
     * 执行DSL查询（使用Query对象）
     */
    public <T> List<T> query(String index, Query query, Class<T> clazz) {
        return query(index, query, 0, 10, clazz);
    }

    /**
     * 执行DSL查询（带分页）
     */
    public <T> List<T> query(String index, Query query, int from, int size, Class<T> clazz) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index(index)
                    .query(query)
                    .from(from)
                    .size(size)
            );

            SearchResponse<T> response = client.search(request, clazz);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("DSL分页查询失败 - index: {}, from: {}, size: {}", index, from, size, e);
            throw new RuntimeException("DSL分页查询执行失败", e);
        }
    }

    /**
     * 执行DSL查询（带排序）
     */
    public <T> List<T> queryWithSort(String index, Query query, String sortField, SortOrder sortOrder, Class<T> clazz) {
        try {
            SearchRequest request = SearchRequest.of(s -> s
                    .index(index)
                    .query(query)
                    .sort(sort -> sort.field(f -> f.field(sortField).order(sortOrder)))
            );

            SearchResponse<T> response = client.search(request, clazz);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("DSL排序查询失败 - index: {}, sortField: {}, sortOrder: {}", index, sortField, sortOrder, e);
            throw new RuntimeException("DSL排序查询执行失败", e);
        }
    }

    /**
     * 执行聚合查询
     */
    public Map<String, Object> aggregate(String index, Query query, Map<String, Aggregation> aggregations) {
        try {
            SearchRequest.Builder requestBuilder = new SearchRequest.Builder()
                    .index(index)
                    .size(0); // 不返回文档，只返回聚合结果

            if (query != null) {
                requestBuilder.query(query);
            }

            aggregations.forEach(requestBuilder::aggregations);

            SearchResponse<Void> response = client.search(requestBuilder.build(), Void.class);
            return response.aggregations().entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue()._get()
                    ));
        } catch (Exception e) {
            log.error("聚合查询失败 - index: {}, aggregations: {}", index, aggregations.keySet(), e);
            throw new RuntimeException("聚合查询执行失败", e);
        }
    }

    /**
     * 获取查询总数
     */
    public long count(String index, Query query) {
        try {
            CountRequest request = CountRequest.of(c -> c
                    .index(index)
                    .query(query)
            );

            CountResponse response = client.count(request);
            return response.count();
        } catch (Exception e) {
            log.error("计数查询失败 - index: {}", index, e);
            throw new RuntimeException("计数查询执行失败", e);
        }
    }

    /**
     * 批量索引文档
     */
    public <T> BulkResponse bulkIndex(String index, List<T> documents) {
        try {
            BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();

            for (T doc : documents) {
                bulkBuilder.operations(op -> op
                        .index(idx -> idx
                                .index(index)
                                .document(doc)
                        )
                );
            }

            return client.bulk(bulkBuilder.build());
        } catch (Exception e) {
            log.error("批量索引失败 - index: {}, documentCount: {}", index, documents.size(), e);
            throw new RuntimeException("批量索引执行失败", e);
        }
    }

    /**
     * 批量删除文档
     */
    public BulkResponse bulkDelete(String index, List<String> ids) {
        try {
            BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();

            for (String id : ids) {
                bulkBuilder.operations(op -> op
                        .delete(del -> del
                                .index(index)
                                .id(id)
                        )
                );
            }

            return client.bulk(bulkBuilder.build());
        } catch (Exception e) {
            log.error("批量删除失败 - index: {}, idCount: {}", index, ids.size(), e);
            throw new RuntimeException("批量删除执行失败", e);
        }
    }

    /**
     * 构建term查询
     */
    public static Query termQuery(String field, String value) {
        return Query.of(q -> q
                .term(t -> t
                        .field(field)
                        .value(FieldValue.of(value))
                )
        );
    }

    /**
     * 构建match查询
     */
    public static Query matchQuery(String field, String value) {
        return Query.of(q -> q
                .match(m -> m
                        .field(field)
                        .query(value)
                )
        );
    }

    /**
     * 构建range查询
     */
    public static Query rangeQuery(String field, Object gte, Object lte) {
        return Query.of(q -> q
                .range(r -> {
                    var rangeQuery = r.field(field);
                    if (gte != null) {
                        rangeQuery.gte(JsonData.of(gte));
                    }
                    if (lte != null) {
                        rangeQuery.lte(JsonData.of(lte));
                    }
                    return rangeQuery;
                })
        );
    }

    /**
     * 构建bool查询
     */
    public static Query boolQuery() {
        return Query.of(q -> q.bool(b -> b));
    }
}