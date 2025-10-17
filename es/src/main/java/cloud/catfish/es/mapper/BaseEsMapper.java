package cloud.catfish.es.mapper;

import cloud.catfish.es.annotation.EsDSL;
import cloud.catfish.es.annotation.EsSQL;
import cloud.catfish.es.annotation.Param;
import cloud.catfish.es.template.EsSqlTemplate;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch Mapper基础接口
 * 提供通用的CRUD操作方法，类似于MyBatis的BaseMapper
 * 每个具体的Mapper接口对应一个特定的索引
 * 
 * @param <T> 实体类型
 * @param <ID> 主键类型
 */
public interface BaseEsMapper<T, ID> {

    /**
     * 根据ID查询单个文档
     * 索引名称由@EsMapper注解的defaultIndex或方法注解的index指定
     */
    @EsDSL(value = "{\"term\": {\"_id\": \"#{id}\"}}")
    T findById(@Param("id") ID id);

    /**
     * 查询所有文档
     */
    @EsSQL("SELECT * FROM {index}")
    List<T> findAll();

    /**
     * 根据条件查询文档列表
     */
    @EsDSL(value = "#{query}")
    List<T> findByQuery(@Param("query") Map<String, Object> query);

    /**
     * 分页查询文档
     */
    @EsSQL(value = "SELECT * FROM {index} LIMIT #{from}, #{size}", pageable = true)
    EsSqlTemplate.PageResult<T> findByPage(@Param("from") int from, @Param("size") int size);

    /**
     * 根据字段值查询
     */
    @EsDSL(value = "{\"term\": {\"#{field}\": \"#{value}\"}}")
    List<T> findByField(@Param("field") String field, @Param("value") Object value);

    /**
     * 模糊查询
     */
    @EsDSL(value = "{\"match\": {\"#{field}\": \"#{keyword}\"}}")
    List<T> findByKeyword(@Param("field") String field, @Param("keyword") String keyword);

    /**
     * 范围查询
     */
    @EsDSL(value = "{\"range\": {\"#{field}\": {\"gte\": \"#{from}\", \"lte\": \"#{to}\"}}}")
    List<T> findByRange(@Param("field") String field, 
                       @Param("from") Object from, 
                       @Param("to") Object to);

    /**
     * 计数查询
     */
    @EsDSL(value = "{\"match_all\": {}}", type = EsDSL.QueryType.COUNT)
    long count();

    /**
     * 根据条件计数
     */
    @EsDSL(value = "#{query}", type = EsDSL.QueryType.COUNT)
    long countByQuery(@Param("query") Map<String, Object> query);

    /**
     * 检查文档是否存在
     */
    @EsSQL("SELECT COUNT(*) FROM {index} WHERE _id = ?")
    boolean existsById(@Param("id") ID id);

    /**
     * 保存或更新文档
     * 注意：这个方法需要在具体实现中处理，因为涉及到索引操作
     */
    default void save(T entity) {
        throw new UnsupportedOperationException("Save operation should be implemented in concrete mapper or use EsDslTemplate directly");
    }

    /**
     * 根据ID删除文档
     */
    @EsSQL("DELETE FROM {index} WHERE _id = ?")
    boolean deleteById(@Param("id") ID id);

    /**
     * 批量删除
     */
    @EsSQL("DELETE FROM {index} WHERE _id IN (#{ids})")
    int deleteByIds(@Param("ids") List<ID> ids);
}