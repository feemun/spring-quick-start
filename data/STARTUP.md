# data 模块启动说明

## 模块信息

- 模块路径：`spring-quick-start-backend/data`
- Spring Boot 端口：`8085`（见 [application.yml](file:///c:/Users/feemu/Documents/GitHub/spring-quick-start/spring-quick-start-backend/data/src/main/resources/application.yml)）

## 前置条件

- JDK：21
- Maven：3.9+
- 依赖服务（application.yml 中为示例开发配置）
  - MySQL：`10.0.0.6:13306`，库：`map_oxygen`
  - Kafka：`10.0.0.6:9092`
  - Elasticsearch：`10.0.0.6:19200`
  - XXL-Job Admin：`http://127.0.0.1:8080/xxl-job-admin`（若未启动会打印注册失败日志，但不一定阻塞 Web 服务启动）

## 启动流程（clean → package → run）

在 `spring-quick-start-backend/data` 目录下执行：

1. Maven clean

```bash
mvn clean
```

2. Maven 打包

```bash
mvn package -DskipTests
```

3. 启动项目

```bash
java -jar target/data-oxygen.jar
```

## 验证方式

- 健康检查：

```bash
curl http://localhost:8085/actuator/health
```

## 常见问题

- XXL-Job 注册失败（Connection refused）：确认 `xxl.job.admin.addresses` 指向的管理端是否启动，或调整为正确地址
- MySQL/Kafka/ES 连接失败：检查 [application.yml](file:///c:/Users/feemu/Documents/GitHub/spring-quick-start/spring-quick-start-backend/data/src/main/resources/application.yml) 中连接信息与网络连通性

