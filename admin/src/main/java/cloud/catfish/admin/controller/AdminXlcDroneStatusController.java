package cloud.catfish.admin.controller;

import cloud.catfish.api.domain.XlcDroneStatus;
import cloud.catfish.api.domain.XlcDroneStatusExample;
import cloud.catfish.common.api.R;
import cloud.catfish.mbg.mapper.XlcDroneStatusMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/droneStatus")
@Tag(name = "AdminXlcDroneStatusController", description = "无人机状态管理")
@RequiredArgsConstructor
public class AdminXlcDroneStatusController {

    private final XlcDroneStatusMapper xlcDroneStatusMapper;

    @Operation(summary = "获取所有无人机状态")
    @GetMapping("/list")
    public R<List<XlcDroneStatus>> list() {
        System.out.println("Entering AdminXlcDroneStatusController.list()");
        // Mock data to bypass missing table error
        List<XlcDroneStatus> list = new java.util.ArrayList<>();
        list.addAll(xlcDroneStatusMapper.selectByExample(null));
        System.out.println("Returning mock data (database table missing), size: " + list.size());
        return R.ok(list);
    }
}
