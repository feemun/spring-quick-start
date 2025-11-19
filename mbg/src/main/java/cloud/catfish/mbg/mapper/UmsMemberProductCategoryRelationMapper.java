package cloud.catfish.mbg.mapper;

import domain.UmsMemberProductCategoryRelation;
import domain.UmsMemberProductCategoryRelationExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UmsMemberProductCategoryRelationMapper {
    long countByExample(UmsMemberProductCategoryRelationExample example);

    int deleteByExample(UmsMemberProductCategoryRelationExample example);

    int deleteByPrimaryKey(Long id);

    int insert(UmsMemberProductCategoryRelation row);

    int insertSelective(UmsMemberProductCategoryRelation row);

    List<UmsMemberProductCategoryRelation> selectByExample(UmsMemberProductCategoryRelationExample example);

    UmsMemberProductCategoryRelation selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") UmsMemberProductCategoryRelation row, @Param("example") UmsMemberProductCategoryRelationExample example);

    int updateByExample(@Param("row") UmsMemberProductCategoryRelation row, @Param("example") UmsMemberProductCategoryRelationExample example);

    int updateByPrimaryKeySelective(UmsMemberProductCategoryRelation row);

    int updateByPrimaryKey(UmsMemberProductCategoryRelation row);
}