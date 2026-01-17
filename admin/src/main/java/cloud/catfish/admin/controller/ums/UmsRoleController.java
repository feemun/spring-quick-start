package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsRoleService;
import cloud.catfish.api.common.CommonPage;
import cloud.catfish.api.domain.UmsMenu;
import cloud.catfish.api.domain.UmsResource;
import cloud.catfish.api.domain.UmsRole;
import cloud.catfish.api.req.EnabledParam;
import cloud.catfish.api.req.IdListParam;
import cloud.catfish.api.req.UmsRoleCreateParam;
import cloud.catfish.api.req.UmsRoleUpdateParam;
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

@RestController
@Tag(name = "UmsRoleController", description = "后台用户角色管理")
@RequestMapping("/role")
@RequiredArgsConstructor
@Validated
public class UmsRoleController {

    private final UmsRoleService roleService;

    @Operation(summary = "添加角色")
    @PostMapping
    public ResponseEntity<UmsRole> create(@Valid @RequestBody UmsRoleCreateParam param) {
        UmsRole role = new UmsRole();
        role.setName(param.getName());
        role.setDescription(param.getDescription());
        if (param.getStatus() != null) {
            role.setStatus(param.getStatus() != 0);
        }
        role.setSort(param.getSort());
        int count = roleService.create(role);
        if (count <= 0) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.status(201).body(role);
    }

    @Operation(summary = "修改角色")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable @NotNull Long id, @Valid @RequestBody UmsRoleUpdateParam param) {
        UmsRole role = new UmsRole();
        role.setName(param.getName());
        role.setDescription(param.getDescription());
        if (param.getStatus() != null) {
            role.setStatus(param.getStatus() != 0);
        }
        role.setSort(param.getSort());
        int count = roleService.update(id, role);
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NotNull Long id) {
        int count = roleService.delete(List.of(id));
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "获取所有角色")
    @GetMapping(value = "/all")
    public ResponseEntity<List<UmsRole>> listAll() {
        List<UmsRole> roleList = roleService.list();
        return ResponseEntity.ok(roleList);
    }

    @Operation(summary = "角色查询分页")
    @GetMapping
    public ResponseEntity<CommonPage<UmsRole>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer pageSize,
            @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer pageNum
    ) {
        List<UmsRole> roleList = roleService.list(keyword, pageSize, pageNum);
        return ResponseEntity.ok(CommonPage.restPage(roleList));
    }

    @Operation(summary = "启用或停止角色")
    @PatchMapping(value = "/{id}/enabled")
    public ResponseEntity<Void> updateStatus(@PathVariable @NotNull Long id, @Valid @RequestBody EnabledParam param) {
        UmsRole umsRole = new UmsRole();
        umsRole.setStatus(Boolean.TRUE.equals(param.getEnabled()));
        int count = roleService.update(id, umsRole);
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "获取角色关联菜单")
    @GetMapping(value = "/{roleId}/menus")
    public ResponseEntity<List<UmsMenu>> getRoleRelatedMenu(@PathVariable @NotNull Long roleId) {
        List<UmsMenu> roleList = roleService.getRoleRelatedMenu(roleId);
        return ResponseEntity.ok(roleList);
    }

    @Operation(summary = "获取角色关联资源")
    @GetMapping(value = "/{roleId}/resources")
    public ResponseEntity<List<UmsResource>> getRoleRelatedResource(@PathVariable @NotNull Long roleId) {
        List<UmsResource> roleList = roleService.getRoleRelatedResource(roleId);
        return ResponseEntity.ok(roleList);
    }

    @Operation(summary = "给角色关联菜单")
    @PutMapping(value = "/{roleId}/menus")
    public ResponseEntity<Void> allocateMenu2Role(@PathVariable @NotNull Long roleId, @Valid @RequestBody IdListParam param) {
        roleService.allocateMenu2Role(roleId, param.getIds());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "给角色关联资源")
    @PutMapping(value = "/{roleId}/resources")
    public ResponseEntity<Void> allocateResource2Role(@PathVariable @NotNull Long roleId, @Valid @RequestBody IdListParam param) {
        roleService.allocateResource2Role(roleId, param.getIds());
        return ResponseEntity.noContent().build();
    }

}
