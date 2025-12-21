package cloud.catfish.admin.controller;

import cloud.catfish.api.domain.XlcSignalDataStatus;
import cloud.catfish.api.domain.XlcSignalDataStatusExample;
import cloud.catfish.common.api.R;
import cloud.catfish.mbg.mapper.XlcSignalDataStatusMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/signalDataStatus")
@Tag(name = "AdminXlcSignalDataStatusController", description = "信号数据状态管理")
@RequiredArgsConstructor
public class AdminXlcSignalDataStatusController {

    private final XlcSignalDataStatusMapper xlcSignalDataStatusMapper;

    @Operation(summary = "获取所有信号数据状态")
    @GetMapping("/list")
    public R<List<XlcSignalDataStatus>> list() {
        // Mock data to bypass missing table error
        List<XlcSignalDataStatus> list = new java.util.ArrayList<>();
        list.addAll(xlcSignalDataStatusMapper.selectByExample(null));
        return R.ok(list);
    }
}
