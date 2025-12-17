package cloud.catfish.mbg.controller;

import cloud.catfish.api.domain.IpTagRule;
import cloud.catfish.api.req.IpTagRuleReq;
import cloud.catfish.api.vo.IpTagRuleVO;
import cloud.catfish.mbg.service.IpTagRuleService;
import cn.hutool.db.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "IpTagRule API", description = "IpTagRule相关的增删改查操作")
@RestController
@RequestMapping("/IpTagRule")
@Slf4j
public class IpTagRuleController {

    @Resource
    private IpTagRuleService ipTagRuleService;

    @Operation(summary = "分页条件查询", description = "根据条件分页查询IpTagRule列表")
    @GetMapping("/search")
    public ResponseEntity<PageResult<IpTagRuleVO>> search(@Valid @ModelAttribute IpTagRuleReq req) {
        return ResponseEntity.ok(null);
    }

    @Operation(summary = "根据主键查询", description = "根据ID查询单个IpTagRule")
    @GetMapping("/{id}")
    public ResponseEntity<IpTagRuleVO> selectByPrimaryKey(@PathVariable Long id) {
        IpTagRuleVO result = ipTagRuleService.selectByPrimaryKey(id);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "根据主键更新", description = "更新IpTagRule信息")
    @PutMapping("")
    public ResponseEntity<Boolean> updateByPrimaryKeySelective(@Valid @RequestBody IpTagRule record) {
        Boolean success = ipTagRuleService.updateByPrimaryKeySelective(record);
        log.info(success ? "Successfully updated IpTagRule" : "Failed to update IpTagRule");
        return ResponseEntity.ok(success);
    }

    @Operation(summary = "根据条件批量更新", description = "根据指定条件批量更新IpTagRule信息")
    @PutMapping("/batch")
    public ResponseEntity<Integer> batchUpdate(
            @Valid @RequestBody IpTagRule updateData,
            @Valid @ModelAttribute IpTagRuleReq condition) {
        if (condition == null) {
            throw new IllegalArgumentException("批量更新必须指定明确的条件");
        }
        Integer rowsAffected = ipTagRuleService.updateByExampleSelective(updateData, condition);
        log.info("Updated {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "根据主键删除", description = "删除单个IpTagRule")
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteByPrimaryKey(@PathVariable Long id) {
        Boolean success = ipTagRuleService.deleteByPrimaryKey(id);
        log.info(success ? "Successfully deleted IpTagRule with primary key: id"
                        : "Failed to delete IpTagRule with primary key: id");
        return ResponseEntity.ok(success);
    }

    @Operation(summary = "根据条件批量删除", description = "根据条件批量删除IpTagRule")
    @DeleteMapping("/batch")
    public ResponseEntity<Integer> batchDelete(@Valid @ModelAttribute IpTagRuleReq condition) {
        Integer rowsAffected = ipTagRuleService.deleteByExample(condition);
        log.info("Deleted {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "创建新记录", description = "创建新的IpTagRule")
    @PostMapping
    public ResponseEntity<IpTagRule> create(@Valid @RequestBody IpTagRule record) {
        ipTagRuleService.insertSelective(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @Operation(summary = "Excel批量导入", description = "通过Excel文件批量导入IpTagRule数据")
    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file)  throws Exception {
        // 委托给服务层处理所有业务逻辑
        String result = ipTagRuleService.importExcel(file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "导出Excel", description = "导出IpTagRule数据到Excel文件")
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response, @RequestParam(required = false) IpTagRule condition) throws Exception {
        // 设置响应头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = new String("IpTagRule数据.xlsx".getBytes("UTF-8"), "ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        
        // 这里应该添加数据查询和Excel导出的逻辑
        // 如果condition不为空，则按条件查询
        // 示例：List<IpTagRule> records = condition != null ?
        //     ipTagRuleService.selectByExample(condition) : ipTagRuleService.selectAll();
        // excelService.exportExcel(response, records, "IpTagRule数据");
        
        // 导出逻辑实现...
    }

}
