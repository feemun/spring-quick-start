package cloud.catfish.es.annotation;

import java.lang.annotation.*;

/**
 * Elasticsearch SQL查询注解
 * 类似于MyBatis的@Select注解，用于标记SQL查询方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EsSQL {

    /**
     * SQL查询语句
     * 支持参数占位符，如：SELECT * FROM sample WHERE category = ?
     */
    String value();

    /**
     * 索引名称
     * 如果不指定，将从SQL语句中解析FROM子句获取
     */
    String index() default "";

    /**
     * 是否启用分页
     * 当方法返回类型为PageResult时自动启用
     */
    boolean pageable() default false;

    /**
     * 默认页大小（当启用分页时）
     */
    int defaultPageSize() default 10;

    /**
     * 是否使用游标查询（适用于大数据量）
     */
    boolean cursor() default false;

    /**
     * 游标批次大小
     */
    int fetchSize() default 1000;

    /**
     * 查询超时时间（毫秒）
     */
    long timeout() default 30000;
}