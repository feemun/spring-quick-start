package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsResourceService;
import cloud.catfish.api.common.CommonPage;
import cloud.catfish.security.component.DynamicSecurityMetadataSource;
import cloud.catfish.api.domain.UmsResource;
import cloud.catfish.api.req.UmsResourceCreateParam;
import cloud.catfish.api.req.UmsResourceUpdateParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台资源管理Controller
 * Created by macro on 2020/2/4.
 */
@Tag(name = "UmsResourceController", description = "后台资源管理")
@RestController
@RequestMapping("/resource")
@RequiredArgsConstructor
@Validated
public class UmsResourceController {

    private final UmsResourceService resourceService;
    private final DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    @Operation(summary = "添加后台资源")
    @PostMapping
    public ResponseEntity<UmsResource> create(@Valid @RequestBody UmsResourceCreateParam param) {
        UmsResource resource = new UmsResource();
        resource.setCategoryId(param.getCategoryId());
        resource.setName(param.getName());
        resource.setUrl(param.getUrl());
        resource.setDescription(param.getDescription());
        int count = resourceService.create(resource);
        dynamicSecurityMetadataSource.clearDataSource();
        if (count <= 0) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.status(201).body(resource);
    }

    @Operation(summary = "修改后台资源")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable @NotNull Long id, @Valid @RequestBody UmsResourceUpdateParam param) {
        UmsResource resource = new UmsResource();
        resource.setCategoryId(param.getCategoryId());
        resource.setName(param.getName());
        resource.setUrl(param.getUrl());
        resource.setDescription(param.getDescription());
        int count = resourceService.update(id, resource);
        dynamicSecurityMetadataSource.clearDataSource();
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "根据ID获取资源详情")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UmsResource> getItem(@PathVariable @NotNull Long id) {
        UmsResource umsResource = resourceService.getItem(id);
        if (umsResource == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(umsResource);
    }

    @Operation(summary = "根据ID删除后台资源")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NotNull Long id) {
        int count = resourceService.delete(id);
        dynamicSecurityMetadataSource.clearDataSource();
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "分页模糊查询后台资源")
    @GetMapping
    public ResponseEntity<CommonPage<UmsResource>> list(@RequestParam(required = false) Long categoryId,
                                           @RequestParam(value = "name", required = false) String nameKeyword,
                                           @RequestParam(value = "url", required = false) String urlKeyword,
                                           @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer pageSize,
                                           @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer pageNum) {
        List<UmsResource> resourceList = resourceService.list(categoryId,nameKeyword, urlKeyword, pageSize, pageNum);
        return ResponseEntity.ok(CommonPage.restPage(resourceList));
    }

    @Operation(summary = "查询所有后台资源")
    @GetMapping(value = "/all")
    public ResponseEntity<List<UmsResource>> listAll() {
        List<UmsResource> resourceList = resourceService.listAll();
        return ResponseEntity.ok(resourceList);
    }
}
