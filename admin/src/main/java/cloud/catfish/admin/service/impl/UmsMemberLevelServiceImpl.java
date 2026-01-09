package cloud.catfish.admin.service.impl;

import cloud.catfish.admin.service.UmsMemberLevelService;
import cloud.catfish.mbg.mapper.UmsMemberLevelMapper;
import cloud.catfish.api.domain.UmsMemberLevel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员等级管理Service实现类
 * Created by macro on 2018/4/26.
 */
@Service
public class UmsMemberLevelServiceImpl implements UmsMemberLevelService {
    @Autowired
    private UmsMemberLevelMapper memberLevelMapper;
    @Override
    public List<UmsMemberLevel> list(Integer defaultStatus) {
        return memberLevelMapper.selectList(new LambdaQueryWrapper<UmsMemberLevel>().eq(UmsMemberLevel::getDefaultStatus, defaultStatus));
    }
}
