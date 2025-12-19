package cloud.catfish.data.controller;

import cloud.catfish.common.api.R;
import cloud.catfish.elasticsearch9.dto.NetworkLogMergedStatDto;
import cloud.catfish.elasticsearch9.dto.NetworkLogStatDto;
import cloud.catfish.elasticsearch9.service.NetworkLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/network-log")
@RequiredArgsConstructor
public class NetworkLogController {

    private final NetworkLogService networkLogService;

    @GetMapping("/stats")
    public R<List<NetworkLogMergedStatDto>> getStatistics() {
        try {
            List<NetworkLogStatDto> srcStats = networkLogService.getStatistics("srcIp");
            List<NetworkLogStatDto> destStats = networkLogService.getStatistics("destIp");

            Map<String, NetworkLogMergedStatDto> mergedMap = new HashMap<>();

            // Process srcStats (Upload)
            for (NetworkLogStatDto stat : srcStats) {
                NetworkLogMergedStatDto merged = mergedMap.computeIfAbsent(stat.getKey(), k -> NetworkLogMergedStatDto.builder()
                        .key(k)
                        .uploadCount(0L)
                        .uploadBytes(0.0)
                        .downloadCount(0L)
                        .downloadBytes(0.0)
                        .totalCount(0L)
                        .totalBytes(0.0)
                        .firstCreated(stat.getFirstCreated())
                        .lastCreated(stat.getLastCreated())
                        .hasTrafficToday(false)
                        .build());
                merged.setUploadCount(stat.getCount());
                merged.setUploadBytes(stat.getTotalBytes());
                merged.setTotalCount(merged.getTotalCount() + stat.getCount());
                merged.setTotalBytes(merged.getTotalBytes() + stat.getTotalBytes());
                
                // Update time if needed (though absent usually means first seen here)
                if (stat.getFirstCreated().isBefore(merged.getFirstCreated())) {
                    merged.setFirstCreated(stat.getFirstCreated());
                }
                if (stat.getLastCreated().isAfter(merged.getLastCreated())) {
                    merged.setLastCreated(stat.getLastCreated());
                }
                merged.setHasTrafficToday(merged.getTotalCount() > 0);
            }

            // Process destStats (Download)
            for (NetworkLogStatDto stat : destStats) {
                NetworkLogMergedStatDto merged = mergedMap.computeIfAbsent(stat.getKey(), k -> NetworkLogMergedStatDto.builder()
                        .key(k)
                        .uploadCount(0L)
                        .uploadBytes(0.0)
                        .downloadCount(0L)
                        .downloadBytes(0.0)
                        .totalCount(0L)
                        .totalBytes(0.0)
                        .firstCreated(stat.getFirstCreated())
                        .lastCreated(stat.getLastCreated())
                        .hasTrafficToday(false)
                        .build());
                merged.setDownloadCount(stat.getCount());
                merged.setDownloadBytes(stat.getTotalBytes());
                merged.setTotalCount(merged.getTotalCount() + stat.getCount());
                merged.setTotalBytes(merged.getTotalBytes() + stat.getTotalBytes());
                
                // Update time
                if (stat.getFirstCreated().isBefore(merged.getFirstCreated())) {
                    merged.setFirstCreated(stat.getFirstCreated());
                }
                if (stat.getLastCreated().isAfter(merged.getLastCreated())) {
                    merged.setLastCreated(stat.getLastCreated());
                }
                merged.setHasTrafficToday(merged.getTotalCount() > 0);
            }

            return R.ok(new ArrayList<>(mergedMap.values()));
        } catch (Exception e) {
            log.error("Failed to get network log statistics", e);
            return R.failed("Failed to get statistics: " + e.getMessage());
        }
    }

}
