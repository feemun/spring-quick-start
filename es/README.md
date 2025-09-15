# Elasticsearch 通用查询模块

## 模块简介

本模块提供了基于 Spring Boot 和 Elasticsearch 的通用查询功能，支持多种查询方式和完整的 CRUD 操作。

## 功能特性

- ✅ 通用搜索（关键词搜索、精确匹配、范围查询）
- ✅ 高亮显示
- ✅ 分页查询
- ✅ 排序功能
- ✅ 完整的 CRUD 操作
- ✅ 索引管理
- ✅ 批量操作
- ✅ RESTful API 接口
- ✅ Swagger 文档支持

## 快速开始

### 1. 配置 Elasticsearch

在 `application-dev.yml` 中配置 Elasticsearch 连接信息：

```yaml
elasticsearch:
  host: localhost
  port: 9200
  username: elastic
  password: password
  connection-timeout: 5000
  socket-timeout: 60000
```

### 2. 启动应用

```bash
cd es
mvn spring-boot:run
```

### 3. 访问 API 文档

启动后访问：http://localhost:8082/swagger-ui.html

## API 接口

### 搜索相关

- `POST /api/es/search` - 通用搜索
- `GET /api/es/{index}/{id}` - 根据 ID 获取文档

### 文档操作

- `POST /api/es/{index}` - 保存单个文档
- `POST /api/es/{index}/batch` - 批量保存文档
- `DELETE /api/es/{index}/{id}` - 删除单个文档
- `DELETE /api/es/{index}/batch` - 批量删除文档

### 索引管理

- `GET /api/es/{index}/exists` - 检查索引是否存在
- `POST /api/es/{index}/create` - 创建索引
- `DELETE /api/es/{index}` - 删除索引
- `GET /api/es/{index}/mapping` - 获取索引映射

### 聚合查询

- `POST /api/es/{index}/aggregate` - 聚合查询

## 使用示例

### 1. 通用搜索

```json
POST /api/es/search
{
  "index": "products",
  "keyword": "手机",
  "fields": ["name", "description"],
  "pageNum": 1,
  "pageSize": 10,
  "sortField": "price",
  "sortOrder": "desc",
  "highlightFields": ["name", "description"],
  "exactMatch": {
    "category": "electronics"
  },
  "rangeQuery": {
    "price": {
      "gte": 1000,
      "lte": 5000
    }
  }
}
```

### 2. 保存文档

```json
POST /api/es/products?id=1
{
  "name": "iPhone 15",
  "price": 5999,
  "category": "electronics",
  "description": "最新款苹果手机"
}
```

### 3. 创建索引

```json
POST /api/es/products/create
{
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "analyzer": "ik_max_word"
      },
      "price": {
        "type": "double"
      },
      "category": {
        "type": "keyword"
      }
    }
  }
}
```

## 核心组件

### 1. ElasticsearchConfig
Elasticsearch 配置类，负责客户端连接配置。

### 2. ElasticsearchService
通用查询服务接口，定义了所有 ES 操作方法。

### 3. ElasticsearchServiceImpl
服务实现类，提供完整的 ES 操作功能。

### 4. ElasticsearchController
REST 控制器，提供 HTTP API 接口。

### 5. SearchRequest/SearchResponse
搜索请求和响应的 DTO 类。

## 依赖说明

- Spring Boot 3.2.2
- Spring Data Elasticsearch
- Hutool 工具库
- SpringDoc OpenAPI (Swagger)

## 注意事项

1. 确保 Elasticsearch 服务正常运行
2. 根据实际环境调整连接配置
3. 建议在生产环境中配置认证信息
4. 大批量操作时注意性能和内存使用

## 扩展功能

模块设计为通用查询框架，可以根据具体业务需求进行扩展：

- 自定义分析器
- 复杂聚合查询
- 地理位置查询
- 更多查询类型支持