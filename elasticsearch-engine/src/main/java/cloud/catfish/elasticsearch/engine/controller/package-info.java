/**
 * REST控制器包
 * 
 * 本包包含Elasticsearch搜索模块的所有REST API控制器，提供标准的RESTful接口�? * 所有控制器都遵循统一的API设计规范，返回标准的JSON响应格式�? * 
 * <h2>主要控制�?/h2>
 * <ul>
 *   <li>{@link cloud.catfish.elasticsearch.engine.controller.HighFrequencyQueryController} - 高频查询API控制�?/li>
 *   <li>{@link cloud.catfish.elasticsearch.engine.controller.TestDataController} - 测试数据管理控制�?/li>
 * </ul>
 * 
 * <h2>API设计规范</h2>
 * <ul>
 *   <li>统一使用 {@code /api/es} 作为基础路径前缀</li>
 *   <li>查询接口使用 {@code /api/es/query} 路径</li>
 *   <li>测试接口使用 {@code /api/es/test} 路径</li>
 *   <li>所有响应都包装�?{@code CommonResult} �?/li>
 *   <li>支持分页查询，使用标准的 {@code page} �?{@code size} 参数</li>
 *   <li>异常处理由全局异常处理器统一处理</li>
 * </ul>
 * 
 * <h2>响应格式</h2>
 * <pre>{@code
 * {
 *   "code": 200,
 *   "message": "操作成功",
 *   "data": {
 *     // 具体数据内容
 *   }
 * }
 * }</pre>
 * 
 * <h2>分页响应格式</h2>
 * <pre>{@code
 * {
 *   "code": 200,
 *   "message": "操作成功",
 *   "data": {
 *     "content": [...],
 *     "totalElements": 100,
 *     "totalPages": 10,
 *     "size": 10,
 *     "number": 0
 *   }
 * }
 * }</pre>
 * 
 * @author catfish
 * @version 1.0.0
 * @since 1.0.0
 */
package cloud.catfish.elasticsearch.engine.controller;
