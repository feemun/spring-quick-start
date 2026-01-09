package cloud.catfish.admin.service.impl;

import cloud.catfish.admin.dao.UmsAdminRoleRelationDao;
import cloud.catfish.admin.service.UmsAdminCacheService;
import cloud.catfish.cache.service.CacheService;
import cloud.catfish.mbg.mapper.UmsAdminRoleRelationMapper;
import cloud.catfish.mbg.mapper.UmsAdminMapper;
import cloud.catfish.api.domain.UmsAdmin;
import cloud.catfish.api.domain.UmsAdminExample;
import cloud.catfish.api.domain.UmsAdminRoleRelation;
import cloud.catfish.api.domain.UmsAdminRoleRelationExample;
import cloud.catfish.api.domain.UmsResource;
import cn.hutool.core.collection.CollUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台用户缓存管理Service实现类
 * Created by macro on 2020/3/13.
 */
@Service
public class UmsAdminCacheServiceImpl implements UmsAdminCacheService {
    @Autowired
    private UmsAdminMapper adminMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private UmsAdminRoleRelationMapper adminRoleRelationMapper;
    @Autowired
    private UmsAdminRoleRelationDao adminRoleRelationDao;
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;
    @Value("${redis.key.admin}")
    private String REDIS_KEY_ADMIN;
    @Value("${redis.key.resourceList}")
    private String REDIS_KEY_RESOURCE_LIST;

    private String adminCacheName() {
        return REDIS_DATABASE + ":" + REDIS_KEY_ADMIN;
    }

    private String resourceListCacheName() {
        return REDIS_DATABASE + ":" + REDIS_KEY_RESOURCE_LIST;
    }

    @Override
    public void delAdmin(Long adminId) {
        UmsAdmin admin = adminMapper.selectByPrimaryKey(adminId);
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
        UmsAdminRoleRelationExample example = new UmsAdminRoleRelationExample();
        example.createCriteria().andRoleIdEqualTo(roleId);
        List<UmsAdminRoleRelation> relationList = adminRoleRelationMapper.selectByExample(example);
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
        UmsAdminRoleRelationExample example = new UmsAdminRoleRelationExample();
        example.createCriteria().andRoleIdIn(roleIds);
        List<UmsAdminRoleRelation> relationList = adminRoleRelationMapper.selectByExample(example);
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
        UmsAdminExample example = new UmsAdminExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<UmsAdmin> adminList = adminMapper.selectByExample(example);
        if (CollUtil.isEmpty(adminList)) {
            return null;
        }
        return adminList.get(0);
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
