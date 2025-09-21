package cloud.catfish.dataprocessing.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 数据处理模块配置属性
 * 
 * @author catfish
 * @since 1.0.0
 */
@Data
@Component
@ConfigurationProperties(prefix = "data.processing")
public class DataProcessingProperties {

    /**
     * 是否启用数据处理模块
     */
    private boolean enabled = true;

    /**
     * 批处理大小
     */
    private int batchSize = 1000;

    /**
     * 线程池配置
     */
    private ThreadPool threadPool = new ThreadPool();

    /**
     * 数据清理配置
     */
    private DataClean dataClean = new DataClean();

    /**
     * 数据同步配置
     */
    private DataSync dataSync = new DataSync();

    /**
     * 数据统计配置
     */
    private DataStats dataStats = new DataStats();

    @Data
    public static class ThreadPool {
        /**
         * 核心线程数
         */
        private int corePoolSize = 5;

        /**
         * 最大线程数
         */
        private int maximumPoolSize = 10;

        /**
         * 线程空闲时间（秒）
         */
        private long keepAliveTime = 60;

        /**
         * 队列容量
         */
        private int queueCapacity = 100;
    }

    @Data
    public static class DataClean {
        /**
         * 是否启用数据清理
         */
        private boolean enabled = true;

        /**
         * 数据保留天数
         */
        private int retentionDays = 30;

        /**
         * 清理批次大小
         */
        private int batchSize = 500;
    }

    @Data
    public static class DataSync {
        /**
         * 是否启用数据同步
         */
        private boolean enabled = true;

        /**
         * 同步间隔（分钟）
         */
        private int intervalMinutes = 30;

        /**
         * 同步批次大小
         */
        private int batchSize = 1000;
    }

    @Data
    public static class DataStats {
        /**
         * 是否启用数据统计
         */
        private boolean enabled = true;

        /**
         * 统计间隔（小时）
         */
        private int intervalHours = 1;

        /**
         * 统计数据保留天数
         */
        private int retentionDays = 90;
    }
}