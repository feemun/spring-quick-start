package cloud.catfish.admin.controller.ums;

import cloud.catfish.admin.service.UmsMemberLevelService;
import cloud.catfish.common.api.R;
import domain.UmsMemberLevel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 会员等级管理Controller
 * Created by macro on 2018/4/26.
 */
@RestController
@Tag(name = "UmsMemberLevelController", description = "会员等级管理")
@RequestMapping("/memberLevel")
public class UmsMemberLevelController {
    @Resource
    private UmsMemberLevelService memberLevelService;

    @Operation(summary = "查询所有会员等级")
    @GetMapping(value = "/list")
    
    public R<List<UmsMemberLevel>> list(@RequestParam("defaultStatus") Integer defaultStatus) {
        List<UmsMemberLevel> memberLevelList = memberLevelService.list(defaultStatus);
        return R.ok(memberLevelList);
    }
}
