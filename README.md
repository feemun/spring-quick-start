# Spring Quick Start

一个基于Spring Boot 3.x的快速开发脚手架项目，采用模块化架构设计，支持多种数据存储和搜索技术。

## 项目架构

### 模块化设计
本项目采用统一启动模块的架构模式，参考了若依(RuoYi)、mall等流行开源项目的最佳实践：

```
spring-quick-start/
├── bootstrap/          # 统一启动模块
├── admin/             # 管理后台模块
├── app/               # 用户前端模块  
├── es/                # Elasticsearch搜索模块
├── neo4j/             # Neo4j图数据库模块
├── common/            # 公共模块
├── mbg/               # MyBatis Generator模块
└── security/          # 安全模块
```

### 架构优势
- **职责分离**：各业务模块职责单一，便于维护和扩展
- **统一管理**：通过bootstrap模块统一启动和管理所有服务
- **安全集中**：统一的Spring Security配置管理所有模块接口
- **配置统一**：集中的配置管理和环境支持
- **部署简化**：单一部署单元，降低运维复杂度

## 技术栈

- **核心框架**：Spring Boot 3.x, Spring Security 6.x
- **数据存储**：MySQL 8.0, Redis 6.0
- **搜索引擎**：Elasticsearch 8.x
- **图数据库**：Neo4j 5.x
- **API文档**：SpringDoc OpenAPI 3.x
- **容器化**：Docker, Docker Compose

## 快速开始

### 1. 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Elasticsearch 8.x
- Neo4j 5.x

### 2. 本地开发

```bash
# 克隆项目
git clone <repository-url>
cd spring-quick-start

# 编译项目
mvn clean compile

# 启动应用（统一启动入口）
cd bootstrap
mvn spring-boot:run
```

### 3. Docker部署

```bash
# 一键启动完整环境
docker-compose up -d

# 查看服务状态
docker-compose ps
```

## 模块说明

### Bootstrap模块（统一启动）
- **职责**：整合所有业务模块，提供统一启动入口
- **特性**：统一安全管理、配置管理、API文档整合
- **端口**：8080

### Admin模块（管理后台）
- **职责**：系统管理、用户管理、权限管理等后台功能
- **特性**：管理员权限控制、系统配置管理

### App模块（用户服务）
- **职责**：面向终端用户的业务功能
- **特性**：用户认证、业务逻辑处理

### ES模块（搜索服务）
- **职责**：提供Elasticsearch搜索功能
- **特性**：通用搜索接口、索引管理、全文检索

### Neo4j模块（图数据库）
- **职责**：图数据存储和查询服务
- **特性**：关系数据建模、图算法支持

## API文档

启动应用后访问：
- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api/v3/api-docs

## 安全配置

### 统一权限管理
- `/admin/**` - 管理员权限
- `/app/**` - 用户权限  
- `/es/**` - 用户权限
- `/neo4j/**` - 用户权限

### 公开接口
- API文档接口
- 健康检查接口
- 监控端点

## 监控和健康检查

- **健康检查**: http://localhost:8080/api/health
- **系统信息**: http://localhost:8080/api/health/info
- **监控端点**: http://localhost:8080/api/actuator

## 开发指南

### 添加新模块
1. 创建Maven子模块
2. 在bootstrap/pom.xml中添加依赖
3. 在BootstrapApplication中配置包扫描
4. 配置相应的安全规则

### 配置管理
- `application.yml` - 主配置
- `application-dev.yml` - 开发环境
- `application-docker.yml` - Docker环境

### 最佳实践
- 业务模块间避免直接依赖
- 通过common模块共享公共组件
- 使用统一的异常处理和响应格式
- 遵循RESTful API设计规范

## 部署说明

### 生产环境
```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar bootstrap/target/bootstrap-*.jar --spring.profiles.active=prod
```

### Docker部署
```bash
# 完整环境部署
docker-compose up -d

# 扩容应用实例
docker-compose up -d --scale spring-quick-start=3
```

## 参考项目

本项目架构设计参考了以下优秀开源项目：
- [RuoYi-Cloud](https://gitee.com/y_project/RuoYi-Cloud) - 微服务架构设计
- [mall](https://github.com/macrozheng/mall) - 模块化设计模式
- [jeecg-boot](https://github.com/jeecgboot/jeecg-boot) - 统一启动模式

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交变更
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情