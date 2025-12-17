package cloud.catfish.mbg.mapper;

import java.util.List;

import cloud.catfish.mbg.example.IpTagRuleExample;
import cloud.catfish.api.domain.IpTagRule;
import org.apache.ibatis.annotations.Param;

public interface IpTagRuleMapper {
    long countByExample(IpTagRuleExample example);

    int deleteByExample(IpTagRuleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(IpTagRule row);

    int insertSelective(IpTagRule row);

    List<IpTagRule> selectByExample(IpTagRuleExample example);

    IpTagRule selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") IpTagRule row, @Param("example") IpTagRuleExample example);

    int updateByExample(@Param("row") IpTagRule row, @Param("example") IpTagRuleExample example);

    int updateByPrimaryKeySelective(IpTagRule row);

    int updateByPrimaryKey(IpTagRule row);

    /**
     * 批量插入记录
     * @param records 要插入的记录列表
     * @return 插入的记录数
     */
    int batchInsert(List<IpTagRule> records);
}