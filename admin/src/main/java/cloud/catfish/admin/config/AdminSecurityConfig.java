package cloud.catfish.admin.config;

import cloud.catfish.api.domain.UmsAdmin;
import cloud.catfish.api.domain.UmsAdminRoleRelation;
import cloud.catfish.api.domain.UmsResource;
import cloud.catfish.api.domain.UmsRoleResourceRelation;
import cloud.catfish.mbg.mapper.UmsAdminMapper;
import cloud.catfish.mbg.mapper.UmsAdminRoleRelationMapper;
import cloud.catfish.mbg.mapper.UmsResourceMapper;
import cloud.catfish.mbg.mapper.UmsRoleResourceRelationMapper;
import cloud.catfish.security.component.DynamicSecurityService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
public class AdminSecurityConfig {
    private final UmsAdminMapper umsAdminMapper;
    private final UmsResourceMapper umsResourceMapper;
    private final UmsAdminRoleRelationMapper umsAdminRoleRelationMapper;
    private final UmsRoleResourceRelationMapper umsRoleResourceRelationMapper;

    public AdminSecurityConfig(
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
            LambdaQueryWrapper<UmsAdmin> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UmsAdmin::getUsername, username);
            List<UmsAdmin> admins = umsAdminMapper.selectList(queryWrapper);
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
            List<UmsResource> resources = umsResourceMapper.selectList(null);
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

        LambdaQueryWrapper<UmsAdminRoleRelation> adminRoleQueryWrapper = new LambdaQueryWrapper<>();
        adminRoleQueryWrapper.eq(UmsAdminRoleRelation::getAdminId, adminId);
        List<UmsAdminRoleRelation> adminRoles = umsAdminRoleRelationMapper.selectList(adminRoleQueryWrapper);
        if (adminRoles == null || adminRoles.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> roleIds = adminRoles.stream().map(UmsAdminRoleRelation::getRoleId).distinct().toList();
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<UmsRoleResourceRelation> roleResourceQueryWrapper = new LambdaQueryWrapper<>();
        roleResourceQueryWrapper.in(UmsRoleResourceRelation::getRoleId, roleIds);
        List<UmsRoleResourceRelation> roleResources = umsRoleResourceRelationMapper.selectList(roleResourceQueryWrapper);
        if (roleResources == null || roleResources.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> resourceIds = roleResources.stream().map(UmsRoleResourceRelation::getResourceId).distinct().toList();
        if (resourceIds.isEmpty()) {
            return Collections.emptyList();
        }

        return umsResourceMapper.selectBatchIds(resourceIds);
    }
}

