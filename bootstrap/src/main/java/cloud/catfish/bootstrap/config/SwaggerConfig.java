package cloud.catfish.bootstrap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger统一配置
 * 整合所有模块的API文档
 * 
 * @author catfish
 * @since 1.0.0
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Spring Quick Start API")
                        .description("统一API文档，整合所有业务模块：Admin管理模块、App用户模块、ES搜索模块、Neo4j图数据库模块")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("catfish")
                                .email("catfish@example.com")
                                .url("https://github.com/catfish"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080/api").description("开发环境"),
                        new Server().url("https://api.example.com").description("生产环境")
                ));
    }
}