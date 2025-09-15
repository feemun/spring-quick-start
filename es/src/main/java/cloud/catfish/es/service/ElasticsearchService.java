package cloud.catfish.es.service;

import cloud.catfish.es.dto.SearchRequest;
import cloud.catfish.es.dto.SearchResponse;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch通用查询服务接口
 * 
 * @author catfish
 * @since 1.0.0
 */
public interface ElasticsearchService {

    /**
     * 通用搜索
     * 
     * @param searchRequest 搜索请求
     * @return 搜索结果
     */
    SearchResponse<Map<String, Object>> search(SearchRequest searchRequest);

    /**
     * 根据ID获取文档
     * 
     * @param index 索引名称
     * @param id 文档ID
     * @return 文档内容
     */
    Map<String, Object> getById(String index, String id);

    /**
     * 保存文档
     * 
     * @param index 索引名称
     * @param id 文档ID
     * @param document 文档内容
     * @return 是否成功
     */
    boolean save(String index, String id, Map<String, Object> document);

    /**
     * 批量保存文档
     * 
     * @param index 索引名称
     * @param documents 文档列表，key为文档ID，value为文档内容
     * @return 是否成功
     */
    boolean batchSave(String index, Map<String, Map<String, Object>> documents);

    /**
     * 删除文档
     * 
     * @param index 索引名称
     * @param id 文档ID
     * @return 是否成功
     */
    boolean deleteById(String index, String id);

    /**
     * 批量删除文档
     * 
     * @param index 索引名称
     * @param ids 文档ID列表
     * @return 是否成功
     */
    boolean batchDelete(String index, List<String> ids);

    /**
     * 检查索引是否存在
     * 
     * @param index 索引名称
     * @return 是否存在
     */
    boolean indexExists(String index);

    /**
     * 创建索引
     * 
     * @param index 索引名称
     * @param mapping 映射配置
     * @return 是否成功
     */
    boolean createIndex(String index, Map<String, Object> mapping);

    /**
     * 删除索引
     * 
     * @param index 索引名称
     * @return 是否成功
     */
    boolean deleteIndex(String index);

    /**
     * 获取索引映射
     * 
     * @param index 索引名称
     * @return 映射配置
     */
    Map<String, Object> getMapping(String index);

    /**
     * 聚合查询
     * 
     * @param index 索引名称
     * @param aggregationQuery 聚合查询条件
     * @return 聚合结果
     */
    Map<String, Object> aggregate(String index, Map<String, Object> aggregationQuery);
}