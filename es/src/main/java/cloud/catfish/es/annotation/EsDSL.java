package cloud.catfish.es.annotation;

import java.lang.annotation.*;

/**
 * Elasticsearch DSL查询注解
 * 用于标记DSL查询方法，支持JSON字符串或引用外部文件
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsDSL {

    /**
     * DSL查询语句（JSON格式）
     * 支持参数占位符，如：{"term": {"category": "#{category}"}}
     */
    String value() default "";

    /**
     * 外部DSL文件路径（相对于classpath）
     * 当value为空时使用，文件内容为JSON格式的DSL查询
     */
    String file() default "";

    /**
     * 索引名称
     */
    String index();

    /**
     * 查询类型
     */
    QueryType type() default QueryType.SEARCH;

    /**
     * 是否启用分页
     */
    boolean pageable() default false;

    /**
     * 默认页大小
     */
    int defaultPageSize() default 10;

    /**
     * 默认起始位置
     */
    int defaultFrom() default 0;

    /**
     * 排序字段
     */
    String[] sort() default {};

    /**
     * 是否启用聚合查询
     */
    boolean aggregation() default false;

    /**
     * 查询超时时间（毫秒）
     */
    long timeout() default 30000;

    /**
     * 查询类型枚举
     */
    enum QueryType {
        /** 搜索查询 */
        SEARCH,
        /** 计数查询 */
        COUNT,
        /** 聚合查询 */
        AGGREGATION,
        /** 批量操作 */
        BULK
    }
}