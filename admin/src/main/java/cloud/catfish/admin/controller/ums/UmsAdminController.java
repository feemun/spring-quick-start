package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsAdminService;
import cloud.catfish.admin.service.UmsRoleService;
import cloud.catfish.common.api.CommonPage;
import cloud.catfish.common.api.R;
import cn.hutool.core.collection.CollUtil;
import domain.UmsAdmin;
import domain.UmsRole;
import dto.UmsAdminLoginParam;
import dto.UmsAdminParam;
import dto.UpdateAdminPasswordParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台用户管理Controller
 * Created by macro on 2018/4/26.
 */
@RestController
@Tag(name = "UmsAdminController", description = "后台用户管理")
@RequestMapping("/admin")
public class UmsAdminController {
    
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Resource
    private UmsAdminService adminService;
    @Resource
    private UmsRoleService roleService;

    @Operation(summary = "用户注册")
    @PostMapping(value = "/register")
    public R<UmsAdmin> register(@Validated @RequestBody UmsAdminParam umsAdminParam) {
        UmsAdmin umsAdmin = adminService.register(umsAdminParam);
        if (umsAdmin == null) {
            return R.failed();
        }
        return R.ok(umsAdmin);
    }

    @Operation(summary = "登录以后返回token")
    @PostMapping(value = "/login")
    public R login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam) {
        String token = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (token == null) {
            return R.validateFailed("用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return R.ok(tokenMap);
    }

    @Operation(summary = "刷新token")
    @GetMapping(value = "/refreshToken")
    public R refreshToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String refreshToken = adminService.refreshToken(token);
        if (refreshToken == null) {
            return R.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return R.ok(tokenMap);
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping(value = "/info")
    public R getAdminInfo(Principal principal) {
        if (principal == null) {
            return R.unauthorized(null);
        }
        String username = principal.getName();
        UmsAdmin umsAdmin = adminService.getAdminByUsername(username);
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("menus", roleService.getMenuList(umsAdmin.getId()));
        data.put("icon", umsAdmin.getIcon());
        List<UmsRole> roleList = adminService.getRoleList(umsAdmin.getId());
        if (CollUtil.isNotEmpty(roleList)) {
            List<String> roles = roleList.stream().map(UmsRole::getName).collect(Collectors.toList());
            data.put("roles", roles);
        }
        return R.ok(data);
    }

    @Operation(summary = "登出功能")
    @PostMapping(value = "/logout")
    public R logout(Principal principal) {
        adminService.logout(principal.getName());
        return R.ok(null);
    }

    @Operation(summary = "根据用户名或姓名分页获取用户列表")
    @GetMapping(value = "/list")
    public R<CommonPage<UmsAdmin>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<UmsAdmin> adminList = adminService.list(keyword, pageSize, pageNum);
        return R.ok(CommonPage.restPage(adminList));
    }

    @Operation(summary = "获取指定用户信息")
    @GetMapping(value = "/{id}")
    public R<UmsAdmin> getItem(@PathVariable Long id) {
        UmsAdmin admin = adminService.getItem(id);
        return R.ok(admin);
    }

    @Operation(summary = "修改指定用户信息")
    @PostMapping(value = "/update/{id}")
    public R update(@PathVariable Long id, @RequestBody UmsAdmin admin) {
        int count = adminService.update(id, admin);
        if (count > 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "修改指定用户密码")
    @PostMapping(value = "/updatePassword")
    public R updatePassword(@Validated @RequestBody UpdateAdminPasswordParam updatePasswordParam) {
        int status = adminService.updatePassword(updatePasswordParam);
        if (status > 0) {
            return R.ok(status);
        } else if (status == -1) {
            return R.failed("提交参数不合法");
        } else if (status == -2) {
            return R.failed("找不到该用户");
        } else if (status == -3) {
            return R.failed("旧密码错误");
        } else {
            return R.failed();
        }
    }

    @Operation(summary = "删除指定用户信息")
    @PostMapping(value = "/delete/{id}")
    public R delete(@PathVariable Long id) {
        int count = adminService.delete(id);
        if (count > 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "修改帐号状态")
    @PostMapping(value = "/updateStatus/{id}")
    public R updateStatus(@PathVariable Long id, @RequestParam(value = "status") Boolean status) {
        UmsAdmin umsAdmin = new UmsAdmin();
        umsAdmin.setStatus(status);
        int count = adminService.update(id, umsAdmin);
        if (count > 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "给用户分配角色")
    @PostMapping(value = "/role/update")
    public R updateRole(@RequestParam("adminId") Long adminId,
                        @RequestParam("roleIds") List<Long> roleIds) {
        int count = adminService.updateRole(adminId, roleIds);
        if (count >= 0) {
            return R.ok(count);
        }
        return R.failed();
    }

    @Operation(summary = "获取指定用户的角色")
    @GetMapping(value = "/role/{adminId}")
    public R<List<UmsRole>> getRoleList(@PathVariable Long adminId) {
        List<UmsRole> roleList = adminService.getRoleList(adminId);
        return R.ok(roleList);
    }

}
