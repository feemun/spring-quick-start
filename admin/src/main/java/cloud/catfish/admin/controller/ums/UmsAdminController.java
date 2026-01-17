package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsAdminService;
import cloud.catfish.admin.service.UmsRoleService;
import cloud.catfish.admin.dto.TokenPair;
import cloud.catfish.api.common.CommonPage;
import cloud.catfish.api.vo.AdminLoginVO;
import cloud.catfish.api.req.RefreshTokenParam;
import cn.hutool.core.collection.CollUtil;
import cloud.catfish.api.domain.UmsAdmin;
import cloud.catfish.api.domain.UmsRole;
import cloud.catfish.api.req.EnabledParam;
import cloud.catfish.api.req.IdListParam;
import cloud.catfish.api.req.UmsAdminLoginParam;
import cloud.catfish.api.req.UmsAdminCreateParam;
import cloud.catfish.api.req.UmsAdminUpdateParam;
import cloud.catfish.api.dto.UpdateAdminPasswordParam;
import cloud.catfish.security.config.SecurityProperties.JwtProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Tag(name = "UmsAdminController", description = "后台用户管理")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UmsAdminController {
    private final JwtProperties jwtProperties;
    private final UmsAdminService adminService;
    private final UmsRoleService roleService;

    @Operation(summary = "用户注册")
    @PostMapping
    public ResponseEntity<UmsAdmin> register(@Validated @RequestBody UmsAdminCreateParam param) {
        UmsAdmin umsAdmin = adminService.register(param);
        if (umsAdmin == null) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.status(201).body(umsAdmin);
    }

    @Operation(summary = "登录以后返回token")
    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@Validated @RequestBody UmsAdminLoginParam umsAdminLoginParam) {
        TokenPair tokenPair = adminService.login(umsAdminLoginParam.getUsername(), umsAdminLoginParam.getPassword());
        if (tokenPair == null) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pd);
        }
        AdminLoginVO adminLoginVO = new AdminLoginVO();
        adminLoginVO.setToken(tokenPair.accessToken());
        adminLoginVO.setTokenHead(jwtProperties.tokenHead());
        adminLoginVO.setRefreshToken(tokenPair.refreshToken());
        return ResponseEntity.ok(adminLoginVO);
    }

    @Operation(summary = "刷新token")
    @PostMapping(value = "/refreshToken")
    public ResponseEntity<?> refreshToken(@Validated @RequestBody RefreshTokenParam param) {
        TokenPair tokenPair = adminService.refreshToken(param.getRefreshToken());
        if (tokenPair == null) {
            ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "refreshToken无效或已过期！");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(pd);
        }
        AdminLoginVO adminLoginVO = new AdminLoginVO();
        adminLoginVO.setToken(tokenPair.accessToken());
        adminLoginVO.setTokenHead(jwtProperties.tokenHead());
        adminLoginVO.setRefreshToken(tokenPair.refreshToken());
        return ResponseEntity.ok(adminLoginVO);
    }

    @Operation(summary = "获取当前登录用户信息")
    @GetMapping(value = "/info")
    public ResponseEntity<?> getAdminInfo(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String username = principal.getName();
        UmsAdmin umsAdmin = adminService.getAdminByUsername(username);
        if (umsAdmin == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Map<String, Object> data = new HashMap<>();
        data.put("username", umsAdmin.getUsername());
        data.put("menus", roleService.getMenuList(umsAdmin.getId()));
        data.put("icon", umsAdmin.getIcon());
        List<UmsRole> roleList = adminService.getRoleList(umsAdmin.getId());
        if (CollUtil.isNotEmpty(roleList)) {
            List<String> roles = roleList.stream().map(UmsRole::getName).collect(Collectors.toList());
            data.put("roles", roles);
        }
        return ResponseEntity.ok(data);
    }

    @Operation(summary = "登出功能")
    @PostMapping(value = "/logout")
    public ResponseEntity<Void> logout(Principal principal) {
        if (principal == null) {
            return ResponseEntity.noContent().build();
        }
        adminService.logout(principal.getName());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "根据用户名或姓名分页获取用户列表")
    @GetMapping
    public ResponseEntity<CommonPage<UmsAdmin>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                                        @RequestParam(value = "page", defaultValue = "1") Integer pageNum) {
        List<UmsAdmin> adminList = adminService.list(keyword, pageSize, pageNum);
        return ResponseEntity.ok(CommonPage.restPage(adminList));
    }

    @Operation(summary = "获取指定用户信息")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UmsAdmin> getItem(@PathVariable Long id) {
        UmsAdmin admin = adminService.getItem(id);
        if (admin == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(admin);
    }

    @Operation(summary = "修改指定用户信息")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Validated @RequestBody UmsAdminUpdateParam param) {
        UmsAdmin admin = new UmsAdmin();
        admin.setIcon(param.getIcon());
        admin.setEmail(param.getEmail());
        admin.setNickName(param.getNickName());
        admin.setNote(param.getNote());
        int count = adminService.update(id, admin);
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "修改指定用户密码")
    @PostMapping(value = "/updatePassword")
    public ResponseEntity<?> updatePassword(@Validated @RequestBody UpdateAdminPasswordParam updatePasswordParam) {
        int status = adminService.updatePassword(updatePasswordParam);
        if (status > 0) {
            return ResponseEntity.noContent().build();
        } else if (status == -1) {
            return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "提交参数不合法"));
        } else if (status == -2) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, "找不到该用户"));
        } else if (status == -3) {
            return ResponseEntity.badRequest().body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "旧密码错误"));
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "删除指定用户信息")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        int count = adminService.delete(id);
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "修改帐号状态")
    @PatchMapping(value = "/{id}/enabled")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody EnabledParam param) {
        UmsAdmin umsAdmin = new UmsAdmin();
        umsAdmin.setStatus(Boolean.TRUE.equals(param.getEnabled()));
        int count = adminService.update(id, umsAdmin);
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "给用户分配角色")
    @PutMapping(value = "/{adminId}/roles")
    public ResponseEntity<Void> updateRole(@PathVariable("adminId") Long adminId, @Valid @RequestBody IdListParam param) {
        int count = adminService.updateRole(adminId, param.getIds());
        if (count < 0) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "获取指定用户的角色")
    @GetMapping(value = "/{adminId}/roles")
    public ResponseEntity<List<UmsRole>> getRoleList(@PathVariable Long adminId) {
        List<UmsRole> roleList = adminService.getRoleList(adminId);
        return ResponseEntity.ok(roleList);
    }

}
