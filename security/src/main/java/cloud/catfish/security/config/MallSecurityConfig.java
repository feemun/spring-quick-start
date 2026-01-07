package cloud.catfish.security.config;

import cloud.catfish.api.domain.*;
import cloud.catfish.mbg.mapper.UmsAdminMapper;
import cloud.catfish.mbg.mapper.UmsAdminRoleRelationMapper;
import cloud.catfish.mbg.mapper.UmsResourceMapper;
import cloud.catfish.mbg.mapper.UmsRoleResourceRelationMapper;
import cloud.catfish.security.component.DynamicSecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class MallSecurityConfig {

    private final UmsAdminMapper umsAdminMapper;
    private final UmsResourceMapper umsResourceMapper;
    private final UmsAdminRoleRelationMapper umsAdminRoleRelationMapper;
    private final UmsRoleResourceRelationMapper umsRoleResourceRelationMapper;

    public MallSecurityConfig(
            UmsAdminMapper umsAdminMapper,
            UmsResourceMapper umsResourceMapper,
            UmsAdminRoleRelationMapper umsAdminRoleRelationMapper,
            UmsRoleResourceRelationMapper umsRoleResourceRelationMapper
    ) {
        this.umsAdminMapper = umsAdminMapper;
        this.umsResourceMapper = umsResourceMapper;
        this.umsAdminRoleRelationMapper = umsAdminRoleRelationMapper;
        this.umsRoleResourceRelationMapper = umsRoleResourceRelationMapper;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UmsAdminExample example = new UmsAdminExample();
            example.createCriteria().andUsernameEqualTo(username);
            List<UmsAdmin> admins = umsAdminMapper.selectByExample(example);
            if (admins == null || admins.isEmpty()) {
                throw new UsernameNotFoundException("用户名或密码错误");
            }

            UmsAdmin admin = admins.get(0);
            List<UmsResource> resources = getResourceList(admin.getId());
            String[] authorities = resources.stream()
                    .map(resource -> resource.getId() + ":" + resource.getName())
                    .toArray(String[]::new);

            return User.withUsername(admin.getUsername())
                    .password(admin.getPassword())
                    .disabled(!Boolean.TRUE.equals(admin.getStatus()))
                    .accountExpired(false)
                    .accountLocked(false)
                    .credentialsExpired(false)
                    .authorities(authorities)
                    .build();
        };
    }

    @Bean
    public DynamicSecurityService dynamicSecurityService() {
        return () -> {
            Map<String, String> map = new ConcurrentHashMap<>();
            List<UmsResource> resources = umsResourceMapper.selectByExample(new UmsResourceExample());
            for (UmsResource resource : resources) {
                map.put(resource.getUrl(), resource.getId() + ":" + resource.getName());
            }
            return map;
        };
    }

    private List<UmsResource> getResourceList(Long adminId) {
        if (adminId == null) {
            return Collections.emptyList();
        }

        UmsAdminRoleRelationExample adminRoleExample = new UmsAdminRoleRelationExample();
        adminRoleExample.createCriteria().andAdminIdEqualTo(adminId);
        List<UmsAdminRoleRelation> adminRoles = umsAdminRoleRelationMapper.selectByExample(adminRoleExample);
        if (adminRoles == null || adminRoles.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> roleIds = adminRoles.stream().map(UmsAdminRoleRelation::getRoleId).distinct().toList();
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        UmsRoleResourceRelationExample roleResourceExample = new UmsRoleResourceRelationExample();
        roleResourceExample.createCriteria().andRoleIdIn(roleIds);
        List<UmsRoleResourceRelation> roleResources = umsRoleResourceRelationMapper.selectByExample(roleResourceExample);
        if (roleResources == null || roleResources.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> resourceIds = roleResources.stream().map(UmsRoleResourceRelation::getResourceId).distinct().toList();
        if (resourceIds.isEmpty()) {
            return Collections.emptyList();
        }

        UmsResourceExample resourceExample = new UmsResourceExample();
        resourceExample.createCriteria().andIdIn(resourceIds);
        resourceExample.setDistinct(true);
        return umsResourceMapper.selectByExample(resourceExample);
    }
}
