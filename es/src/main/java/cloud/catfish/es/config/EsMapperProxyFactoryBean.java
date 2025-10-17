package cloud.catfish.es.config;

import cloud.catfish.es.proxy.EsMapperProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Elasticsearch Mapper代理工厂Bean
 * 用于创建Mapper接口的代理实例
 */
@Slf4j
public class EsMapperProxyFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    @Autowired
    private EsMapperProxyFactory esMapperProxyFactory;

    public EsMapperProxyFactoryBean() {
        // 无参构造函数
    }

    public EsMapperProxyFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() throws Exception {
        log.debug("Creating proxy for mapper interface: {}", mapperInterface.getName());
        return esMapperProxyFactory.createProxy(mapperInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return mapperInterface;
    }
}