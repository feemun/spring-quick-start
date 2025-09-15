package cloud.catfish.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * App应用主启动类
 * 用户展示服务模块
 * 
 * @author catfish
 * @since 1.0.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"cloud.catfish.app", "cloud.catfish.common"})
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}