package cloud.catfish.mbg.mapper;

import cloud.catfish.api.domain.XlcSignalDataStatus;
import cloud.catfish.api.domain.XlcSignalDataStatusExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XlcSignalDataStatusMapper {
    long countByExample(XlcSignalDataStatusExample example);

    int deleteByExample(XlcSignalDataStatusExample example);

    int deleteByPrimaryKey(Long uavPort);

    int insert(XlcSignalDataStatus row);

    int insertSelective(XlcSignalDataStatus row);

    List<XlcSignalDataStatus> selectByExample(XlcSignalDataStatusExample example);

    XlcSignalDataStatus selectByPrimaryKey(Long uavPort);

    int updateByExampleSelective(@Param("row") XlcSignalDataStatus row, @Param("example") XlcSignalDataStatusExample example);

    int updateByExample(@Param("row") XlcSignalDataStatus row, @Param("example") XlcSignalDataStatusExample example);

    int updateByPrimaryKeySelective(XlcSignalDataStatus row);

    int updateByPrimaryKey(XlcSignalDataStatus row);

    /**
     * 批量插入记录
     * @param records 要插入的记录列表
     * @return 插入的记录数
     */
    int batchInsert(List<XlcSignalDataStatus> records);
}