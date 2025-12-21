package cloud.catfish.mbg.controller;

import cloud.catfish.api.domain.XlcDroneStatus;
import cloud.catfish.api.req.XlcDroneStatusReq;
import cloud.catfish.api.vo.XlcDroneStatusVO;
import cloud.catfish.mbg.service.XlcDroneStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "XlcDroneStatus API", description = "XlcDroneStatus相关的增删改查操作")
@RestController
@RequestMapping("/XlcDroneStatus")
@Slf4j
public class XlcDroneStatusController {

    @Resource
    private XlcDroneStatusService xlcDroneStatusService;

    @Operation(summary = "分页条件查询", description = "根据条件分页查询XlcDroneStatus列表")
    @GetMapping("/search")
    public ResponseEntity<Page<XlcDroneStatusVO>> search(@Valid @ModelAttribute XlcDroneStatusReq req) {
        Page<XlcDroneStatusVO> page = xlcDroneStatusService.page(req);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "根据主键查询", description = "根据ID查询单个XlcDroneStatus")
    @GetMapping("/{uavPort}")
    public ResponseEntity<XlcDroneStatusVO> selectByPrimaryKey(@PathVariable Long uavPort) {
        XlcDroneStatusVO result = xlcDroneStatusService.selectByPrimaryKey(uavPort);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "根据主键更新", description = "更新XlcDroneStatus信息")
    @PutMapping("")
    public ResponseEntity<Boolean> updateByPrimaryKeySelective(@Valid @RequestBody XlcDroneStatus record) {
        Boolean success = xlcDroneStatusService.updateByPrimaryKeySelective(record);
        log.info(success ? "Successfully updated XlcDroneStatus" : "Failed to update XlcDroneStatus");
        return ResponseEntity.ok(success);
    }

    @Operation(summary = "根据条件批量更新", description = "根据指定条件批量更新XlcDroneStatus信息")
    @PutMapping("/batch")
    public ResponseEntity<Integer> batchUpdate(
            @Valid @RequestBody XlcDroneStatus updateData,
            @Valid @ModelAttribute XlcDroneStatusReq condition) {
        if (condition == null) {
            throw new IllegalArgumentException("批量更新必须指定明确的条件");
        }
        Integer rowsAffected = xlcDroneStatusService.updateByExampleSelective(updateData, condition);
        log.info("Updated {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "根据主键删除", description = "删除单个XlcDroneStatus")
    @DeleteMapping("/{uavPort}")
    public ResponseEntity<Boolean> deleteByPrimaryKey(@PathVariable Long uavPort) {
        Boolean success = xlcDroneStatusService.deleteByPrimaryKey(uavPort);
        log.info(success ? "Successfully deleted XlcDroneStatus with primary key: uavPort"
                        : "Failed to delete XlcDroneStatus with primary key: uavPort");
        return ResponseEntity.ok(success);
    }

    @Operation(summary = "根据条件批量删除", description = "根据条件批量删除XlcDroneStatus")
    @DeleteMapping("/batch")
    public ResponseEntity<Integer> batchDelete(@Valid @ModelAttribute XlcDroneStatusReq condition) {
        Integer rowsAffected = xlcDroneStatusService.deleteByExample(condition);
        log.info("Deleted {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "创建新记录", description = "创建新的XlcDroneStatus")
    @PostMapping
    public ResponseEntity<XlcDroneStatus> create(@Valid @RequestBody XlcDroneStatus record) {
        xlcDroneStatusService.insertSelective(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @Operation(summary = "Excel批量导入", description = "通过Excel文件批量导入XlcDroneStatus数据")
    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file)  throws Exception {
        // 委托给服务层处理所有业务逻辑
        String result = xlcDroneStatusService.importExcel(file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "导出Excel", description = "导出XlcDroneStatus数据到Excel文件")
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response, @RequestParam(required = false) XlcDroneStatus condition) throws Exception {
        // 设置响应头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = new String("XlcDroneStatus数据.xlsx".getBytes("UTF-8"), "ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        
        // 这里应该添加数据查询和Excel导出的逻辑
        // 如果condition不为空，则按条件查询
        // 示例：List<XlcDroneStatus> records = condition != null ?
        //     xlcDroneStatusService.selectByExample(condition) : xlcDroneStatusService.selectAll();
        // excelService.exportExcel(response, records, "XlcDroneStatus数据");
        
        // 导出逻辑实现...
    }

}
