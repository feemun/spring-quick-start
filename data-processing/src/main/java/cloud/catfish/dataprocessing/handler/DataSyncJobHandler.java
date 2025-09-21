package cloud.catfish.dataprocessing.handler;

import cloud.catfish.dataprocessing.service.DataProcessingService;
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
import java.util.concurrent.TimeUnit;

/**
 * 数据同步任务处理器
 * 用于数据表之间的同步，支持增量同步
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@Component
public class DataSyncJobHandler extends BaseJobHandler {

    @Autowired
    private DataProcessingService dataProcessingService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String SYNC_TIME_KEY_PREFIX = "data_sync:last_time:";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 数据同步任务
     * 参数格式: sourceTable1:targetTable1,sourceTable2:targetTable2
     * 例如: user_info:user_info_backup,order_info:order_info_sync
     * 
     * @param param 任务参数
     */
    @XxlJob("dataSyncJob")
    public void dataSyncJob(String param) {
        execute(param);
    }

    @Override
    protected String doExecute(String param) throws Exception {
        if (!StringUtils.hasText(param)) {
            throw new IllegalArgumentException("参数不能为空，格式: sourceTable1:targetTable1,sourceTable2:targetTable2");
        }

        // 解析参数
        List<SyncTask> syncTasks = parseSyncTasks(param);
        if (syncTasks.isEmpty()) {
            throw new IllegalArgumentException("没有有效的同步任务");
        }

        int totalSynced = 0;
        StringBuilder result = new StringBuilder();

        for (SyncTask task : syncTasks) {
            try {
                logProgress("开始同步数据: %s -> %s", task.getSourceTable(), task.getTargetTable());
                
                // 获取上次同步时间
                LocalDateTime lastSyncTime = getLastSyncTime(task.getSourceTable(), task.getTargetTable());
                logProgress("上次同步时间: %s", lastSyncTime.format(FORMATTER));
                
                // 获取源表记录数
                long sourceCount = dataProcessingService.getTableCount(task.getSourceTable());
                
                // 执行同步
                int syncedCount = dataProcessingService.syncData(
                    task.getSourceTable(), 
                    task.getTargetTable(), 
                    lastSyncTime
                );
                
                // 更新同步时间
                updateLastSyncTime(task.getSourceTable(), task.getTargetTable(), LocalDateTime.now());
                
                totalSynced += syncedCount;
                
                String taskResult = String.format("同步[%s->%s]: 源表记录数=%d, 同步记录数=%d", 
                    task.getSourceTable(), task.getTargetTable(), sourceCount, syncedCount);
                
                result.append(taskResult).append("; ");
                logProgress(taskResult);
                
            } catch (Exception e) {
                String errorMsg = String.format("同步[%s->%s]失败: %s", 
                    task.getSourceTable(), task.getTargetTable(), e.getMessage());
                log.error(errorMsg, e);
                result.append(errorMsg).append("; ");
                // 继续处理其他同步任务，不中断整个任务
            }
        }

        String finalResult = String.format("数据同步完成，总同步记录数: %d。详情: %s", totalSynced, result.toString());
        return finalResult;
    }

    /**
     * 全量数据同步任务
     * 参数格式: sourceTable1:targetTable1,sourceTable2:targetTable2
     * 
     * @param param 任务参数
     */
    @XxlJob("dataFullSyncJob")
    public void dataFullSyncJob(String param) {
        execute(param);
    }

    /**
     * 获取上次同步时间
     * 
     * @param sourceTable 源表
     * @param targetTable 目标表
     * @return 上次同步时间
     */
    private LocalDateTime getLastSyncTime(String sourceTable, String targetTable) {
        String key = SYNC_TIME_KEY_PREFIX + sourceTable + ":" + targetTable;
        String lastSyncTimeStr = redisTemplate.opsForValue().get(key);
        
        if (StringUtils.hasText(lastSyncTimeStr)) {
            try {
                return LocalDateTime.parse(lastSyncTimeStr, FORMATTER);
            } catch (Exception e) {
                log.warn("解析上次同步时间失败: {}, 使用默认时间", lastSyncTimeStr);
            }
        }
        
        // 默认返回24小时前
        return LocalDateTime.now().minusDays(1);
    }

    /**
     * 更新上次同步时间
     * 
     * @param sourceTable 源表
     * @param targetTable 目标表
     * @param syncTime 同步时间
     */
    private void updateLastSyncTime(String sourceTable, String targetTable, LocalDateTime syncTime) {
        String key = SYNC_TIME_KEY_PREFIX + sourceTable + ":" + targetTable;
        String syncTimeStr = syncTime.format(FORMATTER);
        
        // 设置过期时间为7天
        redisTemplate.opsForValue().set(key, syncTimeStr, 7, TimeUnit.DAYS);
        
        log.debug("更新同步时间: {} = {}", key, syncTimeStr);
    }

    /**
     * 解析同步任务参数
     * 
     * @param param 参数字符串
     * @return 同步任务列表
     */
    private List<SyncTask> parseSyncTasks(String param) {
        return Arrays.stream(param.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .map(this::parseSyncTask)
            .filter(task -> task != null)
            .toList();
    }

    /**
     * 解析单个同步任务
     * 
     * @param taskStr 任务字符串，格式: sourceTable:targetTable
     * @return 同步任务对象
     */
    private SyncTask parseSyncTask(String taskStr) {
        try {
            String[] parts = taskStr.split(":");
            if (parts.length != 2) {
                log.warn("无效的任务格式: {}, 应为 sourceTable:targetTable", taskStr);
                return null;
            }

            String sourceTable = parts[0].trim();
            String targetTable = parts[1].trim();

            if (!StringUtils.hasText(sourceTable) || !StringUtils.hasText(targetTable)) {
                log.warn("源表或目标表名不能为空: {}", taskStr);
                return null;
            }

            return new SyncTask(sourceTable, targetTable);

        } catch (Exception e) {
            log.warn("解析同步任务失败: {}", taskStr, e);
            return null;
        }
    }

    /**
     * 同步任务内部类
     */
    private static class SyncTask {
        private final String sourceTable;
        private final String targetTable;

        public SyncTask(String sourceTable, String targetTable) {
            this.sourceTable = sourceTable;
            this.targetTable = targetTable;
        }

        public String getSourceTable() {
            return sourceTable;
        }

        public String getTargetTable() {
            return targetTable;
        }
    }
}