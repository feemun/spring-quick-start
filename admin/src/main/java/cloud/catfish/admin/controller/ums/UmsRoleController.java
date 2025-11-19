package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsRoleService;
import cloud.catfish.common.api.CommonPage;
import cloud.catfish.common.api.R;
import domain.UmsMenu;
import domain.UmsResource;
import domain.UmsRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台用户角色管理Controller
 * Created by macro on 2018/9/30.
 */
@RestController
@Tag(name = "UmsRoleController", description = "后台用户角色管理")
@RequestMapping("/role")
public class UmsRoleController {
    @Resource
    private UmsRoleService roleService;

    @Operation(summary = "添加角色")
    @PostMapping(value = "/create")
    public R create(@RequestBody UmsRole role) {
        int count = roleService.create(role);
        if (count > 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "修改角色")
    @PostMapping(value = "/update/{id}")
    public R update(@PathVariable Long id, @RequestBody UmsRole role) {
        int count = roleService.update(id, role);
        if (count > 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "批量删除角色")
    @PostMapping(value = "/delete")
    public R delete(@RequestParam("ids") List<Long> ids) {
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

    @Operation(summary = "根据角色名称分页获取角色列表")
    @GetMapping(value = "/list")
    public R<CommonPage<UmsRole>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsRole> roleList = roleService.list(keyword, pageSize, pageNum);
        return R.ok(CommonPage.restPage(roleList));
    }

    @Operation(summary = "修改角色状态")
    @PostMapping(value = "/updateStatus/{id}")
    public R updateStatus(@PathVariable Long id, @RequestParam(value = "status") Boolean status) {
        UmsRole umsRole = new UmsRole();
        umsRole.setStatus(status);
        int count = roleService.update(id, umsRole);
        if (count > 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "获取角色相关菜单")
    @GetMapping(value = "/listMenu/{roleId}")
    public R<List<UmsMenu>> listMenu(@PathVariable Long roleId) {
        List<UmsMenu> roleList = roleService.listMenu(roleId);
        return R.ok(roleList);
    }

    @Operation(summary = "获取角色相关资源")
    @GetMapping(value = "/listResource/{roleId}")
    public R<List<UmsResource>> listResource(@PathVariable Long roleId) {
        List<UmsResource> roleList = roleService.listResource(roleId);
        return R.ok(roleList);
    }

    @Operation(summary = "给角色分配菜单")
    @PostMapping(value = "/allocMenu")
    public R allocMenu(@RequestParam Long roleId, @RequestParam List<Long> menuIds) {
        int count = roleService.allocMenu(roleId, menuIds);
        return R.ok(count);
    }

    @Operation(summary = "给角色分配资源")
    @PostMapping(value = "/allocResource")
    public R allocResource(@RequestParam Long roleId, @RequestParam List<Long> resourceIds) {
        int count = roleService.allocResource(roleId, resourceIds);
        return R.ok(count);
    }

}
