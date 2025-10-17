package cloud.catfish.es.config;

import cloud.catfish.es.proxy.EsMapperProxyFactory;
import cloud.catfish.es.template.EsDslTemplate;
import cloud.catfish.es.template.EsSqlTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch Mapper自动配置类
 * 自动配置Mapper相关的Bean
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass({EsDslTemplate.class, EsSqlTemplate.class})
public class EsMapperAutoConfiguration {

    /**
     * 配置EsMapperProxyFactory Bean
     */
    @Bean
    @ConditionalOnMissingBean
    public EsMapperProxyFactory esMapperProxyFactory(EsDslTemplate esDslTemplate,
                                                    EsSqlTemplate esSqlTemplate,
                                                    ObjectMapper objectMapper) {
        log.info("Creating EsMapperProxyFactory bean");
        return new EsMapperProxyFactory(esDslTemplate, esSqlTemplate, objectMapper);
    }

    /**
     * 配置ObjectMapper Bean（如果不存在的话）
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        log.info("Creating ObjectMapper bean for ES Mapper");
        return new ObjectMapper();
    }
}