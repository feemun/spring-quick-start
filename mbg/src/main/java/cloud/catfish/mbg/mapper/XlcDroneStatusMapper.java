package cloud.catfish.mbg.mapper;

import cloud.catfish.api.domain.XlcDroneStatus;
import cloud.catfish.api.domain.XlcDroneStatusExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XlcDroneStatusMapper {
    long countByExample(XlcDroneStatusExample example);

    int deleteByExample(XlcDroneStatusExample example);

    int deleteByPrimaryKey(Long uavPort);

    int insert(XlcDroneStatus row);

    int insertSelective(XlcDroneStatus row);

    List<XlcDroneStatus> selectByExample(XlcDroneStatusExample example);

    XlcDroneStatus selectByPrimaryKey(Long uavPort);

    int updateByExampleSelective(@Param("row") XlcDroneStatus row, @Param("example") XlcDroneStatusExample example);

    int updateByExample(@Param("row") XlcDroneStatus row, @Param("example") XlcDroneStatusExample example);

    int updateByPrimaryKeySelective(XlcDroneStatus row);

    int updateByPrimaryKey(XlcDroneStatus row);

    /**
     * 批量插入记录
     * @param records 要插入的记录列表
     * @return 插入的记录数
     */
    int batchInsert(List<XlcDroneStatus> records);
}