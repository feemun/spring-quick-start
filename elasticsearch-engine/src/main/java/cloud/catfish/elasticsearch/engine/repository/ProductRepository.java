package cloud.catfish.elasticsearch.engine.repository;

import cloud.catfish.elasticsearch.engine.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品Elasticsearch存储库接�? * 提供基于spring-data-elasticsearch的查询方�? * 
 * @author catfish
 * @since 1.0.0
 */
@Repository
public interface ProductRepository extends ElasticsearchRepository<Product, String> {

    /**
     * 根据商品名称或描述进行全文搜�?     * 使用多字段匹配查�?     */
    @Query("""
            {
              "bool": {
                "should": [
                  {
                    "match": {
                      "name": {
                        "query": "?0",
                        "boost": 2.0
                      }
                    }
                  },
                  {
                    "match": {
                      "description": {
                        "query": "?0",
                        "boost": 1.0
                      }
                    }
                  },
                  {
                    "match": {
                      "brand": {
                        "query": "?0",
                        "boost": 1.5
                      }
                    }
                  },
                  {
                    "match": {
                      "category": {
                        "query": "?0",
                        "boost": 1.2
                      }
                    }
                  }
                ],
                "minimum_should_match": 1
              }
            }
            """)
    Page<Product> findByNameOrDescription(String keyword, Pageable pageable);

    /**
     * 根据分类查询商品
     * 使用精确匹配
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * 根据品牌查询商品
     * 使用精确匹配
     */
    Page<Product> findByBrand(String brand, Pageable pageable);

    /**
     * 根据价格区间查询商品
     */
    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * 查询库存大于指定数量的商�?     */
    Page<Product> findByStockGreaterThan(Integer stock, Pageable pageable);

    /**
     * 根据销量降序查询商�?     */
    List<Product> findBySalesGreaterThanOrderBySalesDesc(Integer sales, Pageable pageable);

    /**
     * 根据创建时间降序查询商品
     */
    List<Product> findByOrderByCreateTimeDesc(Pageable pageable);

    /**
     * 多字段搜索查�?     * 支持在多个字段中进行模糊匹配
     */
    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": ["name^3", "description^1", "brand^2", "category^1.5"],
                "type": "best_fields",
                "fuzziness": "AUTO"
              }
            }
            """)
    Page<Product> multiFieldSearch(String keyword, Pageable pageable);

    /**
     * 根据分类和价格区间查�?     */
    Page<Product> findByCategoryAndPriceBetween(String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * 根据品牌和库存状态查�?     */
    Page<Product> findByBrandAndStockGreaterThan(String brand, Integer stock, Pageable pageable);

    /**
     * 查询指定分类下的热门商品
     */
    List<Product> findByCategoryOrderBySalesDesc(String category, Pageable pageable);

    /**
     * 查询指定价格范围内的商品数量
     */
    long countByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * 查询指定分类下的商品数量
     */
    long countByCategory(String category);

    /**
     * 查询指定品牌下的商品数量
     */
    long countByBrand(String brand);

    /**
     * 查询有库存的商品数量
     */
    long countByStockGreaterThan(Integer stock);

    /**
     * 根据名称前缀查询商品（用于搜索建议）
     */
    List<Product> findByNameStartingWithIgnoreCase(String prefix, Pageable pageable);

    /**
     * 根据品牌前缀查询商品（用于搜索建议）
     */
    List<Product> findByBrandStartingWithIgnoreCase(String prefix, Pageable pageable);

    /**
     * 根据分类前缀查询商品（用于搜索建议）
     */
    List<Product> findByCategoryStartingWithIgnoreCase(String prefix, Pageable pageable);
}
