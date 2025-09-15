package cloud.catfish.es.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

/**
 * 通用搜索响应DTO
 * 
 * @author catfish
 * @since 1.0.0
 */
@Schema(description = "通用搜索响应")
public class SearchResponse<T> {

    @Schema(description = "搜索结果列表")
    private List<SearchHit<T>> hits;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "每页大小")
    private Integer pageSize;

    @Schema(description = "总页数")
    private Integer totalPages;

    @Schema(description = "查询耗时(毫秒)")
    private Long took;

    @Schema(description = "聚合结果")
    private Map<String, Object> aggregations;

    // Constructors
    public SearchResponse() {}

    public SearchResponse(List<SearchHit<T>> hits, Long total, Integer pageNum, Integer pageSize) {
        this.hits = hits;
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    // Getters and Setters
    public List<SearchHit<T>> getHits() {
        return hits;
    }

    public void setHits(List<SearchHit<T>> hits) {
        this.hits = hits;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
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

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTook() {
        return took;
    }

    public void setTook(Long took) {
        this.took = took;
    }

    public Map<String, Object> getAggregations() {
        return aggregations;
    }

    public void setAggregations(Map<String, Object> aggregations) {
        this.aggregations = aggregations;
    }

    /**
     * 搜索命中结果内部类
     */
    @Schema(description = "搜索命中结果")
    public static class SearchHit<T> {
        @Schema(description = "文档ID")
        private String id;

        @Schema(description = "文档内容")
        private T source;

        @Schema(description = "评分")
        private Float score;

        @Schema(description = "高亮内容")
        private Map<String, List<String>> highlight;

        // Constructors
        public SearchHit() {}

        public SearchHit(String id, T source, Float score) {
            this.id = id;
            this.source = source;
            this.score = score;
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public T getSource() {
            return source;
        }

        public void setSource(T source) {
            this.source = source;
        }

        public Float getScore() {
            return score;
        }

        public void setScore(Float score) {
            this.score = score;
        }

        public Map<String, List<String>> getHighlight() {
            return highlight;
        }

        public void setHighlight(Map<String, List<String>> highlight) {
            this.highlight = highlight;
        }
    }
}