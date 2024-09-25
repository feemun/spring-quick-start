package cloud.catfish.mbg.mapper;

import cloud.catfish.mbg.model.DeviceConnection;
import cloud.catfish.mbg.model.DeviceConnectionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DeviceConnectionMapper {
    long countByExample(DeviceConnectionExample example);

    int deleteByExample(DeviceConnectionExample example);

    int insert(DeviceConnection row);

    int insertSelective(DeviceConnection row);

    List<DeviceConnection> selectByExample(DeviceConnectionExample example);

    int updateByExampleSelective(@Param("row") DeviceConnection row, @Param("example") DeviceConnectionExample example);

    int updateByExample(@Param("row") DeviceConnection row, @Param("example") DeviceConnectionExample example);
}