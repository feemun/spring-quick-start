package cloud.catfish.es.service.impl;

import cloud.catfish.es.dto.SearchRequest;
import cloud.catfish.es.dto.SearchResponse;
import cloud.catfish.es.service.ElasticsearchService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Elasticsearch通用查询服务实现类
 * 
 * @author catfish
 * @since 1.0.0
 */
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchServiceImpl.class);

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public SearchResponse<Map<String, Object>> search(SearchRequest searchRequest) {
        try {
            // 构建查询
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            
            // 构建查询条件
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            
            // 关键词搜索
            if (StrUtil.isNotBlank(searchRequest.getKeyword())) {
                if (CollUtil.isNotEmpty(searchRequest.getFields())) {
                    // 多字段搜索
                    MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery(
                            searchRequest.getKeyword(),
                            searchRequest.getFields().toArray(new String[0])
                    );
                    boolQuery.must(multiMatchQuery);
                } else {
                    // 全文搜索
                    boolQuery.must(QueryBuilders.queryStringQuery(searchRequest.getKeyword()));
                }
            }
            
            // 精确匹配
            if (CollUtil.isNotEmpty(searchRequest.getExactMatch())) {
                searchRequest.getExactMatch().forEach((field, value) -> {
                    boolQuery.filter(QueryBuilders.termQuery(field, value));
                });
            }
            
            // 范围查询
            if (CollUtil.isNotEmpty(searchRequest.getRangeQuery())) {
                searchRequest.getRangeQuery().forEach((field, range) -> {
                    RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(field);
                    if (range.getGte() != null) rangeQuery.gte(range.getGte());
                    if (range.getLte() != null) rangeQuery.lte(range.getLte());
                    if (range.getGt() != null) rangeQuery.gt(range.getGt());
                    if (range.getLt() != null) rangeQuery.lt(range.getLt());
                    boolQuery.filter(rangeQuery);
                });
            }
            
            queryBuilder.withQuery(boolQuery);
            
            // 分页
            int from = (searchRequest.getPageNum() - 1) * searchRequest.getPageSize();
            queryBuilder.withPageable(PageRequest.of(from / searchRequest.getPageSize(), searchRequest.getPageSize()));
            
            // 排序
            if (StrUtil.isNotBlank(searchRequest.getSortField())) {
                SortOrder sortOrder = "asc".equalsIgnoreCase(searchRequest.getSortOrder()) 
                        ? SortOrder.ASC : SortOrder.DESC;
                queryBuilder.withSort(SortBuilders.fieldSort(searchRequest.getSortField()).order(sortOrder));
            }
            
            // 高亮
            if (CollUtil.isNotEmpty(searchRequest.getHighlightFields())) {
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                searchRequest.getHighlightFields().forEach(field -> {
                    highlightBuilder.field(new HighlightBuilder.Field(field)
                            .preTags("<em>")
                            .postTags("</em>"));
                });
                queryBuilder.withHighlightBuilder(highlightBuilder);
            }
            
            // 执行查询
            SearchHits<Map> searchHits = elasticsearchTemplate.search(
                    queryBuilder.build(),
                    Map.class,
                    IndexCoordinates.of(searchRequest.getIndex())
            );
            
            // 构建响应
            List<SearchResponse.SearchHit<Map<String, Object>>> hits = searchHits.getSearchHits().stream()
                    .map(hit -> {
                        SearchResponse.SearchHit<Map<String, Object>> searchHit = new SearchResponse.SearchHit<>();
                        searchHit.setId(hit.getId());
                        searchHit.setSource(hit.getContent());
                        searchHit.setScore(hit.getScore());
                        
                        // 处理高亮
                        if (CollUtil.isNotEmpty(hit.getHighlightFields())) {
                            Map<String, List<String>> highlight = new HashMap<>();
                            hit.getHighlightFields().forEach((field, fragments) -> {
                                highlight.put(field, Arrays.asList(fragments));
                            });
                            searchHit.setHighlight(highlight);
                        }
                        
                        return searchHit;
                    })
                    .collect(Collectors.toList());
            
            SearchResponse<Map<String, Object>> response = new SearchResponse<>(
                    hits,
                    searchHits.getTotalHits(),
                    searchRequest.getPageNum(),
                    searchRequest.getPageSize()
            );
            response.setTook(searchHits.getTotalHitsRelation().name().equals("EQUAL_TO") ? 0L : 0L);
            
            return response;
            
        } catch (Exception e) {
            logger.error("搜索失败: {}", e.getMessage(), e);
            throw new RuntimeException("搜索失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getById(String index, String id) {
        try {
            return elasticsearchTemplate.get(id, Map.class, IndexCoordinates.of(index));
        } catch (Exception e) {
            logger.error("根据ID获取文档失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean save(String index, String id, Map<String, Object> document) {
        try {
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(id)
                    .withObject(document)
                    .build();
            
            String result = elasticsearchTemplate.index(indexQuery, IndexCoordinates.of(index));
            return StrUtil.isNotBlank(result);
        } catch (Exception e) {
            logger.error("保存文档失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean batchSave(String index, Map<String, Map<String, Object>> documents) {
        try {
            List<IndexQuery> queries = documents.entrySet().stream()
                    .map(entry -> new IndexQueryBuilder()
                            .withId(entry.getKey())
                            .withObject(entry.getValue())
                            .build())
                    .collect(Collectors.toList());
            
            List<String> results = elasticsearchTemplate.bulkIndex(queries, IndexCoordinates.of(index));
            return CollUtil.isNotEmpty(results);
        } catch (Exception e) {
            logger.error("批量保存文档失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteById(String index, String id) {
        try {
            String result = elasticsearchTemplate.delete(id, IndexCoordinates.of(index));
            return StrUtil.isNotBlank(result);
        } catch (Exception e) {
            logger.error("删除文档失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean batchDelete(String index, List<String> ids) {
        try {
            List<String> results = elasticsearchTemplate.bulkDelete(ids, Map.class, IndexCoordinates.of(index));
            return CollUtil.isNotEmpty(results);
        } catch (Exception e) {
            logger.error("批量删除文档失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean indexExists(String index) {
        try {
            return elasticsearchTemplate.indexOps(IndexCoordinates.of(index)).exists();
        } catch (Exception e) {
            logger.error("检查索引是否存在失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean createIndex(String index, Map<String, Object> mapping) {
        try {
            IndexCoordinates indexCoordinates = IndexCoordinates.of(index);
            if (!elasticsearchTemplate.indexOps(indexCoordinates).exists()) {
                boolean created = elasticsearchTemplate.indexOps(indexCoordinates).create();
                if (created && CollUtil.isNotEmpty(mapping)) {
                    Document mappingDoc = Document.from(mapping);
                    return elasticsearchTemplate.indexOps(indexCoordinates).putMapping(mappingDoc);
                }
                return created;
            }
            return true;
        } catch (Exception e) {
            logger.error("创建索引失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteIndex(String index) {
        try {
            return elasticsearchTemplate.indexOps(IndexCoordinates.of(index)).delete();
        } catch (Exception e) {
            logger.error("删除索引失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Map<String, Object> getMapping(String index) {
        try {
            Document mapping = elasticsearchTemplate.indexOps(IndexCoordinates.of(index)).getMapping();
            return mapping != null ? mapping : new HashMap<>();
        } catch (Exception e) {
            logger.error("获取索引映射失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> aggregate(String index, Map<String, Object> aggregationQuery) {
        try {
            // 这里可以根据需要实现聚合查询
            // 由于聚合查询比较复杂，这里提供一个基础框架
            logger.info("聚合查询功能待实现");
            return new HashMap<>();
        } catch (Exception e) {
            logger.error("聚合查询失败: {}", e.getMessage(), e);
            return new HashMap<>();
        }
    }
}