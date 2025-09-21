/**
 * 实体类包
 * 
 * 本包包含Elasticsearch搜索模块的所有实体类定义，这些实体类直接映射到Elasticsearch索引�? * 所有实体类都使用Spring Data Elasticsearch注解进行配置，支持完整的字段映射和索引设置�? * 
 * <h2>主要实体�?/h2>
 * <ul>
 *   <li>{@link cloud.catfish.elasticsearch.engine.entity.Product} - 商品实体类，映射到products索引</li>
 * </ul>
 * 
 * <h2>设计特点</h2>
 * <ul>
 *   <li>使用 {@code @Document} 注解定义索引名称和设�?/li>
 *   <li>使用 {@code @Field} 注解配置字段类型和分析器</li>
 *   <li>支持IK中文分词器进行全文搜�?/li>
 *   <li>合理设置字段类型以优化查询性能</li>
 *   <li>支持多种数据类型：文本、关键词、数值、日期等</li>
 * </ul>
 * 
 * <h2>索引配置</h2>
 * <ul>
 *   <li>分片数：3个主分片，提供良好的并发性能</li>
 *   <li>副本数：1个副本，保证数据可用�?/li>
 *   <li>分析器：使用ik_max_word进行索引，ik_smart进行搜索</li>
 *   <li>字段映射：根据查询需求优化字段类型配�?/li>
 * </ul>
 * 
 * <h2>字段类型说明</h2>
 * <ul>
 *   <li>{@code Text} - 支持全文搜索的文本字�?/li>
 *   <li>{@code Keyword} - 精确匹配的关键词字段</li>
 *   <li>{@code Integer/Double} - 数值字段，支持范围查询</li>
 *   <li>{@code Date} - 日期字段，支持时间范围查�?/li>
 * </ul>
 * 
 * <h2>使用示例</h2>
 * <pre>{@code
 * @Document(indexName = "products")
 * @Setting(numberOfShards = 3, numberOfReplicas = 1)
 * public class Product {
 *     @Id
 *     private String id;
 *     
 *     @Field(type = FieldType.Text, analyzer = "ik_max_word")
 *     private String name;
 *     
 *     @Field(type = FieldType.Double)
 *     private BigDecimal price;
 * }
 * }</pre>
 * 
 * @author catfish
 * @version 1.0.0
 * @since 1.0.0
 */
package cloud.catfish.elasticsearch.engine.entity;
