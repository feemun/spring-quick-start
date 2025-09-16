/**
 * 视图对象�? * 
 * 本包包含Elasticsearch搜索模块的所有视图对象（VO - View Object）�? * VO专门用于前端展示，包含格式化后的数据和展示逻辑，与DTO相比更注重用户界面的需求�? * 
 * <h2>VO与DTO的区�?/h2>
 * <ul>
 *   <li><strong>DTO</strong>：数据传输对象，关注数据的传输和校验</li>
 *   <li><strong>VO</strong>：视图对象，关注数据的展示和格式�?/li>
 *   <li><strong>用�?/strong>：VO专门为前端UI设计，包含展示相关的计算字段</li>
 *   <li><strong>转换</strong>：通常由DTO或Entity转换而来</li>
 * </ul>
 * 
 * <h2>主要VO类型</h2>
 * <ul>
 *   <li><strong>列表VO</strong>：用于列表页面展示的简化数�?/li>
 *   <li><strong>详情VO</strong>：用于详情页面展示的完整数据</li>
 *   <li><strong>统计VO</strong>：用于图表和统计展示的聚合数�?/li>
 *   <li><strong>搜索结果VO</strong>：用于搜索结果页面的格式化数�?/li>
 * </ul>
 * 
 * <h2>设计特点</h2>
 * <ul>
 *   <li><strong>展示友好</strong>：包含格式化的字段，如价格显示、日期格式等</li>
 *   <li><strong>计算字段</strong>：包含基于原始数据计算的展示字段</li>
 *   <li><strong>国际化支�?/strong>：支持多语言展示</li>
 *   <li><strong>性能优化</strong>：只包含前端需要的字段，减少数据传�?/li>
 *   <li><strong>版本兼容</strong>：保持API的向后兼容�?/li>
 * </ul>
 * 
 * <h2>商品列表VO示例</h2>
 * <pre>{@code
 * public class ProductListVO {
 *     private String id;
 *     private String name;
 *     private String displayPrice;        // 格式化的价格显示
 *     private String category;
 *     private String brand;
 *     private String imageUrl;
 *     private Integer sales;
 *     private Double rating;
 *     private String ratingDisplay;       // 格式化的评分显示
 *     private Boolean inStock;
 *     private String stockStatus;         // 库存状态文�? *     private String createTimeDisplay;   // 格式化的创建时间
 *     
 *     // 计算字段
 *     public String getDisplayPrice() {
 *         return "�? + NumberFormat.getInstance().format(price);
 *     }
 *     
 *     public String getStockStatus() {
 *         return inStock ? "现货" : "缺货";
 *     }
 * }
 * }</pre>
 * 
 * <h2>搜索结果VO示例</h2>
 * <pre>{@code
 * public class SearchResultVO {
 *     private List<ProductListVO> products;
 *     private PaginationVO pagination;
 *     private List<FilterOptionVO> categoryFilters;
 *     private List<FilterOptionVO> brandFilters;
 *     private PriceRangeVO priceRange;
 *     private SearchStatisticsVO statistics;
 *     
 *     // 搜索相关的展示信�? *     private String searchKeyword;
 *     private String searchSummary;       // "共找�?23个商�?
 *     private Long searchTime;            // 搜索耗时
 *     private List<String> suggestions;   // 搜索建议
 * }
 * }</pre>
 * 
 * <h2>格式化工�?/h2>
 * <ul>
 *   <li><strong>价格格式�?/strong>：添加货币符号和千分位分隔符</li>
 *   <li><strong>日期格式�?/strong>：转换为用户友好的日期显�?/li>
 *   <li><strong>数量格式�?/strong>：大数字的简化显示（�?.2万）</li>
 *   <li><strong>状态转�?/strong>：将枚举值转换为用户可读的文�?/li>
 * </ul>
 * 
 * <h2>最佳实�?/h2>
 * <ul>
 *   <li>保持VO的轻量级，避免包含复杂的业务逻辑</li>
 *   <li>使用工厂方法或Builder模式创建VO</li>
 *   <li>提供静态方法进行Entity到VO的转�?/li>
 *   <li>考虑缓存频繁使用的格式化结果</li>
 *   <li>为前端提供清晰的字段说明文档</li>
 * </ul>
 * 
 * @author catfish
 * @version 1.0.0
 * @since 1.0.0
 */
package cloud.catfish.elasticsearch.engine.vo;
