# admin 模块启动说明

## 模块信息

- 模块路径：`spring-quick-start-backend/admin`
- Spring Boot 端口：`18888`（见 [application.yml](file:///c:/Users/feemu/Documents/GitHub/spring-quick-start/spring-quick-start-backend/admin/src/main/resources/application.yml)）
- 默认 profile：`dev`（`spring.profiles.active: dev`）

## 前置条件

- JDK：21
- Maven：3.9+
- 依赖服务（dev 环境示例配置）
  - MySQL：`10.0.0.6:13306`，库：`map_oxygen`（见 [database.yml](file:///c:/Users/feemu/Documents/GitHub/spring-quick-start/spring-quick-start-backend/admin/src/main/resources/config/dev/database.yml)）
  - Redis：`10.0.0.6:6379`（见 [redis.yml](file:///c:/Users/feemu/Documents/GitHub/spring-quick-start/spring-quick-start-backend/admin/src/main/resources/config/dev/redis.yml)）

## 启动流程（clean → package → run）

在 `spring-quick-start-backend/admin` 目录下执行：

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
java -jar target/admin-oxygen.jar
```

## 验证方式

- 健康检查：

```bash
curl http://localhost:18888/actuator/health
```

- 前端联调基地址：
  - 前端 `VITE_BASE_API` 默认指向 `http://localhost:18888`（与该模块端口一致）

## 常见问题

- 端口占用：修改 `server.port` 或释放 `18888`
- MySQL/Redis 连接失败：检查 `config/dev` 目录下连接信息与网络连通性

