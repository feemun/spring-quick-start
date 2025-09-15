package cloud.catfish.es.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

/**
 * 通用搜索请求DTO
 * 
 * @author catfish
 * @since 1.0.0
 */
@Schema(description = "通用搜索请求")
public class SearchRequest {

    @Schema(description = "索引名称", example = "user_index")
    @NotBlank(message = "索引名称不能为空")
    private String index;

    @Schema(description = "搜索关键词", example = "张三")
    private String keyword;

    @Schema(description = "搜索字段列表", example = "[\"name\", \"email\"]")
    private List<String> fields;

    @Schema(description = "精确匹配条件")
    private Map<String, Object> exactMatch;

    @Schema(description = "范围查询条件")
    private Map<String, RangeQuery> rangeQuery;

    @Schema(description = "排序字段", example = "createTime")
    private String sortField;

    @Schema(description = "排序方向", example = "desc", allowableValues = {"asc", "desc"})
    private String sortOrder = "desc";

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize = 10;

    @Schema(description = "高亮字段")
    private List<String> highlightFields;

    // Getters and Setters
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Map<String, Object> getExactMatch() {
        return exactMatch;
    }

    public void setExactMatch(Map<String, Object> exactMatch) {
        this.exactMatch = exactMatch;
    }

    public Map<String, RangeQuery> getRangeQuery() {
        return rangeQuery;
    }

    public void setRangeQuery(Map<String, RangeQuery> rangeQuery) {
        this.rangeQuery = rangeQuery;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getHighlightFields() {
        return highlightFields;
    }

    public void setHighlightFields(List<String> highlightFields) {
        this.highlightFields = highlightFields;
    }

    /**
     * 范围查询内部类
     */
    @Schema(description = "范围查询条件")
    public static class RangeQuery {
        @Schema(description = "最小值")
        private Object gte;

        @Schema(description = "最大值")
        private Object lte;

        @Schema(description = "大于")
        private Object gt;

        @Schema(description = "小于")
        private Object lt;

        // Getters and Setters
        public Object getGte() {
            return gte;
        }

        public void setGte(Object gte) {
            this.gte = gte;
        }

        public Object getLte() {
            return lte;
        }

        public void setLte(Object lte) {
            this.lte = lte;
        }

        public Object getGt() {
            return gt;
        }

        public void setGt(Object gt) {
            this.gt = gt;
        }

        public Object getLt() {
            return lt;
        }

        public void setLt(Object lt) {
            this.lt = lt;
        }
    }
}