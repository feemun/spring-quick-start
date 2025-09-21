package cloud.catfish.dataprocessing.handler;

import cloud.catfish.dataprocessing.service.DataProcessingService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 数据清理任务处理器
 * 用于清理过期数据，支持多表清理
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@Component
public class DataCleanJobHandler extends BaseJobHandler {

    @Autowired
    private DataProcessingService dataProcessingService;

    /**
     * 数据清理任务
     * 参数格式: tableName1:retentionDays1,tableName2:retentionDays2
     * 例如: user_log:30,system_log:7,audit_log:90
     * 
     * @param param 任务参数
     */
    @XxlJob("dataCleanJob")
    public void dataCleanJob(String param) {
        execute(param);
    }

    @Override
    protected String doExecute(String param) throws Exception {
        if (!StringUtils.hasText(param)) {
            throw new IllegalArgumentException("参数不能为空，格式: tableName1:retentionDays1,tableName2:retentionDays2");
        }

        // 解析参数
        List<CleanTask> cleanTasks = parseCleanTasks(param);
        if (cleanTasks.isEmpty()) {
            throw new IllegalArgumentException("没有有效的清理任务");
        }

        int totalCleaned = 0;
        StringBuilder result = new StringBuilder();

        for (CleanTask task : cleanTasks) {
            try {
                logProgress("开始清理表: %s, 保留天数: %d", task.getTableName(), task.getRetentionDays());
                
                // 获取清理前的记录数
                long beforeCount = dataProcessingService.getTableCount(task.getTableName());
                
                // 执行清理
                int cleanedCount = dataProcessingService.cleanExpiredData(task.getTableName(), task.getRetentionDays());
                
                // 获取清理后的记录数
                long afterCount = dataProcessingService.getTableCount(task.getTableName());
                
                totalCleaned += cleanedCount;
                
                String taskResult = String.format("表[%s]: 清理前=%d, 清理后=%d, 删除=%d", 
                    task.getTableName(), beforeCount, afterCount, cleanedCount);
                
                result.append(taskResult).append("; ");
                logProgress(taskResult);
                
            } catch (Exception e) {
                String errorMsg = String.format("清理表[%s]失败: %s", task.getTableName(), e.getMessage());
                log.error(errorMsg, e);
                result.append(errorMsg).append("; ");
                // 继续处理其他表，不中断整个任务
            }
        }

        String finalResult = String.format("数据清理完成，总删除记录数: %d。详情: %s", totalCleaned, result.toString());
        return finalResult;
    }

    /**
     * 解析清理任务参数
     * 
     * @param param 参数字符串
     * @return 清理任务列表
     */
    private List<CleanTask> parseCleanTasks(String param) {
        return Arrays.stream(param.split(","))
            .map(String::trim)
            .filter(StringUtils::hasText)
            .map(this::parseCleanTask)
            .filter(task -> task != null)
            .toList();
    }

    /**
     * 解析单个清理任务
     * 
     * @param taskStr 任务字符串，格式: tableName:retentionDays
     * @return 清理任务对象
     */
    private CleanTask parseCleanTask(String taskStr) {
        try {
            String[] parts = taskStr.split(":");
            if (parts.length != 2) {
                log.warn("无效的任务格式: {}, 应为 tableName:retentionDays", taskStr);
                return null;
            }

            String tableName = parts[0].trim();
            int retentionDays = Integer.parseInt(parts[1].trim());

            if (!StringUtils.hasText(tableName)) {
                log.warn("表名不能为空: {}", taskStr);
                return null;
            }

            if (retentionDays <= 0) {
                log.warn("保留天数必须大于0: {}", taskStr);
                return null;
            }

            return new CleanTask(tableName, retentionDays);

        } catch (NumberFormatException e) {
            log.warn("无效的保留天数: {}", taskStr);
            return null;
        }
    }

    /**
     * 清理任务内部类
     */
    private static class CleanTask {
        private final String tableName;
        private final int retentionDays;

        public CleanTask(String tableName, int retentionDays) {
            this.tableName = tableName;
            this.retentionDays = retentionDays;
        }

        public String getTableName() {
            return tableName;
        }

        public int getRetentionDays() {
            return retentionDays;
        }
    }
}