package cloud.catfish.dataprocessing.service.impl;

import cloud.catfish.dataprocessing.service.DataProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据处理服务实现类
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@Service
public class DataProcessingServiceImpl implements DataProcessingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public int cleanExpiredData(String tableName, int retentionDays) {
        try {
            String sql = String.format(
                "DELETE FROM %s WHERE created_time < DATE_SUB(NOW(), INTERVAL %d DAY)",
                tableName, retentionDays
            );
            
            int deletedCount = jdbcTemplate.update(sql);
            log.info("清理过期数据完成: 表={}, 保留天数={}, 删除记录数={}", tableName, retentionDays, deletedCount);
            
            return deletedCount;
        } catch (Exception e) {
            log.error("清理过期数据失败: 表={}, 保留天数={}", tableName, retentionDays, e);
            throw new RuntimeException("清理过期数据失败", e);
        }
    }

    @Override
    @Transactional
    public int syncData(String sourceTable, String targetTable, LocalDateTime lastSyncTime) {
        try {
            // 查询需要同步的数据
            String selectSql = String.format(
                "SELECT * FROM %s WHERE updated_time > ?",
                sourceTable
            );
            
            List<Map<String, Object>> dataToSync = jdbcTemplate.queryForList(
                selectSql, lastSyncTime.format(FORMATTER)
            );
            
            if (dataToSync.isEmpty()) {
                log.info("没有需要同步的数据: 源表={}, 目标表={}", sourceTable, targetTable);
                return 0;
            }
            
            // 构建插入或更新SQL
            int syncedCount = 0;
            for (Map<String, Object> row : dataToSync) {
                String insertSql = buildInsertOrUpdateSql(targetTable, row);
                jdbcTemplate.update(insertSql, row.values().toArray());
                syncedCount++;
            }
            
            log.info("数据同步完成: 源表={}, 目标表={}, 同步记录数={}", sourceTable, targetTable, syncedCount);
            return syncedCount;
            
        } catch (Exception e) {
            log.error("数据同步失败: 源表={}, 目标表={}", sourceTable, targetTable, e);
            throw new RuntimeException("数据同步失败", e);
        }
    }

    @Override
    public Map<String, Object> generateStats(String tableName, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 总记录数
            String countSql = String.format(
                "SELECT COUNT(*) FROM %s WHERE created_time BETWEEN ? AND ?",
                tableName
            );
            Long totalCount = jdbcTemplate.queryForObject(
                countSql, Long.class, 
                startTime.format(FORMATTER), endTime.format(FORMATTER)
            );
            stats.put("totalCount", totalCount);
            
            // 按日期分组统计
            String dailyStatsSql = String.format(
                "SELECT DATE(created_time) as date, COUNT(*) as count " +
                "FROM %s WHERE created_time BETWEEN ? AND ? " +
                "GROUP BY DATE(created_time) ORDER BY date",
                tableName
            );
            List<Map<String, Object>> dailyStats = jdbcTemplate.queryForList(
                dailyStatsSql, startTime.format(FORMATTER), endTime.format(FORMATTER)
            );
            stats.put("dailyStats", dailyStats);
            
            // 按小时分组统计
            String hourlyStatsSql = String.format(
                "SELECT HOUR(created_time) as hour, COUNT(*) as count " +
                "FROM %s WHERE created_time BETWEEN ? AND ? " +
                "GROUP BY HOUR(created_time) ORDER BY hour",
                tableName
            );
            List<Map<String, Object>> hourlyStats = jdbcTemplate.queryForList(
                hourlyStatsSql, startTime.format(FORMATTER), endTime.format(FORMATTER)
            );
            stats.put("hourlyStats", hourlyStats);
            
            stats.put("startTime", startTime.format(FORMATTER));
            stats.put("endTime", endTime.format(FORMATTER));
            
            log.info("生成统计数据完成: 表={}, 时间范围={} - {}, 总记录数={}", 
                tableName, startTime.format(FORMATTER), endTime.format(FORMATTER), totalCount);
            
            return stats;
            
        } catch (Exception e) {
            log.error("生成统计数据失败: 表={}, 时间范围={} - {}", 
                tableName, startTime.format(FORMATTER), endTime.format(FORMATTER), e);
            throw new RuntimeException("生成统计数据失败", e);
        }
    }

    @Override
    @Transactional
    public int batchProcess(String tableName, DataProcessor processor, int batchSize) {
        try {
            long totalCount = getTableCount(tableName);
            int processedCount = 0;
            int offset = 0;
            
            while (offset < totalCount) {
                String sql = String.format(
                    "SELECT * FROM %s LIMIT %d OFFSET %d",
                    tableName, batchSize, offset
                );
                
                List<Map<String, Object>> batch = jdbcTemplate.queryForList(sql);
                if (batch.isEmpty()) {
                    break;
                }
                
                boolean success = processor.process(batch);
                if (success) {
                    processedCount += batch.size();
                } else {
                    log.warn("批次处理失败: 表={}, offset={}, batchSize={}", tableName, offset, batchSize);
                }
                
                offset += batchSize;
            }
            
            log.info("批量处理完成: 表={}, 处理记录数={}", tableName, processedCount);
            return processedCount;
            
        } catch (Exception e) {
            log.error("批量处理失败: 表={}", tableName, e);
            throw new RuntimeException("批量处理失败", e);
        }
    }

    @Override
    public long getTableCount(String tableName) {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s", tableName);
            return jdbcTemplate.queryForObject(sql, Long.class);
        } catch (Exception e) {
            log.error("获取表记录数失败: 表={}", tableName, e);
            return 0;
        }
    }

    @Override
    public long getTableCount(String tableName, String condition) {
        try {
            String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s", tableName, condition);
            return jdbcTemplate.queryForObject(sql, Long.class);
        } catch (Exception e) {
            log.error("获取表记录数失败: 表={}, 条件={}", tableName, condition, e);
            return 0;
        }
    }

    /**
     * 构建插入或更新SQL
     */
    private String buildInsertOrUpdateSql(String tableName, Map<String, Object> row) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ").append(tableName).append(" (");
        
        // 字段名
        String columns = String.join(", ", row.keySet());
        sql.append(columns).append(") VALUES (");
        
        // 占位符
        String placeholders = row.keySet().stream()
            .map(k -> "?")
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
        sql.append(placeholders).append(")");
        
        // ON DUPLICATE KEY UPDATE
        sql.append(" ON DUPLICATE KEY UPDATE ");
        String updates = row.keySet().stream()
            .map(k -> k + " = VALUES(" + k + ")")
            .reduce((a, b) -> a + ", " + b)
            .orElse("");
        sql.append(updates);
        
        return sql.toString();
    }
}