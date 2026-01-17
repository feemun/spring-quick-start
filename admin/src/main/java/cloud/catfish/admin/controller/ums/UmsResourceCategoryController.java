package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsResourceCategoryService;
import cloud.catfish.api.domain.UmsResourceCategory;
import cloud.catfish.api.req.UmsResourceCategoryCreateParam;
import cloud.catfish.api.req.UmsResourceCategoryUpdateParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台资源分类管理Controller
 * Created by macro on 2020/2/5.
 */
@RestController
@Tag(name = "UmsResourceCategoryController", description = "后台资源分类管理")
@RequestMapping("/resourceCategory")
@RequiredArgsConstructor
@Validated
public class UmsResourceCategoryController {
    private final UmsResourceCategoryService resourceCategoryService;

    @Operation(summary = "查询所有后台资源分类")
    @GetMapping
    public ResponseEntity<List<UmsResourceCategory>> listAll() {
        List<UmsResourceCategory> resourceList = resourceCategoryService.listAll();
        return ResponseEntity.ok(resourceList);
    }

    @Operation(summary = "添加后台资源分类")
    @PostMapping
    public ResponseEntity<UmsResourceCategory> create(@Valid @RequestBody UmsResourceCategoryCreateParam param) {
        UmsResourceCategory category = new UmsResourceCategory();
        category.setName(param.getName());
        category.setSort(param.getSort());
        int count = resourceCategoryService.create(category);
        if (count <= 0) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.status(201).body(category);
    }

    @Operation(summary = "修改后台资源分类")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable @NotNull Long id, @Valid @RequestBody UmsResourceCategoryUpdateParam param) {
        UmsResourceCategory category = new UmsResourceCategory();
        category.setName(param.getName());
        category.setSort(param.getSort());
        int count = resourceCategoryService.update(id, category);
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "根据ID删除后台资源分类")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NotNull Long id) {
        int count = resourceCategoryService.delete(id);
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
