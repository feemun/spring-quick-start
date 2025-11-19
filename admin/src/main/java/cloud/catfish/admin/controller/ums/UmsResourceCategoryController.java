package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsResourceCategoryService;
import cloud.catfish.common.api.R;
import domain.UmsResourceCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台资源分类管理Controller
 * Created by macro on 2020/2/5.
 */
@RestController
@Tag(name = "UmsResourceCategoryController", description = "后台资源分类管理")
@RequestMapping("/resourceCategory")
public class UmsResourceCategoryController {
    @Resource
    private UmsResourceCategoryService resourceCategoryService;

    @Operation(summary = "查询所有后台资源分类")
    @GetMapping(value = "/listAll")
    public R<List<UmsResourceCategory>> listAll() {
        List<UmsResourceCategory> resourceList = resourceCategoryService.listAll();
        return R.ok(resourceList);
    }

    @Operation(summary = "添加后台资源分类")
    @PostMapping(value = "/create")
    public R create(@RequestBody UmsResourceCategory umsResourceCategory) {
        int count = resourceCategoryService.create(umsResourceCategory);
        if (count > 0) {
            return R.ok(count);
        } else {
            return R.failed();
        }
    }

    @Operation(summary = "修改后台资源分类")
    @PostMapping(value = "/update/{id}")
    public R update(@PathVariable Long id,
                    @RequestBody UmsResourceCategory umsResourceCategory) {
        int count = resourceCategoryService.update(id, umsResourceCategory);
        if (count > 0) {
            return R.ok(count);
        } else {
            return R.failed();
        }
    }

    @Operation(summary = "根据ID删除后台资源分类")
    @PostMapping(value = "/delete/{id}")
    public R delete(@PathVariable Long id) {
        int count = resourceCategoryService.delete(id);
        if (count > 0) {
            return R.ok(count);
        } else {
            return R.failed();
        }
    }
}
