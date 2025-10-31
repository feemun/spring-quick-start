package cloud.catfish.es.config;

import cloud.catfish.es.annotation.EsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Elasticsearch Mapper扫描注册器
 * 扫描@EsMapper注解的接口并注册为Spring Bean
 */
@Slf4j
public class EsMapperScannerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 获取@EnableEsMappers注解的属性
        Set<String> basePackages = getBasePackages(importingClassMetadata);
        
        // 创建自定义扫描器
        EsMapperScanner scanner = new EsMapperScanner(registry);
        scanner.registerFilters();
        
        // 扫描并注册Mapper
        scanner.doScan(basePackages.toArray(new String[0]));
    }

    /**
     * 获取要扫描的基础包路径
     */
    private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Set<String> basePackages = new LinkedHashSet<>();
        
        // 从@EnableEsMappers注解获取basePackages
        if (importingClassMetadata.hasAnnotation(EnableEsMappers.class.getName())) {
            String[] packages = (String[]) importingClassMetadata
                .getAnnotationAttributes(EnableEsMappers.class.getName())
                .get("basePackages");
            
            if (packages != null) {
                basePackages.addAll(Arrays.asList(packages));
            }
            
            // 获取basePackageClasses
            Class<?>[] classes = (Class<?>[]) importingClassMetadata
                .getAnnotationAttributes(EnableEsMappers.class.getName())
                .get("basePackageClasses");
            
            if (classes != null) {
                for (Class<?> clazz : classes) {
                    basePackages.add(clazz.getPackage().getName());
                }
            }
        }
        
        // 如果没有指定包，使用配置类所在的包
        if (basePackages.isEmpty()) {
            basePackages.add(getPackageName(importingClassMetadata.getClassName()));
        }
        
        return basePackages;
    }

    /**
     * 获取类的包名
     */
    private String getPackageName(String className) {
        int lastDot = className.lastIndexOf('.');
        return lastDot != -1 ? className.substring(0, lastDot) : "";
    }

    /**
     * 自定义Mapper扫描器
     */
    private static class EsMapperScanner extends ClassPathBeanDefinitionScanner {

        public EsMapperScanner(BeanDefinitionRegistry registry) {
            super(registry, false);
        }

        /**
         * 注册过滤器，只扫描@EsMapper注解的接口
         */
        public void registerFilters() {
            // 添加@EsMapper注解过滤器
            addIncludeFilter(new AnnotationTypeFilter(EsMapper.class));
        }

        @Override
        protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
            
            if (beanDefinitions.isEmpty()) {
                log.warn("No EsMapper was found in '{}' package. Please check your configuration.", 
                    Arrays.toString(basePackages));
            } else {
                processBeanDefinitions(beanDefinitions);
            }
            
            return beanDefinitions;
        }

        /**
         * 处理Bean定义，将接口转换为代理Bean
         */
        private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
            for (BeanDefinitionHolder holder : beanDefinitions) {
                GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
                String beanClassName = definition.getBeanClassName();
                
                log.debug("Creating EsMapperProxyFactoryBean with mapper interface: {}", beanClassName);
                
                // 设置构造函数参数
                definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
                
                // 设置Bean类为代理工厂Bean
                definition.setBeanClass(EsMapperProxyFactoryBean.class);
                
                // 设置自动装配模式
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            }
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            AnnotationMetadata metadata = beanDefinition.getMetadata();
            return metadata.isInterface() && metadata.isIndependent();
        }

        @Override
        protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
            if (super.checkCandidate(beanName, beanDefinition)) {
                return true;
            } else {
                log.warn("Skipping EsMapperProxyFactoryBean with name '{}' and '{}' mapperInterface. " +
                    "Bean already defined with the same name!", beanName, beanDefinition.getBeanClassName());
                return false;
            }
        }
    }
}