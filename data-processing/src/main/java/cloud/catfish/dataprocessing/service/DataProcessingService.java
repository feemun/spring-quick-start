package cloud.catfish.dataprocessing.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据处理服务接口
 * 定义通用的数据处理方法
 * 
 * @author catfish
 * @since 1.0.0
 */
public interface DataProcessingService {

    /**
     * 清理过期数据
     * 
     * @param tableName 表名
     * @param retentionDays 保留天数
     * @return 清理的记录数
     */
    int cleanExpiredData(String tableName, int retentionDays);

    /**
     * 同步数据
     * 
     * @param sourceTable 源表
     * @param targetTable 目标表
     * @param lastSyncTime 上次同步时间
     * @return 同步的记录数
     */
    int syncData(String sourceTable, String targetTable, LocalDateTime lastSyncTime);

    /**
     * 生成统计数据
     * 
     * @param tableName 表名
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    Map<String, Object> generateStats(String tableName, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 批量处理数据
     * 
     * @param tableName 表名
     * @param processor 处理器
     * @param batchSize 批次大小
     * @return 处理的记录数
     */
    int batchProcess(String tableName, DataProcessor processor, int batchSize);

    /**
     * 获取表的记录总数
     * 
     * @param tableName 表名
     * @return 记录总数
     */
    long getTableCount(String tableName);

    /**
     * 获取表的记录总数（带条件）
     * 
     * @param tableName 表名
     * @param condition 条件
     * @return 记录总数
     */
    long getTableCount(String tableName, String condition);

    /**
     * 数据处理器接口
     */
    @FunctionalInterface
    interface DataProcessor {
        /**
         * 处理数据
         * 
         * @param data 数据列表
         * @return 处理结果
         */
        boolean process(List<Map<String, Object>> data);
    }
}