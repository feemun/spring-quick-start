package cloud.catfish.admin.config;

import cloud.catfish.admin.service.UmsAdminService;
import cloud.catfish.admin.service.UmsResourceService;
import domain.UmsResource;
import cloud.catfish.security.component.DynamicSecurityService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.security.core.userdetails.UserDetailsService;

 

/**
 * mall-security模块相关配置
 * Created by macro on 2019/11/9.
 */
@Configuration
public class MallSecurityConfig {

    @Resource
    private UmsAdminService adminService;
    @Resource
    private UmsResourceService resourceService;

    @Bean
    public UserDetailsService userDetailsService() {
        //获取登录用户信息
        return username -> adminService.loadUserByUsername(username);
    }

    @Bean
    public DynamicSecurityService dynamicSecurityService() {
        return new DynamicSecurityService() {
            @Override
            public Map<String, String> loadDataSource() {
                Map<String, String> map = new ConcurrentHashMap<>();
                List<UmsResource> resourceList = resourceService.listAll();
                for (UmsResource resource : resourceList) {
                    map.put(resource.getUrl(), resource.getId() + ":" + resource.getName());
                }
                return map;
            }
        };
    }
}
