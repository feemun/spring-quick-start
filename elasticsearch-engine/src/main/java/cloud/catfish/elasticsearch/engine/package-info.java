/**
 * Elasticsearch搜索模块
 * 
 * 本模块提供基于Elasticsearch的高性能搜索和数据分析功能，专注于电商商品搜索场景�? * 采用spring-data-elasticsearch框架，实现了完整的搜索解决方案�? * 
 * <h2>核心功能</h2>
 * <ul>
 *   <li>全文搜索：支持多字段模糊匹配，智能权重排�?/li>
 *   <li>精确筛选：分类、品牌、价格区间等条件筛�?/li>
 *   <li>聚合统计：分类统计、品牌统计、价格分布分�?/li>
 *   <li>智能推荐：相似商品推荐、搜索建�?/li>
 *   <li>高频查询：针对常用查询场景的性能优化</li>
 * </ul>
 * 
 * <h2>技术特�?/h2>
 * <ul>
 *   <li>基于Spring Data Elasticsearch 5.x</li>
 *   <li>支持JDK 21文本块语法优化查询DSL</li>
 *   <li>无异常处理设计，依赖全局异常处理�?/li>
 *   <li>支持IK中文分词�?/li>
 *   <li>提供完整的RESTful API接口</li>
 * </ul>
 * 
 * <h2>主要组件</h2>
 * <ul>
 *   <li>{@link cloud.catfish.elasticsearch.engine.entity} - 实体类定�?/li>
 *   <li>{@link cloud.catfish.elasticsearch.engine.repository} - 数据访问�?/li>
 *   <li>{@link cloud.catfish.elasticsearch.engine.service} - 业务服务�?/li>
 *   <li>{@link cloud.catfish.elasticsearch.engine.controller} - REST控制�?/li>
 *   <li>{@link cloud.catfish.elasticsearch.engine.dto} - 数据传输对象</li>
 *   <li>{@link cloud.catfish.elasticsearch.engine.vo} - 视图对象</li>
 * </ul>
 * 
 * <h2>使用示例</h2>
 * <pre>{@code
 * // 全文搜索
 * GET /api/es/query/search?keyword=手机&page=0&size=10
 * 
 * // 分类筛�? * GET /api/es/query/category/电子产品
 * 
 * // 价格区间查询
 * GET /api/es/query/price?min=100&max=1000
 * 
 * // 组合条件查询
 * GET /api/es/query/complex?keyword=手机&category=电子产品&min=1000&max=5000
 * }</pre>
 * 
 * @author catfish
 * @version 1.0.0
 * @since 1.0.0
 */
package cloud.catfish.elasticsearch.engine;
