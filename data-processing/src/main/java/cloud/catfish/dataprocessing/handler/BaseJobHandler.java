package cloud.catfish.dataprocessing.handler;

import cloud.catfish.dataprocessing.config.DataProcessingProperties;
import com.xxl.job.core.context.XxlJobHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 任务处理器基类
 * 提供通用的任务处理功能和工具方法
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
public abstract class BaseJobHandler {

    @Autowired
    protected DataProcessingProperties properties;

    @Autowired
    protected ThreadPoolExecutor taskExecutor;

    /**
     * 执行任务的抽象方法，子类需要实现具体的业务逻辑
     * 
     * @param param 任务参数
     * @return 执行结果
     * @throws Exception 执行异常
     */
    protected abstract String doExecute(String param) throws Exception;

    /**
     * 任务执行入口，提供统一的异常处理和日志记录
     * 
     * @param param 任务参数
     */
    public final void execute(String param) {
        String jobName = this.getClass().getSimpleName();
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("开始执行任务: {}, 参数: {}", jobName, param);
            XxlJobHelper.log("开始执行任务: {}, 参数: {}", jobName, param);
            
            String result = doExecute(param);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.info("任务执行完成: {}, 耗时: {}ms, 结果: {}", jobName, duration, result);
            XxlJobHelper.log("任务执行完成: {}, 耗时: {}ms, 结果: {}", jobName, duration, result);
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            log.error("任务执行失败: {}, 耗时: {}ms, 错误: {}", jobName, duration, e.getMessage(), e);
            XxlJobHelper.log("任务执行失败: {}, 耗时: {}ms, 错误: {}", jobName, duration, e.getMessage());
            XxlJobHelper.handleFail(e.getMessage());
        }
    }

    /**
     * 获取当前时间字符串
     * 
     * @return 格式化的时间字符串
     */
    protected String getCurrentTimeString() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 记录进度日志
     * 
     * @param message 进度信息
     * @param args 参数
     */
    protected void logProgress(String message, Object... args) {
        String formattedMessage = String.format(message, args);
        log.info(formattedMessage);
        XxlJobHelper.log(formattedMessage);
    }

    /**
     * 检查任务是否被中断
     * 
     * @return true表示任务被中断
     */
    protected boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    /**
     * 处理中断异常
     */
    protected void handleInterrupt() {
        if (isInterrupted()) {
            log.warn("任务被中断: {}", this.getClass().getSimpleName());
            XxlJobHelper.log("任务被中断: {}", this.getClass().getSimpleName());
            throw new RuntimeException("任务被中断");
        }
    }

    /**
     * 分批处理数据
     * 
     * @param totalCount 总数量
     * @param batchSize 批次大小
     * @param processor 批次处理器
     */
    protected void processBatch(int totalCount, int batchSize, BatchProcessor processor) {
        int totalBatches = (totalCount + batchSize - 1) / batchSize;
        
        for (int i = 0; i < totalBatches; i++) {
            handleInterrupt();
            
            int offset = i * batchSize;
            int currentBatchSize = Math.min(batchSize, totalCount - offset);
            
            try {
                processor.process(offset, currentBatchSize);
                logProgress("处理进度: %d/%d 批次完成", i + 1, totalBatches);
            } catch (Exception e) {
                log.error("批次处理失败: offset={}, batchSize={}", offset, currentBatchSize, e);
                throw new RuntimeException("批次处理失败", e);
            }
        }
    }

    /**
     * 批次处理器接口
     */
    @FunctionalInterface
    protected interface BatchProcessor {
        void process(int offset, int batchSize) throws Exception;
    }
}