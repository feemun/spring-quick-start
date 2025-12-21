package cloud.catfish.admin.controller;

import cloud.catfish.api.domain.XlcDroneStatus;
import cloud.catfish.api.domain.XlcDroneStatusExample;
import cloud.catfish.api.domain.XlcSignalDataStatus;
import cloud.catfish.api.domain.XlcSignalDataStatusExample;
import cloud.catfish.api.dto.XlcMergedStatusDto;
import cloud.catfish.common.api.R;
import cloud.catfish.mbg.mapper.XlcDroneStatusMapper;
import cloud.catfish.mbg.mapper.XlcSignalDataStatusMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/mergedStatus")
@Tag(name = "XlcMergedStatusController", description = "无人机与信号融合状态管理")
@RequiredArgsConstructor
public class XlcMergedStatusController {

    private final XlcDroneStatusMapper xlcDroneStatusMapper;
    private final XlcSignalDataStatusMapper xlcSignalDataStatusMapper;

    @Operation(summary = "获取融合状态列表")
    @GetMapping("/list")
    public R<List<XlcMergedStatusDto>> list() {
        List<XlcDroneStatus> droneStatusList = xlcDroneStatusMapper.selectByExample(new XlcDroneStatusExample());
        List<XlcSignalDataStatus> signalStatusList = xlcSignalDataStatusMapper.selectByExample(new XlcSignalDataStatusExample());

        Map<Long, XlcMergedStatusDto> mergedMap = new HashMap<>();

        // Process Signal Status first (to be overridden by Drone Status if fields conflict)
        for (XlcSignalDataStatus signal : signalStatusList) {
            if (signal.getUavPort() == null) continue;
            
            XlcMergedStatusDto dto = mergedMap.computeIfAbsent(signal.getUavPort(), k -> new XlcMergedStatusDto());
            
            // Map Signal fields
            dto.setUavPort(signal.getUavPort());
            dto.setAzimuth(signal.getAzimuth());
            dto.setFrequency(signal.getFrequency());
            dto.setPitch(signal.getPitch());
            dto.setPower(signal.getPower());
            dto.setTime(signal.getTime());
            
            // Map common fields (can be overridden later)
            dto.setLatitude(signal.getLatitude());
            dto.setLongitude(signal.getLongitude());
            dto.setUpdatedAt(signal.getUpdatedAt());
        }

        // Process Drone Status (overrides common fields)
        for (XlcDroneStatus drone : droneStatusList) {
            if (drone.getUavPort() == null) continue;

            XlcMergedStatusDto dto = mergedMap.computeIfAbsent(drone.getUavPort(), k -> new XlcMergedStatusDto());
            
            // Map Drone fields (will override latitude/longitude/updatedAt if already set)
            BeanUtils.copyProperties(drone, dto);
            
            // Explicitly set uavPort to ensure it's correct (though copyProperties should handle it)
            dto.setUavPort(drone.getUavPort());
        }

        List<XlcMergedStatusDto> result = new ArrayList<>(mergedMap.values());
        return R.ok(result);
    }
}
