package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsResourceService;
import cloud.catfish.common.api.CommonPage;
import cloud.catfish.common.api.R;
import cloud.catfish.security.component.DynamicSecurityMetadataSource;
import domain.UmsResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台资源管理Controller
 * Created by macro on 2020/2/4.
 */
@Tag(name = "UmsResourceController", description = "后台资源管理")
@RestController
@RequestMapping("/resource")
public class UmsResourceController {

    @Resource
    private UmsResourceService resourceService;
    @Resource
    private DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    @Operation(summary = "添加后台资源")
    @PostMapping(value = "/create")
    public R create(@RequestBody UmsResource umsResource) {
        int count = resourceService.create(umsResource);
        dynamicSecurityMetadataSource.clearDataSource();
        if (count > 0) {
            return R.ok(count);
        } else {
            return R.failed();
        }
    }

    @Operation(summary = "修改后台资源")
    @PostMapping(value = "/update/{id}")
    public R update(@PathVariable Long id,
                    @RequestBody UmsResource umsResource) {
        int count = resourceService.update(id, umsResource);
        dynamicSecurityMetadataSource.clearDataSource();
        if (count > 0) {
            return R.ok(count);
        } else {
            return R.failed();
        }
    }

    @Operation(summary = "根据ID获取资源详情")
    @GetMapping(value = "/{id}")
    public R<UmsResource> getItem(@PathVariable Long id) {
        UmsResource umsResource = resourceService.getItem(id);
        return R.ok(umsResource);
    }

    @Operation(summary = "根据ID删除后台资源")
    @PostMapping(value = "/delete/{id}")
    public R delete(@PathVariable Long id) {
        int count = resourceService.delete(id);
        dynamicSecurityMetadataSource.clearDataSource();
        if (count > 0) {
            return R.ok(count);
        } else {
            return R.failed();
        }
    }

    @Operation(summary = "分页模糊查询后台资源")
    @GetMapping(value = "/list")
    public R<CommonPage<UmsResource>> list(@RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) String nameKeyword,
                                           @RequestParam(required = false) String urlKeyword,
                                           @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsResource> resourceList = resourceService.list(categoryId,nameKeyword, urlKeyword, pageSize, pageNum);
        return R.ok(CommonPage.restPage(resourceList));
    }

    @Operation(summary = "查询所有后台资源")
    @GetMapping(value = "/listAll")
    public R<List<UmsResource>> listAll() {
        List<UmsResource> resourceList = resourceService.listAll();
        return R.ok(resourceList);
    }
}
