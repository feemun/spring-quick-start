package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsRoleService;
import cloud.catfish.api.common.CommonPage;
import cloud.catfish.api.common.R;
import cloud.catfish.api.domain.UmsMenu;
import cloud.catfish.api.domain.UmsResource;
import cloud.catfish.api.domain.UmsRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "UmsRoleController", description = "后台用户角色管理")
@RequestMapping("/role")
public class UmsRoleController {

    @Resource
    private UmsRoleService roleService;

    @Operation(summary = "添加角色")
    @PostMapping(value = "/create")
    public R<Integer> create(@RequestBody UmsRole role) {
        int count = roleService.create(role);
        if (count > 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "修改角色")
    @PostMapping(value = "/update/{id}")
    public R<Integer> update(@PathVariable Long id, @RequestBody UmsRole role) {
        int count = roleService.update(id, role);
        if (count > 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "批量删除角色")
    @PostMapping(value = "/delete")
    public R<Integer> delete(@RequestParam("ids") List<Long> ids) {
        int count = roleService.delete(ids);
        if (count > 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "获取所有角色")
    @GetMapping(value = "/listAll")
    public R<List<UmsRole>> listAll() {
        List<UmsRole> roleList = roleService.list();
        return R.ok(roleList);
    }

    @Operation(summary = "角色名模糊拆查询分页")
    @GetMapping(value = "/list")
    public R<CommonPage<UmsRole>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsRole> roleList = roleService.list(keyword, pageSize, pageNum);
        return R.ok(CommonPage.restPage(roleList));
    }

    @Operation(summary = "启用或停止角色")
    @PostMapping(value = "/updateStatus/{id}")
    public R<Integer> updateStatus(@PathVariable Long id, @RequestParam(value = "status") Boolean status) {
        UmsRole umsRole = new UmsRole();
        umsRole.setStatus(status);
        int count = roleService.update(id, umsRole);
        if (count > 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "获取角色关联菜单")
    @GetMapping(value = "/listMenu/{roleId}")
    public R<List<UmsMenu>> getRoleRelatedMenu(@PathVariable Long roleId) {
        List<UmsMenu> roleList = roleService.getRoleRelatedMenu(roleId);
        return R.ok(roleList);
    }

    @Operation(summary = "获取角色关联资源")
    @GetMapping(value = "/listResource/{roleId}")
    public R<List<UmsResource>> getRoleRelatedResource(@PathVariable Long roleId) {
        List<UmsResource> roleList = roleService.getRoleRelatedResource(roleId);
        return R.ok(roleList);
    }

    @Operation(summary = "给角色关联菜单")
    @PostMapping(value = "/allocMenu")
    public R<Integer> allocateMenu2Role(@RequestParam Long roleId,
                                        @RequestParam List<Long> menuIds) {
        int count = roleService.allocateMenu2Role(roleId, menuIds);
        return R.ok(count);
    }

    @Operation(summary = "给角色关联资源")
    @PostMapping(value = "/allocResource")
    public R<Integer> allocateResource2Role(@RequestParam Long roleId,
                                            @RequestParam List<Long> resourceIds) {
        int count = roleService.allocateResource2Role(roleId, resourceIds);
        return R.ok(count);
    }

}
