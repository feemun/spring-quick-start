package cloud.catfish.es.annotation;

import java.lang.annotation.*;

/**
 * 参数注解
 * 用于标记方法参数名称，类似于MyBatis的@Param注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {

    /**
     * 参数名称
     * 在SQL或DSL中使用#{paramName}或?占位符引用
     */
    String value();
}