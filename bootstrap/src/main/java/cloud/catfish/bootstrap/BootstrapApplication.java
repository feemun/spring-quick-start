package cloud.catfish.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * 统一启动类
 * 整合所有业务模块：admin、app、es、neo4j、redis
 * 
 * @author catfish
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = {
    "cloud.catfish.admin",
    "cloud.catfish.app", 
    "cloud.catfish.es",
    "cloud.catfish.neo4j",
    "cloud.catfish.redis",
    "cloud.catfish.common",
    "cloud.catfish.bootstrap"
})
@EnableElasticsearchRepositories(basePackages = "cloud.catfish.es.repository")
public class BootstrapApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BootstrapApplication.class, args);
    }
}