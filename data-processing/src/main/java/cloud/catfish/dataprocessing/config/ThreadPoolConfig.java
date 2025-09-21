package cloud.catfish.dataprocessing.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池配置类
 * 
 * @author catfish
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class ThreadPoolConfig {

    @Autowired
    private DataProcessingProperties properties;

    /**
     * 数据处理任务线程池
     */
    @Bean("taskExecutor")
    public ThreadPoolExecutor taskExecutor() {
        DataProcessingProperties.ThreadPool config = properties.getThreadPool();
        
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            config.getCorePoolSize(),
            config.getMaximumPoolSize(),
            config.getKeepAliveTime(),
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(config.getQueueCapacity()),
            new DataProcessingThreadFactory("data-processing-"),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
        
        log.info("初始化数据处理线程池: corePoolSize={}, maximumPoolSize={}, keepAliveTime={}s, queueCapacity={}",
            config.getCorePoolSize(), config.getMaximumPoolSize(), 
            config.getKeepAliveTime(), config.getQueueCapacity());
        
        return executor;
    }

    /**
     * 自定义线程工厂
     */
    private static class DataProcessingThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DataProcessingThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}