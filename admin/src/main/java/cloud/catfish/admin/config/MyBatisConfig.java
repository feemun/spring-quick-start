package cloud.catfish.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import javax.sql.DataSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * MyBatis相关配置
 * Created by macro on 2019/4/8.
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"cloud.catfish.mbg.mapper","cloud.catfish.admin.dao"})
public class MyBatisConfig {
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        var resources1 = resolver.getResources("classpath:dao/*.xml");
        var resources2 = resolver.getResources("classpath*:cloud/catfish/mbg/mapper/*.xml");
        var combined = new org.springframework.core.io.Resource[resources1.length + resources2.length];
        System.arraycopy(resources1, 0, combined, 0, resources1.length);
        System.arraycopy(resources2, 0, combined, resources1.length, resources2.length);
        factoryBean.setMapperLocations(combined);
        return factoryBean.getObject();
    }

    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
