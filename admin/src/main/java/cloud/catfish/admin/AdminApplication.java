package cloud.catfish.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用启动入口
 * Created by macro on 2018/4/26.
 */
@SpringBootApplication(scanBasePackages = {"cloud.catfish.admin", "cloud.catfish.common", "cloud.catfish.mbg", "cloud.catfish.security"})
@MapperScan({"cloud.catfish.admin.dao", "cloud.catfish.mbg.mapper"})
@EnableScheduling
public class AdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
