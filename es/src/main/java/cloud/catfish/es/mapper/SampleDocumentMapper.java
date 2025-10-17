package cloud.catfish.es.mapper;

import cloud.catfish.es.annotation.*;
import cloud.catfish.es.model.SampleDocument;
import cloud.catfish.es.template.EsSqlTemplate;

import java.util.List;
import java.util.Map;

/**
 * SampleDocument Mapper接口
 * 展示如何使用@EsSQL和@EsDSL注解定义查询方法
 */
@EsMapper(defaultIndex = "sample_documents")
public interface SampleDocumentMapper extends BaseEsMapper<SampleDocument, String> {

    /**
     * 根据分类查询文档 - 使用DSL
     */
    @EsDSL(value = "{\"term\": {\"category\": \"#{category}\"}}", 
           index = "sample_documents")
    List<SampleDocument> findByCategory(@Param("category") String category);

    /**
     * 根据标题关键词搜索 - 使用SQL
     */
    @EsSQL(value = "SELECT * FROM sample_documents WHERE title LIKE '%#{keyword}%'")
    List<SampleDocument> findByTitleKeyword(@Param("keyword") String keyword);

    /**
     * 复合查询 - 分类和标题 - 使用DSL
     */
    @EsDSL(value = """
            {
              "bool": {
                "must": [
                  {"term": {"category": "#{category}"}},
                  {"match": {"title": "#{title}"}}
                ]
              }
            }
            """, 
           index = "sample_documents")
    List<SampleDocument> findByCategoryAndTitle(@Param("category") String category, 
                                               @Param("title") String title);

    /**
     * 分页查询指定分类的文档 - 使用SQL
     */
    @EsSQL(value = "SELECT * FROM sample_documents WHERE category = ? ORDER BY _score DESC LIMIT ?, ?", 
           pageable = true)
    EsSqlTemplate.PageResult<SampleDocument> findByCategoryWithPage(@Param("category") String category,
                                                                   @Param("from") int from,
                                                                   @Param("size") int size);

    /**
     * 聚合查询 - 按分类统计 - 使用DSL
     */
    @EsDSL(value = """
            {
              "size": 0,
              "aggs": {
                "category_count": {
                  "terms": {
                    "field": "category.keyword",
                    "size": 10
                  }
                }
              }
            }
            """, 
           index = "sample_documents", 
           type = EsDSL.QueryType.AGGREGATION)
    Map<String, Object> getCategoryStatistics();

    /**
     * 多字段搜索 - 使用DSL
     */
    @EsDSL(value = """
            {
              "multi_match": {
                "query": "#{keyword}",
                "fields": ["title^2", "content"],
                "type": "best_fields"
              }
            }
            """, 
           index = "sample_documents")
    List<SampleDocument> searchMultiFields(@Param("keyword") String keyword);

    /**
     * 范围查询 - 创建时间范围 - 使用SQL
     */
    @EsSQL("SELECT * FROM sample_documents WHERE created_time BETWEEN ? AND ?")
    List<SampleDocument> findByTimeRange(@Param("startTime") String startTime, 
                                        @Param("endTime") String endTime);

    /**
     * 高亮搜索 - 使用DSL
     */
    @EsDSL(value = """
            {
              "query": {
                "match": {
                  "content": "#{keyword}"
                }
              },
              "highlight": {
                "fields": {
                  "content": {}
                }
              }
            }
            """, 
           index = "sample_documents")
    List<Map<String, Object>> searchWithHighlight(@Param("keyword") String keyword);

    /**
     * 按分类计数 - 使用SQL
     */
    @EsSQL(value = "SELECT COUNT(*) FROM sample_documents WHERE category = ?")
    long countByCategory(@Param("category") String category);

    /**
     * 获取热门文档 - 使用DSL排序
     */
    @EsDSL(value = """
            {
              "query": {"match_all": {}},
              "sort": [
                {"_score": {"order": "desc"}},
                {"created_time": {"order": "desc"}}
              ]
            }
            """, 
           index = "sample_documents",
           defaultPageSize = 5)
    List<SampleDocument> getPopularDocuments();

    /**
     * 复杂聚合查询 - 分类和时间统计 - 使用DSL
     */
    @EsDSL(value = """
            {
              "size": 0,
              "aggs": {
                "categories": {
                  "terms": {
                    "field": "category.keyword"
                  },
                  "aggs": {
                    "monthly_stats": {
                      "date_histogram": {
                        "field": "created_time",
                        "calendar_interval": "month"
                      }
                    }
                  }
                }
              }
            }
            """, 
           index = "sample_documents", 
           type = EsDSL.QueryType.AGGREGATION)
    Map<String, Object> getCategoryTimeStatistics();
}