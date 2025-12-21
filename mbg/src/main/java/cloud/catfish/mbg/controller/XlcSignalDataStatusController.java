package cloud.catfish.mbg.controller;

import cloud.catfish.api.domain.XlcSignalDataStatus;
import cloud.catfish.api.req.XlcSignalDataStatusReq;
import cloud.catfish.api.vo.XlcSignalDataStatusVO;
import cloud.catfish.mbg.service.XlcSignalDataStatusService;
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

@Tag(name = "XlcSignalDataStatus API", description = "XlcSignalDataStatus相关的增删改查操作")
@RestController
@RequestMapping("/XlcSignalDataStatus")
@Slf4j
public class XlcSignalDataStatusController {

    @Resource
    private XlcSignalDataStatusService xlcSignalDataStatusService;

    @Operation(summary = "分页条件查询", description = "根据条件分页查询XlcSignalDataStatus列表")
    @GetMapping("/search")
    public ResponseEntity<Page<XlcSignalDataStatusVO>> search(@Valid @ModelAttribute XlcSignalDataStatusReq req) {
        Page<XlcSignalDataStatusVO> page = xlcSignalDataStatusService.page(req);
        return ResponseEntity.ok(page);
    }

    @Operation(summary = "根据主键查询", description = "根据ID查询单个XlcSignalDataStatus")
    @GetMapping("/{uavPort}")
    public ResponseEntity<XlcSignalDataStatusVO> selectByPrimaryKey(@PathVariable Long uavPort) {
        XlcSignalDataStatusVO result = xlcSignalDataStatusService.selectByPrimaryKey(uavPort);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "根据主键更新", description = "更新XlcSignalDataStatus信息")
    @PutMapping("")
    public ResponseEntity<Boolean> updateByPrimaryKeySelective(@Valid @RequestBody XlcSignalDataStatus record) {
        Boolean success = xlcSignalDataStatusService.updateByPrimaryKeySelective(record);
        log.info(success ? "Successfully updated XlcSignalDataStatus" : "Failed to update XlcSignalDataStatus");
        return ResponseEntity.ok(success);
    }

    @Operation(summary = "根据条件批量更新", description = "根据指定条件批量更新XlcSignalDataStatus信息")
    @PutMapping("/batch")
    public ResponseEntity<Integer> batchUpdate(
            @Valid @RequestBody XlcSignalDataStatus updateData,
            @Valid @ModelAttribute XlcSignalDataStatusReq condition) {
        if (condition == null) {
            throw new IllegalArgumentException("批量更新必须指定明确的条件");
        }
        Integer rowsAffected = xlcSignalDataStatusService.updateByExampleSelective(updateData, condition);
        log.info("Updated {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "根据主键删除", description = "删除单个XlcSignalDataStatus")
    @DeleteMapping("/{uavPort}")
    public ResponseEntity<Boolean> deleteByPrimaryKey(@PathVariable Long uavPort) {
        Boolean success = xlcSignalDataStatusService.deleteByPrimaryKey(uavPort);
        log.info(success ? "Successfully deleted XlcSignalDataStatus with primary key: uavPort"
                        : "Failed to delete XlcSignalDataStatus with primary key: uavPort");
        return ResponseEntity.ok(success);
    }

    @Operation(summary = "根据条件批量删除", description = "根据条件批量删除XlcSignalDataStatus")
    @DeleteMapping("/batch")
    public ResponseEntity<Integer> batchDelete(@Valid @ModelAttribute XlcSignalDataStatusReq condition) {
        Integer rowsAffected = xlcSignalDataStatusService.deleteByExample(condition);
        log.info("Deleted {} row(s) by condition.", rowsAffected);
        return ResponseEntity.ok(rowsAffected);
    }

    @Operation(summary = "创建新记录", description = "创建新的XlcSignalDataStatus")
    @PostMapping
    public ResponseEntity<XlcSignalDataStatus> create(@Valid @RequestBody XlcSignalDataStatus record) {
        
        xlcSignalDataStatusService.insertSelective(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @Operation(summary = "Excel批量导入", description = "通过Excel文件批量导入XlcSignalDataStatus数据")
    @PostMapping("/import")
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file)  throws Exception {
        // 委托给服务层处理所有业务逻辑
        String result = xlcSignalDataStatusService.importExcel(file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "导出Excel", description = "导出XlcSignalDataStatus数据到Excel文件")
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response, @RequestParam(required = false) XlcSignalDataStatus condition) throws Exception {
        // 设置响应头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String filename = new String("XlcSignalDataStatus数据.xlsx".getBytes("UTF-8"), "ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment;filename=" + filename);
        
        // 这里应该添加数据查询和Excel导出的逻辑
        // 如果condition不为空，则按条件查询
        // 示例：List<XlcSignalDataStatus> records = condition != null ?
        //     xlcSignalDataStatusService.selectByExample(condition) : xlcSignalDataStatusService.selectAll();
        // excelService.exportExcel(response, records, "XlcSignalDataStatus数据");
        
        // 导出逻辑实现...
    }

}
