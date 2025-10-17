package cloud.catfish.es.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Elasticsearch Mapper注解
 * 用于标记Mapper接口，类似于MyBatis的@Mapper注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface EsMapper {

    /**
     * Mapper名称
     * 默认使用接口名称
     */
    String value() default "";

    /**
     * 默认索引名称
     * 当方法注解中未指定索引时使用
     */
    String defaultIndex() default "";
}