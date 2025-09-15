package cloud.catfish.es.controller;

import cloud.catfish.common.result.Result;
import cloud.catfish.es.dto.SearchRequest;
import cloud.catfish.es.dto.SearchResponse;
import cloud.catfish.es.service.ElasticsearchService;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Elasticsearch通用查询控制器
 * 
 * @author catfish
 * @since 1.0.0
 */
@Tag(name = "Elasticsearch", description = "Elasticsearch通用查询API")
@RestController
@RequestMapping("/api/es")
public class ElasticsearchController {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchController.class);

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Operation(summary = "通用搜索", description = "支持关键词搜索、精确匹配、范围查询等多种搜索方式")
    @PostMapping("/search")
    public Result<SearchResponse<Map<String, Object>>> search(@RequestBody SearchRequest searchRequest) {
        try {
            // 参数校验
            if (StrUtil.isBlank(searchRequest.getIndex())) {
                return Result.error("索引名称不能为空");
            }
            
            // 设置默认值
            if (searchRequest.getPageNum() == null || searchRequest.getPageNum() < 1) {
                searchRequest.setPageNum(1);
            }
            if (searchRequest.getPageSize() == null || searchRequest.getPageSize() < 1) {
                searchRequest.setPageSize(10);
            }
            
            SearchResponse<Map<String, Object>> response = elasticsearchService.search(searchRequest);
            return Result.success(response);
        } catch (Exception e) {
            logger.error("搜索失败: {}", e.getMessage(), e);
            return Result.error("搜索失败: " + e.getMessage());
        }
    }

    @Operation(summary = "根据ID获取文档", description = "根据文档ID获取单个文档")
    @GetMapping("/{index}/{id}")
    public Result<Map<String, Object>> getById(
            @Parameter(description = "索引名称") @PathVariable String index,
            @Parameter(description = "文档ID") @PathVariable String id) {
        try {
            if (StrUtil.isBlank(index) || StrUtil.isBlank(id)) {
                return Result.error("索引名称和文档ID不能为空");
            }
            
            Map<String, Object> document = elasticsearchService.getById(index, id);
            if (document == null) {
                return Result.error("文档不存在");
            }
            
            return Result.success(document);
        } catch (Exception e) {
            logger.error("获取文档失败: {}", e.getMessage(), e);
            return Result.error("获取文档失败: " + e.getMessage());
        }
    }

    @Operation(summary = "保存文档", description = "保存单个文档到指定索引")
    @PostMapping("/{index}")
    public Result<String> save(
            @Parameter(description = "索引名称") @PathVariable String index,
            @Parameter(description = "文档ID，可选") @RequestParam(required = false) String id,
            @RequestBody Map<String, Object> document) {
        try {
            if (StrUtil.isBlank(index) || CollUtil.isEmpty(document)) {
                return Result.error("索引名称和文档内容不能为空");
            }
            
            // 如果没有提供ID，生成一个UUID
            if (StrUtil.isBlank(id)) {
                id = java.util.UUID.randomUUID().toString();
            }
            
            boolean success = elasticsearchService.save(index, id, document);
            if (success) {
                return Result.success("文档保存成功，ID: " + id);
            } else {
                return Result.error("文档保存失败");
            }
        } catch (Exception e) {
            logger.error("保存文档失败: {}", e.getMessage(), e);
            return Result.error("保存文档失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量保存文档", description = "批量保存多个文档到指定索引")
    @PostMapping("/{index}/batch")
    public Result<String> batchSave(
            @Parameter(description = "索引名称") @PathVariable String index,
            @RequestBody Map<String, Map<String, Object>> documents) {
        try {
            if (StrUtil.isBlank(index) || CollUtil.isEmpty(documents)) {
                return Result.error("索引名称和文档内容不能为空");
            }
            
            boolean success = elasticsearchService.batchSave(index, documents);
            if (success) {
                return Result.success("批量保存成功，共保存 " + documents.size() + " 个文档");
            } else {
                return Result.error("批量保存失败");
            }
        } catch (Exception e) {
            logger.error("批量保存文档失败: {}", e.getMessage(), e);
            return Result.error("批量保存文档失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除文档", description = "根据文档ID删除单个文档")
    @DeleteMapping("/{index}/{id}")
    public Result<String> deleteById(
            @Parameter(description = "索引名称") @PathVariable String index,
            @Parameter(description = "文档ID") @PathVariable String id) {
        try {
            if (StrUtil.isBlank(index) || StrUtil.isBlank(id)) {
                return Result.error("索引名称和文档ID不能为空");
            }
            
            boolean success = elasticsearchService.deleteById(index, id);
            if (success) {
                return Result.success("文档删除成功");
            } else {
                return Result.error("文档删除失败");
            }
        } catch (Exception e) {
            logger.error("删除文档失败: {}", e.getMessage(), e);
            return Result.error("删除文档失败: " + e.getMessage());
        }
    }

    @Operation(summary = "批量删除文档", description = "根据文档ID列表批量删除文档")
    @DeleteMapping("/{index}/batch")
    public Result<String> batchDelete(
            @Parameter(description = "索引名称") @PathVariable String index,
            @RequestBody List<String> ids) {
        try {
            if (StrUtil.isBlank(index) || CollUtil.isEmpty(ids)) {
                return Result.error("索引名称和文档ID列表不能为空");
            }
            
            boolean success = elasticsearchService.batchDelete(index, ids);
            if (success) {
                return Result.success("批量删除成功，共删除 " + ids.size() + " 个文档");
            } else {
                return Result.error("批量删除失败");
            }
        } catch (Exception e) {
            logger.error("批量删除文档失败: {}", e.getMessage(), e);
            return Result.error("批量删除文档失败: " + e.getMessage());
        }
    }

    @Operation(summary = "检查索引是否存在", description = "检查指定索引是否存在")
    @GetMapping("/{index}/exists")
    public Result<Boolean> indexExists(@Parameter(description = "索引名称") @PathVariable String index) {
        try {
            if (StrUtil.isBlank(index)) {
                return Result.error("索引名称不能为空");
            }
            
            boolean exists = elasticsearchService.indexExists(index);
            return Result.success(exists);
        } catch (Exception e) {
            logger.error("检查索引是否存在失败: {}", e.getMessage(), e);
            return Result.error("检查索引是否存在失败: " + e.getMessage());
        }
    }

    @Operation(summary = "创建索引", description = "创建新的索引，可选择性地设置映射")
    @PostMapping("/{index}/create")
    public Result<String> createIndex(
            @Parameter(description = "索引名称") @PathVariable String index,
            @RequestBody(required = false) Map<String, Object> mapping) {
        try {
            if (StrUtil.isBlank(index)) {
                return Result.error("索引名称不能为空");
            }
            
            boolean success = elasticsearchService.createIndex(index, mapping);
            if (success) {
                return Result.success("索引创建成功");
            } else {
                return Result.error("索引创建失败");
            }
        } catch (Exception e) {
            logger.error("创建索引失败: {}", e.getMessage(), e);
            return Result.error("创建索引失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除索引", description = "删除指定的索引")
    @DeleteMapping("/{index}")
    public Result<String> deleteIndex(@Parameter(description = "索引名称") @PathVariable String index) {
        try {
            if (StrUtil.isBlank(index)) {
                return Result.error("索引名称不能为空");
            }
            
            boolean success = elasticsearchService.deleteIndex(index);
            if (success) {
                return Result.success("索引删除成功");
            } else {
                return Result.error("索引删除失败");
            }
        } catch (Exception e) {
            logger.error("删除索引失败: {}", e.getMessage(), e);
            return Result.error("删除索引失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取索引映射", description = "获取指定索引的映射配置")
    @GetMapping("/{index}/mapping")
    public Result<Map<String, Object>> getMapping(@Parameter(description = "索引名称") @PathVariable String index) {
        try {
            if (StrUtil.isBlank(index)) {
                return Result.error("索引名称不能为空");
            }
            
            Map<String, Object> mapping = elasticsearchService.getMapping(index);
            return Result.success(mapping);
        } catch (Exception e) {
            logger.error("获取索引映射失败: {}", e.getMessage(), e);
            return Result.error("获取索引映射失败: " + e.getMessage());
        }
    }

    @Operation(summary = "聚合查询", description = "执行聚合查询操作")
    @PostMapping("/{index}/aggregate")
    public Result<Map<String, Object>> aggregate(
            @Parameter(description = "索引名称") @PathVariable String index,
            @RequestBody Map<String, Object> aggregationQuery) {
        try {
            if (StrUtil.isBlank(index) || CollUtil.isEmpty(aggregationQuery)) {
                return Result.error("索引名称和聚合查询条件不能为空");
            }
            
            Map<String, Object> result = elasticsearchService.aggregate(index, aggregationQuery);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("聚合查询失败: {}", e.getMessage(), e);
            return Result.error("聚合查询失败: " + e.getMessage());
        }
    }
}