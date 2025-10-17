package cloud.catfish.es.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用Elasticsearch Mapper扫描注解
 * 类似于MyBatis的@MapperScan注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EsMapperScannerRegistrar.class)
public @interface EnableEsMappers {

    /**
     * 要扫描的基础包路径
     */
    String[] basePackages() default {};

    /**
     * 基础包类，使用这些类所在的包作为扫描路径
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * Mapper接口的后缀，默认为"Mapper"
     */
    String mapperSuffix() default "Mapper";
}