package cloud.catfish.admin.service.impl;

import cloud.catfish.admin.dao.UmsAdminRoleRelationDao;
import cloud.catfish.admin.config.AdminProperties.RedisProperties;
import cloud.catfish.admin.service.UmsAdminCacheService;
import cloud.catfish.cache.service.CacheService;
import cloud.catfish.mbg.mapper.UmsAdminRoleRelationMapper;
import cloud.catfish.mbg.mapper.UmsAdminMapper;
import cloud.catfish.api.domain.UmsAdmin;
import cloud.catfish.api.domain.UmsAdminRoleRelation;
import cloud.catfish.api.domain.UmsResource;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台用户缓存管理Service实现类
 * Created by macro on 2020/3/13.
 */
@Service
@RequiredArgsConstructor
public class UmsAdminCacheServiceImpl implements UmsAdminCacheService {
    private final UmsAdminMapper adminMapper;
    private final CacheService cacheService;
    private final UmsAdminRoleRelationMapper adminRoleRelationMapper;
    private final UmsAdminRoleRelationDao adminRoleRelationDao;
    private final RedisProperties redisProperties;

    private String adminCacheName() {
        return redisProperties.database() + ":" + redisProperties.key().admin();
    }

    private String resourceListCacheName() {
        return redisProperties.database() + ":" + redisProperties.key().resourceList();
    }

    @Override
    public void delAdmin(Long adminId) {
        UmsAdmin admin = adminMapper.selectById(adminId);
        if (admin == null || admin.getUsername() == null) {
            return;
        }
        cacheService.evict(adminCacheName(), admin.getUsername());
    }

    @Override
    public void delResourceList(Long adminId) {
        cacheService.evict(resourceListCacheName(), String.valueOf(adminId));
    }

    @Override
    public void delResourceListByRole(Long roleId) {
        List<UmsAdminRoleRelation> relationList = adminRoleRelationMapper.selectList(
                new LambdaQueryWrapper<UmsAdminRoleRelation>().eq(UmsAdminRoleRelation::getRoleId, roleId)
        );
        if (CollUtil.isNotEmpty(relationList)) {
            ArrayList<String> keys = new ArrayList<>(relationList.size());
            for (UmsAdminRoleRelation relation : relationList) {
                keys.add(String.valueOf(relation.getAdminId()));
            }
            cacheService.evictAll(resourceListCacheName(), keys);
        }

    }

    @Override
    public void delResourceListByRoleIds(List<Long> roleIds) {
        List<UmsAdminRoleRelation> relationList = adminRoleRelationMapper.selectList(
                new LambdaQueryWrapper<UmsAdminRoleRelation>().in(UmsAdminRoleRelation::getRoleId, roleIds)
        );
        if (CollUtil.isNotEmpty(relationList)) {
            ArrayList<String> keys = new ArrayList<>(relationList.size());
            for (UmsAdminRoleRelation relation : relationList) {
                keys.add(String.valueOf(relation.getAdminId()));
            }
            cacheService.evictAll(resourceListCacheName(), keys);
        }

    }

    @Override
    public void delResourceListByResource(Long resourceId) {
        List<Long> adminIdList = adminRoleRelationDao.getAdminIdList(resourceId);
        if (CollUtil.isNotEmpty(adminIdList)) {
            ArrayList<String> keys = new ArrayList<>(adminIdList.size());
            for (Long adminId : adminIdList) {
                keys.add(String.valueOf(adminId));
            }
            cacheService.evictAll(resourceListCacheName(), keys);
        }

    }

    @Override
    @Cacheable(cacheNames = "${redis.database}:${redis.key.admin}", key = "#username", unless = "#result == null")
    public UmsAdmin getAdmin(String username) {
        if (username == null) {
            return null;
        }
        List<UmsAdmin> adminList = adminMapper.selectList(
                new LambdaQueryWrapper<UmsAdmin>().eq(UmsAdmin::getUsername, username)
        );
        return CollUtil.isEmpty(adminList) ? null : adminList.get(0);
    }

    @Override
    public void setAdmin(UmsAdmin admin) {
        if (admin == null || admin.getUsername() == null) {
            return;
        }
        cacheService.putObject(adminCacheName(), admin.getUsername(), admin);
    }

    @Override
    @Cacheable(cacheNames = "${redis.database}:${redis.key.resourceList}", key = "#adminId.toString()", unless = "#result == null || #result.isEmpty()")
    public List<UmsResource> getResourceList(Long adminId) {
        if (adminId == null) {
            return null;
        }
        return adminRoleRelationDao.getResourceList(adminId);
    }

    @Override
    public void setResourceList(Long adminId, List<UmsResource> resourceList) {
        if (adminId == null) {
            return;
        }
        cacheService.putObject(resourceListCacheName(), String.valueOf(adminId), resourceList);
    }
}
