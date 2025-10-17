package cloud.catfish.es.template;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Elasticsearch SQL查询模板
 * 提供基于REST客户端的SQL查询功能
 */
@Slf4j
@Component
public class EsSqlTemplate {

    private final RestClient restClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public EsSqlTemplate(RestClient restClient) {
        this.restClient = restClient;
    }

    /**
     * 执行SQL查询并返回指定类型的结果列表
     */
    public <T> List<T> query(String sql, Class<T> clazz) {
        try {
            List<Map<String, Object>> rows = queryForMaps(sql);
            return rows.stream()
                    .map(row -> mapper.convertValue(row, clazz))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("SQL查询失败 - sql: {}", sql, e);
            throw new RuntimeException("SQL查询执行失败", e);
        }
    }

    /**
     * 执行SQL查询并返回Map列表
     */
    public List<Map<String, Object>> queryForMaps(String sql) {
        return queryForMaps(sql, Collections.emptyMap());
    }

    /**
     * 执行参数化SQL查询并返回Map列表
     */
    public List<Map<String, Object>> queryForMaps(String sql, Map<String, Object> params) {
        try {
            validateSql(sql);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", sql);
            
            if (params != null && !params.isEmpty()) {
                requestBody.put("params", params);
            }

            Request request = new Request("POST", "/_sql?format=json");
            request.setJsonEntity(mapper.writeValueAsString(requestBody));
            
            Response response = restClient.performRequest(request);
            Map<String, Object> result = mapper.readValue(response.getEntity().getContent(), Map.class);
            
            return parseRows(result);
        } catch (Exception e) {
            log.error("SQL查询失败 - sql: {}, params: {}", sql, params, e);
            throw new RuntimeException("SQL查询执行失败", e);
        }
    }

    /**
     * 执行SQL查询并返回单个结果
     */
    public <T> T queryForObject(String sql, Class<T> clazz) {
        return queryForObject(sql, Collections.emptyMap(), clazz);
    }

    /**
     * 执行参数化SQL查询并返回单个结果
     */
    public <T> T queryForObject(String sql, Map<String, Object> params, Class<T> clazz) {
        List<T> results = query(sql, clazz);
        if (results.isEmpty()) {
            return null;
        }
        if (results.size() > 1) {
            log.warn("SQL查询返回多个结果，只取第一个 - sql: {}", sql);
        }
        return results.get(0);
    }

    /**
     * 执行SQL查询并返回分页结果
     */
    public <T> PageResult<T> queryForPage(String sql, int page, int size, Class<T> clazz) {
        return queryForPage(sql, Collections.emptyMap(), page, size, clazz);
    }

    /**
     * 执行参数化SQL查询并返回分页结果
     */
    public <T> PageResult<T> queryForPage(String sql, Map<String, Object> params, int page, int size, Class<T> clazz) {
        try {
            // 构建分页SQL
            String pagedSql = buildPagedSql(sql, page, size);
            List<T> content = query(pagedSql, clazz);
            
            // 获取总数（移除LIMIT子句）
            String countSql = buildCountSql(sql);
            Long total = queryForObject(countSql, params, Long.class);
            
            return new PageResult<>(content, page, size, total != null ? total : 0L);
        } catch (Exception e) {
            log.error("SQL分页查询失败 - sql: {}, page: {}, size: {}", sql, page, size, e);
            throw new RuntimeException("SQL分页查询执行失败", e);
        }
    }

    /**
     * 执行SQL查询并返回计数
     */
    public long count(String sql) {
        return count(sql, Collections.emptyMap());
    }

    /**
     * 执行参数化SQL查询并返回计数
     */
    public long count(String sql, Map<String, Object> params) {
        String countSql = buildCountSql(sql);
        Long result = queryForObject(countSql, params, Long.class);
        return result != null ? result : 0L;
    }

    /**
     * 执行SQL查询并返回第一列的值列表
     */
    public <T> List<T> queryForList(String sql, Class<T> elementType) {
        return queryForList(sql, Collections.emptyMap(), elementType);
    }

    /**
     * 执行参数化SQL查询并返回第一列的值列表
     */
    public <T> List<T> queryForList(String sql, Map<String, Object> params, Class<T> elementType) {
        List<Map<String, Object>> rows = queryForMaps(sql, params);
        return rows.stream()
                .map(row -> {
                    if (row.isEmpty()) {
                        return null;
                    }
                    Object value = row.values().iterator().next();
                    return mapper.convertValue(value, elementType);
                })
                .collect(Collectors.toList());
    }

    /**
     * 执行SQL查询并返回游标结果（用于大数据量查询）
     */
    public SqlCursor queryCursor(String sql) {
        return queryCursor(sql, Collections.emptyMap());
    }

    /**
     * 执行参数化SQL查询并返回游标结果
     */
    public SqlCursor queryCursor(String sql, Map<String, Object> params) {
        try {
            validateSql(sql);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("query", sql);
            requestBody.put("fetch_size", 1000); // 默认批次大小
            
            if (params != null && !params.isEmpty()) {
                requestBody.put("params", params);
            }

            Request request = new Request("POST", "/_sql?format=json");
            request.setJsonEntity(mapper.writeValueAsString(requestBody));
            
            Response response = restClient.performRequest(request);
            Map<String, Object> result = mapper.readValue(response.getEntity().getContent(), Map.class);
            
            List<Map<String, Object>> rows = parseRows(result);
            String cursor = (String) result.get("cursor");
            
            return new SqlCursor(rows, cursor, this);
        } catch (Exception e) {
            log.error("SQL游标查询失败 - sql: {}", sql, e);
            throw new RuntimeException("SQL游标查询执行失败", e);
        }
    }

    /**
     * 获取游标的下一批数据
     */
    public SqlCursor nextCursor(String cursor) {
        try {
            if (!StringUtils.hasText(cursor)) {
                return new SqlCursor(Collections.emptyList(), null, this);
            }

            Map<String, Object> requestBody = Map.of("cursor", cursor);
            Request request = new Request("POST", "/_sql?format=json");
            request.setJsonEntity(mapper.writeValueAsString(requestBody));
            
            Response response = restClient.performRequest(request);
            Map<String, Object> result = mapper.readValue(response.getEntity().getContent(), Map.class);
            
            List<Map<String, Object>> rows = parseRows(result);
            String nextCursor = (String) result.get("cursor");
            
            return new SqlCursor(rows, nextCursor, this);
        } catch (Exception e) {
            log.error("获取SQL游标下一批数据失败 - cursor: {}", cursor, e);
            throw new RuntimeException("获取游标数据失败", e);
        }
    }

    /**
     * 关闭游标
     */
    public void closeCursor(String cursor) {
        try {
            if (!StringUtils.hasText(cursor)) {
                return;
            }

            Map<String, Object> requestBody = Map.of("cursor", cursor);
            Request request = new Request("POST", "/_sql/close");
            request.setJsonEntity(mapper.writeValueAsString(requestBody));
            
            restClient.performRequest(request);
            log.debug("SQL游标已关闭 - cursor: {}", cursor);
        } catch (Exception e) {
            log.warn("关闭SQL游标失败 - cursor: {}", cursor, e);
        }
    }

    /**
     * 解析SQL查询结果
     */
    private List<Map<String, Object>> parseRows(Map<String, Object> result) {
        List<Map<String, Object>> list = new ArrayList<>();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> columns = (List<Map<String, Object>>) result.get("columns");
        @SuppressWarnings("unchecked")
        List<List<Object>> rows = (List<List<Object>>) result.get("rows");

        if (columns == null || rows == null) {
            return list;
        }

        for (List<Object> row : rows) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 0; i < columns.size() && i < row.size(); i++) {
                String columnName = columns.get(i).get("name").toString();
                map.put(columnName, row.get(i));
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 构建分页SQL
     */
    private String buildPagedSql(String sql, int page, int size) {
        String upperSql = sql.toUpperCase().trim();
        if (upperSql.contains("LIMIT")) {
            // 如果已有LIMIT，替换它
            return sql.replaceAll("(?i)\\s+LIMIT\\s+\\d+(?:\\s*,\\s*\\d+)?", 
                    " LIMIT " + (page * size) + ", " + size);
        } else {
            // 添加LIMIT
            return sql + " LIMIT " + (page * size) + ", " + size;
        }
    }

    /**
     * 构建计数SQL
     */
    private String buildCountSql(String sql) {
        String upperSql = sql.toUpperCase().trim();
        
        // 移除ORDER BY和LIMIT子句
        String countSql = sql.replaceAll("(?i)\\s+ORDER\\s+BY\\s+[^\\s]+(?:\\s+(?:ASC|DESC))?", "");
        countSql = countSql.replaceAll("(?i)\\s+LIMIT\\s+\\d+(?:\\s*,\\s*\\d+)?", "");
        
        // 如果是简单的SELECT语句，包装为COUNT查询
        if (upperSql.startsWith("SELECT") && !upperSql.contains("GROUP BY")) {
            return "SELECT COUNT(*) FROM (" + countSql + ") AS count_query";
        }
        
        return countSql;
    }

    /**
     * 验证SQL语句
     */
    private void validateSql(String sql) {
        if (!StringUtils.hasText(sql)) {
            throw new IllegalArgumentException("SQL语句不能为空");
        }
        
        String upperSql = sql.toUpperCase().trim();
        if (!upperSql.startsWith("SELECT") && !upperSql.startsWith("SHOW") && !upperSql.startsWith("DESCRIBE")) {
            throw new IllegalArgumentException("只支持SELECT、SHOW、DESCRIBE查询语句");
        }
    }

    /**
     * 分页结果包装类
     */
    public static class PageResult<T> {
        private final List<T> content;
        private final int page;
        private final int size;
        private final long total;
        private final int totalPages;

        public PageResult(List<T> content, int page, int size, long total) {
            this.content = content != null ? content : Collections.emptyList();
            this.page = page;
            this.size = size;
            this.total = total;
            this.totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        }

        public List<T> getContent() { return content; }
        public int getPage() { return page; }
        public int getSize() { return size; }
        public long getTotal() { return total; }
        public int getTotalPages() { return totalPages; }
        public boolean hasNext() { return page < totalPages - 1; }
        public boolean hasPrevious() { return page > 0; }
    }

    /**
     * SQL游标包装类
     */
    public static class SqlCursor {
        private final List<Map<String, Object>> rows;
        private final String cursor;
        private final EsSqlTemplate template;

        public SqlCursor(List<Map<String, Object>> rows, String cursor, EsSqlTemplate template) {
            this.rows = rows != null ? rows : Collections.emptyList();
            this.cursor = cursor;
            this.template = template;
        }

        public List<Map<String, Object>> getRows() { return rows; }
        public String getCursor() { return cursor; }
        public boolean hasNext() { return StringUtils.hasText(cursor); }
        
        public SqlCursor next() {
            return template.nextCursor(cursor);
        }
        
        public void close() {
            template.closeCursor(cursor);
        }
    }
}