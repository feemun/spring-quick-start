/**
 * 业务服务层包
 * 
 * 本包包含Elasticsearch搜索模块的所有业务服务接口和实现类�? * 服务层封装了复杂的业务逻辑，提供高层次的搜索和数据分析功能�? * 
 * <h2>主要服务接口</h2>
 * <ul>
 *   <li>{@link cloud.catfish.elasticsearch.engine.service.HighFrequencyQueryService} - 高频查询服务接口</li>
 * </ul>
 * 
 * <h2>服务实现</h2>
 * <ul>
 *   <li>{@link cloud.catfish.elasticsearch.engine.service.impl.HighFrequencyQueryServiceImpl} - 高频查询服务实现</li>
 * </ul>
 * 
 * <h2>核心功能</h2>
 * <ul>
 *   <li><strong>全文搜索</strong>：多字段智能匹配，支持权重排�?/li>
 *   <li><strong>条件筛�?/strong>：分类、品牌、价格等精确筛�?/li>
 *   <li><strong>聚合统计</strong>：分类统计、品牌分析、价格分�?/li>
 *   <li><strong>智能推荐</strong>：基于相似度算法的商品推�?/li>
 *   <li><strong>搜索建议</strong>：前缀匹配的自动补全功�?/li>
 *   <li><strong>批量操作</strong>：支持批量查询和数据处理</li>
 * </ul>
 * 
 * <h2>设计原则</h2>
 * <ul>
 *   <li><strong>单一职责</strong>：每个服务专注于特定的业务领�?/li>
 *   <li><strong>接口分离</strong>：通过接口定义清晰的服务契�?/li>
 *   <li><strong>依赖注入</strong>：使用Spring的依赖注入管理组�?/li>
 *   <li><strong>无异常处�?/strong>：依赖全局异常处理器统一处理异常</li>
 *   <li><strong>性能优化</strong>：针对高频查询场景进行性能调优</li>
 * </ul>
 * 
 * <h2>查询优化策略</h2>
 * <ul>
 *   <li>使用合适的查询类型（term、match、bool等）</li>
 *   <li>合理设置查询权重和boost�?/li>
 *   <li>利用Elasticsearch的缓存机�?/li>
 *   <li>优化聚合查询的性能</li>
 *   <li>使用原生查询构建器提升灵活�?/li>
 * </ul>
 * 
 * <h2>使用示例</h2>
 * <pre>{@code
 * @Service
 * public class HighFrequencyQueryServiceImpl implements HighFrequencyQueryService {
 *     
 *     @Autowired
 *     private ProductRepository productRepository;
 *     
 *     @Autowired
 *     private ElasticsearchOperations elasticsearchOperations;
 *     
 *     @Override
 *     public Page<Product> fullTextSearch(String keyword, Pageable pageable) {
 *         return productRepository.findByNameOrDescription(keyword, pageable);
 *     }
 * }
 * }</pre>
 * 
 * @author catfish
 * @version 1.0.0
 * @since 1.0.0
 */
package cloud.catfish.elasticsearch.engine.service;
