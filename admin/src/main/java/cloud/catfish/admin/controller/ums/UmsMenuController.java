package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsMenuService;
import cloud.catfish.api.common.CommonPage;
import cloud.catfish.api.domain.UmsMenu;
import cloud.catfish.api.dto.UmsMenuNode;
import cloud.catfish.api.req.HiddenParam;
import cloud.catfish.api.req.UmsMenuCreateParam;
import cloud.catfish.api.req.UmsMenuUpdateParam;
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
@Tag(name = "UmsMenuController", description = "后台菜单管理")
@RequestMapping("/menu")
@RequiredArgsConstructor
@Validated
public class UmsMenuController {

    private final UmsMenuService menuService;

    @Operation(summary = "添加后台菜单")
    @PostMapping
    public ResponseEntity<UmsMenu> create(@Valid @RequestBody UmsMenuCreateParam param) {
        UmsMenu menu = new UmsMenu();
        menu.setParentId(param.getParentId());
        menu.setTitle(param.getTitle());
        menu.setName(param.getName());
        menu.setIcon(param.getIcon());
        menu.setHidden(Boolean.TRUE.equals(param.getHidden()) ? 1 : 0);
        menu.setSort(param.getSort());
        int count = menuService.create(menu);
        if (count <= 0) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.status(201).body(menu);
    }

    @Operation(summary = "修改后台菜单")
    @PutMapping(value = "/{id}")
    public ResponseEntity<Void> update(@PathVariable @NotNull Long id, @Valid @RequestBody UmsMenuUpdateParam param) {
        UmsMenu menu = new UmsMenu();
        menu.setTitle(param.getTitle());
        menu.setName(param.getName());
        menu.setIcon(param.getIcon());
        if (param.getHidden() != null) {
            menu.setHidden(Boolean.TRUE.equals(param.getHidden()) ? 1 : 0);
        }
        menu.setSort(param.getSort());
        int count = menuService.update(id, menu);
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "根据ID获取菜单详情")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UmsMenu> getItem(@PathVariable @NotNull Long id) {
        UmsMenu umsMenu = menuService.getItem(id);
        if (umsMenu == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(umsMenu);
    }

    @Operation(summary = "根据ID删除后台菜单")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable @NotNull Long id) {
        int count = menuService.delete(id);
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "分页查询后台菜单")
    @GetMapping
    public ResponseEntity<CommonPage<UmsMenu>> list(@RequestParam("parentId") @NotNull Long parentId,
                                       @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer pageSize,
                                       @RequestParam(value = "page", defaultValue = "1") @Min(1) Integer pageNum) {
        List<UmsMenu> menuList = menuService.list(parentId, pageSize, pageNum);
        return ResponseEntity.ok(CommonPage.restPage(menuList));
    }

    @Operation(summary = "树形结构返回所有菜单列表")
    @GetMapping(value = "/tree")
    public ResponseEntity<List<UmsMenuNode>> treeList() {
        List<UmsMenuNode> list = menuService.treeList();
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "修改菜单显示状态")
    @PatchMapping(value = "/{id}/hidden")
    public ResponseEntity<Void> updateHidden(@PathVariable @NotNull Long id, @Valid @RequestBody HiddenParam param) {
        int hidden = Boolean.TRUE.equals(param.getHidden()) ? 1 : 0;
        int count = menuService.updateHidden(id, hidden);
        if (count <= 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
