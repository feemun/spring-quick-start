package cloud.catfish.es.template;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Elasticsearch SQL查询模板
 * 继承EsDslTemplate，专门为@EsSQL注解提供SQL到DSL转换功能
 * 
 * 核心处理流程：
 * 1. 解析SQL和参数
 * 2. 调用ES SQL转换API将SQL转为DSL
 * 3. 调用父类EsDslTemplate的方法执行DSL查询
 * 4. 返回结果
 */
@Slf4j
@Component
public class EsSqlTemplate extends EsDslTemplate {

    // SQL参数占位符模式
    private static final Pattern PARAM_PATTERN = Pattern.compile("#\\{([^}]+)\\}");

    public EsSqlTemplate(RestClient restClient) {
        super(restClient);
    }

    /**
     * 重写父类的search方法 - 支持SQL查询
     * 将SQL转换为DSL后调用父类方法
     */
    public <T> T search(String sql, Map<String, Object> params, String index, Class<T> clazz) {
        try {
            String dslJson = convertSqlToDsl(sql, params);
            return super.search(index, dslJson, clazz);
        } catch (Exception e) {
            log.error("SQL搜索查询失败 - sql: {}, params: {}, index: {}", sql, params, index, e);
            throw new RuntimeException("SQL搜索查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 重写父类的searchWithPagination方法 - 支持SQL查询
     * 将SQL转换为DSL后调用父类方法
     */
    public <T> T searchWithPagination(String sql, Map<String, Object> params, String index, int from, int size, Class<T> clazz) {
        try {
            String dslJson = convertSqlToDsl(sql, params);
            return super.searchWithPagination(index, dslJson, from, size, clazz);
        } catch (Exception e) {
            log.error("SQL分页搜索查询失败 - sql: {}, params: {}, index: {}, from: {}, size: {}", sql, params, index, from, size, e);
            throw new RuntimeException("SQL分页搜索查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 重写父类的count方法 - 支持SQL查询
     * 将SQL转换为DSL后调用父类方法
     */
    public <T> T count(String sql, Map<String, Object> params, String index, Class<T> clazz) {
        try {
            String dslJson = convertSqlToDsl(sql, params);
            return super.count(index, dslJson, clazz);
        } catch (Exception e) {
            log.error("SQL计数查询失败 - sql: {}, params: {}, index: {}", sql, params, index, e);
            throw new RuntimeException("SQL计数查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 重写父类的aggregation方法 - 支持SQL查询
     * 将SQL转换为DSL后调用父类方法
     */
    public <T> T aggregation(String sql, Map<String, Object> params, String index, Class<T> clazz) {
        try {
            String dslJson = convertSqlToDsl(sql, params);
            return super.aggregation(index, dslJson, clazz);
        } catch (Exception e) {
            log.error("SQL聚合查询失败 - sql: {}, params: {}, index: {}", sql, params, index, e);
            throw new RuntimeException("SQL聚合查询执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 核心方法：将SQL转换为DSL并执行查询
     * 专门为@EsSQL注解使用，保持向后兼容
     */
    public <T> T sqlToDslAndExecute(String sql, Map<String, Object> params, String index, Class<T> clazz) {
        return search(sql, params, index, clazz);
    }

    /**
     * 将SQL转换为DSL
     * 私有方法，供内部使用
     */
    private String convertSqlToDsl(String sql, Map<String, Object> params) throws Exception {
        // 1. 预处理SQL - 替换参数占位符
        String processedSql = replaceNamedParameters(sql, params);
        log.debug("处理后的SQL: {}", processedSql);
        
        // 2. 调用ES SQL转换API并直接返回DSL
        return translateSqlToDsl(processedSql);
    }

    /**
     * 替换SQL中的命名参数
     * 将#{paramName}格式的参数替换为实际值
     */
    private String replaceNamedParameters(String sql, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return sql;
        }

        Matcher matcher = PARAM_PATTERN.matcher(sql);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String paramName = matcher.group(1);
            Object paramValue = params.get(paramName);
            
            if (paramValue != null) {
                String replacement;
                if (paramValue instanceof String) {
                    replacement = "'" + paramValue.toString().replace("'", "''") + "'";
                } else {
                    replacement = paramValue.toString();
                }
                matcher.appendReplacement(result, replacement);
            } else {
                throw new IllegalArgumentException("参数 " + paramName + " 未找到");
            }
        }
        matcher.appendTail(result);
        
        return result.toString();
    }

}