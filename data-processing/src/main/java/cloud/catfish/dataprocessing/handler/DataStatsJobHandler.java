package cloud.catfish.dataprocessing.handler;

import cloud.catfish.dataprocessing.service.DataProcessingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 数据统计任务处理器
 * 用于生成各种数据统计报表
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@Component
public class DataStatsJobHandler extends BaseJobHandler {

    @Autowired
    private DataProcessingService dataProcessingService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String STATS_KEY_PREFIX = "data_stats:";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 数据统计任务
     * 参数格式: tableName1:hours1,tableName2:hours2
     * 例如: user_log:24,order_info:168,system_log:72
     * 
     * @param param 任务参数
     */
    @XxlJob("dataStatsJob")
    public void dataStatsJob(String param) {
        execute(param);
    }

    /**
     * 日报统计任务
     * 参数格式: tableName1,tableName2,tableName3
     * 
     * @param param 任务参数
     */
    @XxlJob("dailyStatsJob")
    public void dailyStatsJob(String param) {
        execute(param + ":24");
    }

    /**
     * 周报统计任务
     * 参数格式: tableName1,tableName2,tableName3
     * 
     * @param param 任务参数
     */
    @XxlJob("weeklyStatsJob")
    public void weeklyStatsJob(String param) {
        execute(param + ":168");
    }

    /**
     * 月报统计任务
     * 参数格式: tableName1,tableName2,tableName3
     * 
     * @param param 任务参数
     */
    @XxlJob("monthlyStatsJob")
    public void monthlyStatsJob(String param) {
        execute(param + ":720");
    }

    @Override
    protected String doExecute(String param) throws Exception {
        if (!StringUtils.hasText(param)) {
            throw new IllegalArgumentException("参数不能为空，格式: tableName1:hours1,tableName2:hours2");
        }

        // 解析参数
        List<StatsTask> statsTasks = parseStatsTasks(param);
        if (statsTasks.isEmpty()) {
            throw new IllegalArgumentException("没有有效的统计任务");
        }

        int totalStats = 0;
        StringBuilder result = new StringBuilder();

        for (StatsTask task : statsTasks) {
            try {
                logProgress("开始统计表: %s, 统计时间范围: %d小时", task.getTableName(), task.getHours());
                
                // 计算时间范围
                LocalDateTime endTime = LocalDateTime.now();
                LocalDateTime startTime = endTime.minusHours(task.getHours());
                
                // 生成统计数据
                Map<String, Object> stats = dataProcessingService.generateStats(
                    task.getTableName(), startTime, endTime
                );
                
                // 保存统计结果到Redis
                String statsKey = generateStatsKey(task.getTableName(), task.getHours());
                String statsJson = objectMapper.writeValueAsString(stats);
                redisTemplate.opsForValue().set(statsKey, statsJson, 7, TimeUnit.DAYS);
                
                totalStats++;
                
                Long totalCount = (Long) stats.get("totalCount");
                String taskResult = String.format("统计[%s]: 时间范围=%s~%s, 记录数=%d", 
                    task.getTableName(), 
                    startTime.format(FORMATTER), 
                    endTime.format(FORMATTER), 
                    totalCount != null ? totalCount : 0);
                
                result.append(taskResult).append("; ");
                logProgress(taskResult);
                
                // 记录详细统计信息
                logDetailedStats(task.getTableName(), stats);
                
            } catch (Exception e) {
                String errorMsg = String.format("统计表[%s]失败: %s", task.getTableName(), e.getMessage());
                log.error(errorMsg, e);
                result.append(errorMsg).append("; ");
                // 继续处理其他统计任务，不中断整个任务
            }
        }

        String finalResult = String.format("数据统计完成，总统计表数: %d。详情: %s", totalStats, result.toString());
        return finalResult;
    }

    /**
     * 生成统计数据的Redis键
     * 
     * @param tableName 表名
     * @param hours 小时数
     * @return Redis键
     */
    private String generateStatsKey(String tableName, int hours) {
        String timeRange;
        if (hours == 24) {
            timeRange = "daily";
        } else if (hours == 168) {
            timeRange = "weekly";
        } else if (hours == 720) {
            timeRange = "monthly";
        } else {
            timeRange = hours + "h";
        }
        
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return STATS_KEY_PREFIX + tableName + ":" + timeRange + ":" + date;
    }

    /**
     * 记录详细统计信息
     * 
     * @param tableName 表名
     * @param stats 统计数据
     */
    @SuppressWarnings("unchecked")
    private void logDetailedStats(String tableName, Map<String, Object> stats) {
        try {
            Long totalCount = (Long) stats.get("totalCount");
            List<Map<String, Object>> dailyStats = (List<Map<String, Object>>) stats.get("dailyStats");
            List<Map<String, Object>> hourlyStats = (List<Map<String, Object>>) stats.get("hourlyStats");
            
            logProgress("表[%s]统计详情: 总记录数=%d", tableName, totalCount != null ? totalCount : 0);
            
            if (dailyStats != null && !dailyStats.isEmpty()) {
                logProgress("按日统计: 共%d天有数据", dailyStats.size());
                for (Map<String, Object> dayStat : dailyStats) {
                    logProgress("  %s: %s条", dayStat.get("date"), dayStat.get("count"));
                }
            }
            
            if (hourlyStats != null && !hourlyStats.isEmpty()) {
                logProgress("按小时统计: 共%d个小时有数据", hourlyStats.size());
                for (Map<String, Object> hourStat : hourlyStats) {
                    logProgress("  %s时: %s条", hourStat.get("hour"), hourStat.get("count"));
                }
            }
            
        } catch (Exception e) {
            log.warn("记录详细统计信息失败: {}", tableName, e);
        }
    }

    /**
     * 解析统计任务参数
     * 
     * @param param 参数字符串
     * @return 统计任务列表
     */
    private List<StatsTask> parseStatsTasks(String param) {
        return Arrays.stream(param.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .map(this::parseStatsTask)
            .filter(task -> task != null)
            .toList();
    }

    /**
     * 解析单个统计任务
     * 
     * @param taskStr 任务字符串，格式: tableName:hours 或 tableName
     * @return 统计任务对象
     */
    private StatsTask parseStatsTask(String taskStr) {
        try {
            String[] parts = taskStr.split(":");
            String tableName;
            int hours;
            
            if (parts.length == 1) {
                // 只有表名，使用默认24小时
                tableName = parts[0].trim();
                hours = 24;
            } else if (parts.length == 2) {
                // 表名和小时数
                tableName = parts[0].trim();
                hours = Integer.parseInt(parts[1].trim());
            } else {
                log.warn("无效的任务格式: {}, 应为 tableName:hours 或 tableName", taskStr);
                return null;
            }

            if (!StringUtils.hasText(tableName)) {
                log.warn("表名不能为空: {}", taskStr);
                return null;
            }

            if (hours <= 0) {
                log.warn("统计小时数必须大于0: {}", taskStr);
                return null;
            }

            return new StatsTask(tableName, hours);

        } catch (NumberFormatException e) {
            log.warn("无效的小时数: {}", taskStr);
            return null;
        }
    }

    /**
     * 统计任务内部类
     */
    private static class StatsTask {
        private final String tableName;
        private final int hours;

        public StatsTask(String tableName, int hours) {
            this.tableName = tableName;
            this.hours = hours;
        }

        public String getTableName() {
            return tableName;
        }

        public int getHours() {
            return hours;
        }
    }
}