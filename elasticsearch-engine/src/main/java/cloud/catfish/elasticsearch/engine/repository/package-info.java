/**
 * 数据访问层包
 * 
 * 本包包含Elasticsearch搜索模块的所有数据访问接口，基于Spring Data Elasticsearch框架�? * 提供了丰富的查询方法，包括方法命名查询、自定义@Query查询等多种方式�? * 
 * <h2>主要Repository接口</h2>
 * <ul>
 *   <li>{@link cloud.catfish.elasticsearch.engine.repository.ProductRepository} - 商品数据访问接口</li>
 * </ul>
 * 
 * <h2>查询方式</h2>
 * <ul>
 *   <li><strong>方法命名查询</strong>：基于方法名自动生成查询逻辑</li>
 *   <li><strong>@Query注解</strong>：使用Elasticsearch DSL进行复杂查询</li>
 *   <li><strong>文本块语�?/strong>：利用JDK 21特性优化查询DSL可读�?/li>
 *   <li><strong>原生查询</strong>：支持完整的Elasticsearch查询功能</li>
 * </ul>
 * 
 * <h2>支持的查询类�?/h2>
 * <ul>
 *   <li>精确匹配查询：{@code findByCategory}</li>
 *   <li>范围查询：{@code findByPriceBetween}</li>
 *   <li>模糊查询：{@code findByNameContaining}</li>
 *   <li>排序查询：{@code findByOrderBySalesDesc}</li>
 *   <li>分页查询：所有查询方法都支持{@code Pageable}参数</li>
 *   <li>计数查询：{@code countByCategory}</li>
 *   <li>存在性查询：{@code existsByName}</li>
 * </ul>
 * 
 * <h2>自定义查询示�?/h2>
 * <pre>{@code
 * @Query("""
 *     {
 *       "bool": {
 *         "should": [
 *           {
 *             "match": {
 *               "name": {
 *                 "query": "?0",
 *                 "boost": 2.0
 *               }
 *             }
 *           }
 *         ]
 *       }
 *     }
 *     """)
 * Page<Product> findByNameOrDescription(String keyword, Pageable pageable);
 * }</pre>
 * 
 * <h2>性能优化</h2>
 * <ul>
 *   <li>合理使用索引和字段类�?/li>
 *   <li>避免深度分页，推荐使用scroll或search_after</li>
 *   <li>使用合适的分析器和搜索�?/li>
 *   <li>利用缓存机制提升查询性能</li>
 * </ul>
 * 
 * @author catfish
 * @version 1.0.0
 * @since 1.0.0
 */
package cloud.catfish.elasticsearch.engine.repository;
