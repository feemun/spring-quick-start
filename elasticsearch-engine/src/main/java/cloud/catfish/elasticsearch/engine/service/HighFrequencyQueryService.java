package cloud.catfish.elasticsearch.engine.service;

import cloud.catfish.elasticsearch.engine.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 高频查询服务接口
 * 提供基于单个索引的常用查询操�? * 
 * @author catfish
 * @since 1.0.0
 */
public interface HighFrequencyQueryService {

    /**
     * 全文搜索 - 最高频的查询操�?     * 在商品名称、描述、分类、品牌中进行全文搜索
     */
    Page<Product> fullTextSearch(String keyword, Pageable pageable);

    /**
     * 分类筛�?- 高频查询
     * 根据商品分类进行精确匹配
     */
    Page<Product> findByCategory(String category, Pageable pageable);

    /**
     * 价格区间查询 - 高频查询
     * 根据价格范围筛选商�?     */
    Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * 品牌筛�?- 高频查询
     * 根据品牌进行精确匹配
     */
    Page<Product> findByBrand(String brand, Pageable pageable);

    /**
     * 库存状态查�?- 高频查询
     * 查询有库存的商品
     */
    Page<Product> findInStock(Pageable pageable);

    /**
     * 热门商品查询 - 高频查询
     * 根据销量排序获取热门商�?     */
    List<Product> findHotProducts(int limit);

    /**
     * 新品查询 - 高频查询
     * 根据创建时间获取最新商�?     */
    List<Product> findLatestProducts(int limit);

    /**
     * 组合条件查询 - 高频查询
     * 支持关键词、分类、价格区间的组合查询
     */
    Page<Product> complexSearch(String keyword, String category, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * 相似商品推荐 - 高频查询
     * 基于商品ID推荐相似商品
     */
    List<Product> findSimilarProducts(String productId, int limit);

    /**
     * 分类统计 - 高频聚合查询
     * 统计各分类下的商品数�?     */
    Map<String, Long> getCategoryStatistics();

    /**
     * 品牌统计 - 高频聚合查询
     * 统计各品牌下的商品数�?     */
    Map<String, Long> getBrandStatistics();

    /**
     * 价格区间统计 - 高频聚合查询
     * 统计不同价格区间的商品分�?     */
    Map<String, Long> getPriceRangeStatistics();

    /**
     * 搜索建议 - 高频查询
     * 基于输入关键词提供搜索建�?     */
    List<String> getSearchSuggestions(String prefix, int limit);

    /**
     * 商品详情查询 - 高频查询
     * 根据商品ID获取详细信息
     */
    Product findById(String id);

    /**
     * 批量查询 - 高频查询
     * 根据商品ID列表批量获取商品信息
     */
    List<Product> findByIds(List<String> ids);
}
