/**
 * 数据传输对象�? * 
 * 本包包含Elasticsearch搜索模块的所有数据传输对象（DTO）�? * DTO用于在不同层之间传输数据，特别是在控制器层和服务层之间的数据交换�? * 
 * <h2>DTO设计原则</h2>
 * <ul>
 *   <li><strong>数据封装</strong>：封装请求参数和响应数据</li>
 *   <li><strong>类型安全</strong>：提供强类型的数据结�?/li>
 *   <li><strong>验证支持</strong>：集成Bean Validation进行参数校验</li>
 *   <li><strong>序列化友�?/strong>：支持JSON序列化和反序列化</li>
 *   <li><strong>文档清晰</strong>：提供清晰的字段说明和示�?/li>
 * </ul>
 * 
 * <h2>主要DTO类型</h2>
 * <ul>
 *   <li><strong>请求DTO</strong>：封装客户端请求参数</li>
 *   <li><strong>响应DTO</strong>：封装服务端响应数据</li>
 *   <li><strong>查询DTO</strong>：封装复杂查询条�?/li>
 *   <li><strong>统计DTO</strong>：封装聚合统计结�?/li>
 * </ul>
 * 
 * <h2>常用注解</h2>
 * <ul>
 *   <li>{@code @NotNull} - 非空校验</li>
 *   <li>{@code @NotBlank} - 非空白字符串校验</li>
 *   <li>{@code @Min/@Max} - 数值范围校�?/li>
 *   <li>{@code @Size} - 字符串长度校�?/li>
 *   <li>{@code @Pattern} - 正则表达式校�?/li>
 *   <li>{@code @Valid} - 嵌套对象校验</li>
 * </ul>
 * 
 * <h2>请求DTO示例</h2>
 * <pre>{@code
 * public class ProductSearchRequest {
 *     @NotBlank(message = "搜索关键词不能为�?)
 *     @Size(max = 100, message = "搜索关键词长度不能超�?00个字�?)
 *     private String keyword;
 *     
 *     @Size(max = 50, message = "分类名称长度不能超过50个字�?)
 *     private String category;
 *     
 *     @Min(value = 0, message = "最低价格不能小�?")
 *     private BigDecimal minPrice;
 *     
 *     @Min(value = 0, message = "最高价格不能小�?")
 *     private BigDecimal maxPrice;
 *     
 *     @Min(value = 0, message = "页码不能小于0")
 *     private Integer page = 0;
 *     
 *     @Min(value = 1, message = "每页大小不能小于1")
 *     @Max(value = 100, message = "每页大小不能超过100")
 *     private Integer size = 20;
 * }
 * }</pre>
 * 
 * <h2>响应DTO示例</h2>
 * <pre>{@code
 * public class ProductSearchResponse {
 *     private List<ProductInfo> products;
 *     private Long totalElements;
 *     private Integer totalPages;
 *     private Integer currentPage;
 *     private Integer pageSize;
 *     private Map<String, Long> categoryStats;
 *     private Map<String, Long> brandStats;
 * }
 * }</pre>
 * 
 * <h2>最佳实�?/h2>
 * <ul>
 *   <li>使用Builder模式简化对象构�?/li>
 *   <li>提供合理的默认�?/li>
 *   <li>添加详细的字段注�?/li>
 *   <li>使用合适的数据类型</li>
 *   <li>避免暴露内部实现细节</li>
 * </ul>
 * 
 * @author catfish
 * @version 1.0.0
 * @since 1.0.0
 */
package cloud.catfish.elasticsearch.engine.dto;
