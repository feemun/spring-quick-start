package cloud.catfish.admin.service.impl;

import cloud.catfish.admin.service.UmsAdminCacheService;
import cloud.catfish.admin.service.UmsResourceService;
import cloud.catfish.mbg.mapper.UmsResourceMapper;
import cloud.catfish.api.domain.UmsResource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台资源管理Service实现类
 * Created by macro on 2020/2/2.
 */
@Service
public class UmsResourceServiceImpl implements UmsResourceService {
    @Autowired
    private UmsResourceMapper resourceMapper;
    @Autowired
    private UmsAdminCacheService adminCacheService;
    @Override
    public int create(UmsResource umsResource) {
        umsResource.setCreateTime(LocalDateTime.now());
        return resourceMapper.insert(umsResource);
    }

    @Override
    public int update(Long id, UmsResource umsResource) {
        umsResource.setId(id);
        int count = resourceMapper.updateById(umsResource);
        adminCacheService.delResourceListByResource(id);
        return count;
    }

    @Override
    public UmsResource getItem(Long id) {
        return resourceMapper.selectById(id);
    }

    @Override
    public int delete(Long id) {
        int count = resourceMapper.deleteById(id);
        adminCacheService.delResourceListByResource(id);
        return count;
    }

    @Override
    public List<UmsResource> list(Long categoryId, String nameKeyword, String urlKeyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        LambdaQueryWrapper<UmsResource> query = new LambdaQueryWrapper<>();
        if(categoryId!=null){
            query.eq(UmsResource::getCategoryId, categoryId);
        }
        if(StrUtil.isNotEmpty(nameKeyword)){
            query.like(UmsResource::getName, nameKeyword);
        }
        if(StrUtil.isNotEmpty(urlKeyword)){
            query.like(UmsResource::getUrl, urlKeyword);
        }
        return resourceMapper.selectList(query);
    }

    @Override
    public List<UmsResource> listAll() {
        return resourceMapper.selectList(null);
    }
}
