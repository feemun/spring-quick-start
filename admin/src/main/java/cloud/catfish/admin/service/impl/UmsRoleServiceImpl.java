package cloud.catfish.admin.service.impl;

import cloud.catfish.admin.dao.UmsRoleDao;
import cloud.catfish.admin.service.UmsAdminCacheService;
import cloud.catfish.admin.service.UmsRoleService;
import cloud.catfish.api.domain.*;
import cloud.catfish.mbg.mapper.UmsRoleMapper;
import cloud.catfish.mbg.mapper.UmsRoleMenuRelationMapper;
import cloud.catfish.mbg.mapper.UmsRoleResourceRelationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台角色管理Service实现类
 * Created by macro on 2018/9/30.
 */
@Service
public class UmsRoleServiceImpl implements UmsRoleService {
    @Autowired
    private UmsRoleMapper roleMapper;
    @Autowired
    private UmsRoleMenuRelationMapper roleMenuRelationMapper;
    @Autowired
    private UmsRoleResourceRelationMapper roleResourceRelationMapper;
    @Autowired
    private UmsRoleDao roleDao;
    @Autowired
    private UmsAdminCacheService adminCacheService;

    @Override
    public int create(UmsRole role) {
        role.setCreateTime(LocalDateTime.now());
        role.setAdminCount(0);
        role.setSort(0);
        return roleMapper.insert(role);
    }

    @Override
    public int update(Long id, UmsRole role) {
        role.setId(id);
        return roleMapper.updateById(role);
    }

    @Override
    public int delete(List<Long> ids) {
        int count = roleMapper.deleteBatchIds(ids);
        adminCacheService.delResourceListByRoleIds(ids);
        return count;
    }

    @Override
    public List<UmsRole> list() {
        return roleMapper.selectList(null);
    }

    @Override
    public List<UmsRole> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(keyword)) {
            return roleMapper.selectList(new LambdaQueryWrapper<UmsRole>().like(UmsRole::getName, keyword));
        }
        return roleMapper.selectList(null);
    }

    @Override
    public List<UmsMenu> getMenuList(Long adminId) {
        return roleDao.getMenuList(adminId);
    }

    @Override
    public List<UmsMenu> getRoleRelatedMenu(Long roleId) {
        return roleDao.getMenuListByRoleId(roleId);
    }

    @Override
    public List<UmsResource> getRoleRelatedResource(Long roleId) {
        return roleDao.getResourceListByRoleId(roleId);
    }

    @Override
    public int allocateMenu2Role(Long roleId, List<Long> menuIds) {
        // todo 事务
        // 删除旧的关系
        roleMenuRelationMapper.delete(new LambdaQueryWrapper<UmsRoleMenuRelation>().eq(UmsRoleMenuRelation::getRoleId, roleId));

        //批量插入新关系
        for (Long menuId : menuIds) {
            UmsRoleMenuRelation relation = new UmsRoleMenuRelation();
            relation.setRoleId(roleId);
            relation.setMenuId(menuId);
            roleMenuRelationMapper.insert(relation);
        }
        return menuIds.size();
    }

    @Override
    public int allocateResource2Role(Long roleId, List<Long> resourceIds) {
        // todo 事务
        //先删除原有关系
        roleResourceRelationMapper.delete(new LambdaQueryWrapper<UmsRoleResourceRelation>().eq(UmsRoleResourceRelation::getRoleId, roleId));
        //批量插入新关系
        for (Long resourceId : resourceIds) {
            UmsRoleResourceRelation relation = new UmsRoleResourceRelation();
            relation.setRoleId(roleId);
            relation.setResourceId(resourceId);
            roleResourceRelationMapper.insert(relation);
        }
        adminCacheService.delResourceListByRole(roleId);
        return resourceIds.size();
    }
}
