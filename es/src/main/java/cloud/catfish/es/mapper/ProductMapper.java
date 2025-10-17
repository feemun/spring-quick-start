package cloud.catfish.es.mapper;

import cloud.catfish.es.annotation.EsMapper;
import cloud.catfish.es.annotation.EsSQL;
import cloud.catfish.es.annotation.EsDSL;
import cloud.catfish.es.annotation.Param;
import cloud.catfish.es.entity.Product;
import cloud.catfish.es.template.EsSqlTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商品Mapper接口
 * 专门用于操作商品索引(products)
 */
@EsMapper(value = "productMapper", defaultIndex = "products")
public interface ProductMapper extends BaseEsMapper<Product, String> {

    /**
     * 根据商品名称查询
     */
    @EsSQL("SELECT * FROM products WHERE name = ?")
    List<Product> findByName(@Param("name") String name);

    /**
     * 根据分类查询商品
     */
    @EsDSL("{\"term\": {\"category.keyword\": \"#{category}\"}}")
    List<Product> findByCategory(@Param("category") String category);

    /**
     * 根据品牌查询商品
     */
    @EsSQL("SELECT * FROM products WHERE brand = ? AND status = 'ACTIVE' ORDER BY salesCount DESC")
    List<Product> findByBrand(@Param("brand") String brand);

    /**
     * 根据价格范围查询商品
     */
    @EsDSL("{\"range\": {\"price\": {\"gte\": #{minPrice}, \"lte\": #{maxPrice}}}}")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                  @Param("maxPrice") BigDecimal maxPrice);

    /**
     * 分页查询商品（按销量排序）
     */
    @EsSQL(value = "SELECT * FROM products WHERE status = 'ACTIVE' ORDER BY salesCount DESC LIMIT #{from}, #{size}", 
           pageable = true)
    EsSqlTemplate.PageResult<Product> findActiveProductsWithPage(@Param("from") int from, 
                                                               @Param("size") int size);

    /**
     * 搜索商品（名称、描述、品牌）
     */
    @EsDSL(value = """
        {
          "bool": {
            "should": [
              {"match": {"name": {"query": "#{keyword}", "boost": 3}}},
              {"match": {"description": {"query": "#{keyword}", "boost": 1}}},
              {"match": {"brand": {"query": "#{keyword}", "boost": 2}}}
            ]
          }
        }
        """)
    List<Product> searchProducts(@Param("keyword") String keyword);

    /**
     * 获取热门商品（高评分且高销量）
     */
    @EsDSL(value = """
        {
          "bool": {
            "must": [
              {"range": {"rating": {"gte": 4.0}}},
              {"range": {"salesCount": {"gte": 100}}},
              {"term": {"status.keyword": "ACTIVE"}}
            ]
          }
        }
        """,
        sort = "salesCount:desc,rating:desc")
    List<Product> getPopularProducts();

    /**
     * 统计各分类商品数量和平均价格
     */
    @EsDSL(value = "{\"match_all\": {}}", 
           type = EsDSL.QueryType.AGGREGATION,
           aggregations = """
           {
             "category_stats": {
               "terms": {
                 "field": "category.keyword",
                 "size": 20
               },
               "aggs": {
                 "avg_price": {
                   "avg": {
                     "field": "price"
                   }
                 },
                 "total_sales": {
                   "sum": {
                     "field": "salesCount"
                   }
                 }
               }
             }
           }
           """)
    Map<String, Object> getCategoryStats();

    /**
     * 根据库存状态查询商品
     */
    @EsSQL("SELECT * FROM products WHERE stock > ? AND status = 'ACTIVE' ORDER BY stock DESC")
    List<Product> findInStockProducts(@Param("minStock") Integer minStock);

    /**
     * 获取低库存商品
     */
    @EsDSL(value = """
        {
          "bool": {
            "must": [
              {"range": {"stock": {"lte": #{threshold}}}},
              {"term": {"status.keyword": "ACTIVE"}}
            ]
          }
        }
        """)
    List<Product> getLowStockProducts(@Param("threshold") Integer threshold);

    /**
     * 根据评分范围查询商品
     */
    @EsSQL("SELECT * FROM products WHERE rating BETWEEN ? AND ? ORDER BY rating DESC, salesCount DESC")
    List<Product> findByRatingRange(@Param("minRating") Double minRating, 
                                   @Param("maxRating") Double maxRating);

    /**
     * 高亮搜索商品名称和描述
     */
    @EsDSL(value = """
        {
          "multi_match": {
            "query": "#{keyword}",
            "fields": ["name^3", "description^1", "brand^2"]
          }
        }
        """,
        highlight = true,
        highlightFields = "name,description")
    List<Product> searchProductsWithHighlight(@Param("keyword") String keyword);

    /**
     * 获取新品（最近创建的商品）
     */
    @EsSQL("SELECT * FROM products WHERE status = 'ACTIVE' ORDER BY createTime DESC LIMIT ?")
    List<Product> getNewProducts(@Param("limit") Integer limit);

    /**
     * 复合条件查询商品
     */
    @EsDSL(value = """
        {
          "bool": {
            "must": [
              {"term": {"category.keyword": "#{category}"}},
              {"range": {"price": {"gte": #{minPrice}, "lte": #{maxPrice}}}},
              {"range": {"rating": {"gte": #{minRating}}}},
              {"term": {"status.keyword": "ACTIVE"}}
            ]
          }
        }
        """,
        sort = "#{sortField}:#{sortOrder}")
    List<Product> findByComplexConditions(@Param("category") String category,
                                         @Param("minPrice") BigDecimal minPrice,
                                         @Param("maxPrice") BigDecimal maxPrice,
                                         @Param("minRating") Double minRating,
                                         @Param("sortField") String sortField,
                                         @Param("sortOrder") String sortOrder);

    /**
     * 统计商品总数（按状态）
     */
    @EsSQL("SELECT COUNT(*) FROM products WHERE status = ?")
    long countByStatus(@Param("status") String status);
}