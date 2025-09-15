# Bootstrap 启动模块

## 模块简介

Bootstrap模块是Spring Quick Start项目的统一启动模块，负责整合和启动所有业务模块，包括：
- **Admin模块**：管理后台功能
- **App模块**：用户前端服务
- **ES模块**：Elasticsearch搜索服务
- **Neo4j模块**：图数据库服务

## 架构优势

### 1. 模块化设计
- 各业务模块职责单一，便于维护和扩展
- 统一启动入口，简化部署和管理
- 模块间松耦合，支持独立开发和测试

### 2. 统一安全管理
- 集中的Spring Security配置
- 统一的权限控制和认证机制
- 跨模块的安全策略一致性

### 3. 统一配置管理
- 集中的配置文件管理
- 环境特定的配置支持
- 统一的日志和监控配置

## 快速开始

### 1. 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Elasticsearch 8.x
- Neo4j 5.x

### 2. 本地开发启动

```bash
# 1. 克隆项目
git clone <repository-url>
cd spring-quick-start

# 2. 编译项目
mvn clean compile

# 3. 启动应用
cd bootstrap
mvn spring-boot:run
```

### 3. Docker部署

```bash
# 1. 构建并启动所有服务
docker-compose up -d

# 2. 查看服务状态
docker-compose ps

# 3. 查看应用日志
docker-compose logs -f spring-quick-start
```

## 配置说明

### 1. 应用配置
- `application.yml`：主配置文件
- `application-dev.yml`：开发环境配置
- `application-docker.yml`：Docker环境配置

### 2. 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/spring_quick_start
    username: root
    password: 123456
```

### 3. 缓存配置
```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

## API文档

启动应用后，可通过以下地址访问API文档：
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api/v3/api-docs

## 健康检查

- 健康检查端点: http://localhost:8080/api/health
- 系统信息端点: http://localhost:8080/api/health/info
- Actuator端点: http://localhost:8080/api/actuator

## 安全配置

### 1. 接口权限
- `/admin/**`：需要ADMIN角色
- `/app/**`：需要USER或ADMIN角色
- `/es/**`：需要USER或ADMIN角色
- `/neo4j/**`：需要USER或ADMIN角色

### 2. 公开接口
- Swagger文档接口
- 健康检查接口
- Actuator监控接口

## 部署说明

### 1. 生产环境部署
```bash
# 1. 打包应用
mvn clean package -DskipTests

# 2. 运行jar包
java -jar bootstrap/target/bootstrap-*.jar --spring.profiles.active=prod
```

### 2. Docker部署
```bash
# 使用docker-compose部署完整环境
docker-compose -f docker-compose.yml up -d
```

## 监控和日志

### 1. 应用监控
- Spring Boot Actuator提供健康检查和指标监控
- 支持Prometheus指标导出

### 2. 日志配置
- 开发环境：控制台输出，DEBUG级别
- 生产环境：文件输出，INFO级别
- 支持按模块配置日志级别

## 扩展功能

### 1. 添加新模块
1. 创建新的Maven模块
2. 在bootstrap的pom.xml中添加依赖
3. 在BootstrapApplication中添加扫描包路径
4. 配置相应的安全规则

### 2. 自定义配置
- 继承现有配置类进行扩展
- 使用@Profile注解支持环境特定配置
- 通过配置文件外部化配置参数

## 注意事项

1. **模块依赖**：各业务模块不应直接依赖其他业务模块
2. **配置管理**：敏感配置信息应通过环境变量或外部配置文件管理
3. **安全考虑**：生产环境应配置适当的安全策略和HTTPS
4. **性能优化**：根据实际负载调整JVM参数和连接池配置

## 技术栈

- **框架**：Spring Boot 3.x
- **安全**：Spring Security 6.x
- **数据库**：MySQL 8.0, Redis 6.0
- **搜索**：Elasticsearch 8.x
- **图数据库**：Neo4j 5.x
- **文档**：SpringDoc OpenAPI 3.x
- **容器化**：Docker, Docker Compose