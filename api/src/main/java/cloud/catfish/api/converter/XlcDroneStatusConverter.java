package cloud.catfish.api.converter;

import cloud.catfish.api.domain.XlcDroneStatus;
import cloud.catfish.api.vo.XlcDroneStatusVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface XlcDroneStatusConverter {

    /**
     * Converts a domain model to a VO.
     * 
     * @param xlcdronestatus the domain model to convert
     * @return the corresponding VO, or null if input is null
     */
    XlcDroneStatusVO toVo(XlcDroneStatus xlcdronestatus);

    /**
     * Converts a VO to a domain model.
     * 
     * @param xlcdronestatusvo the VO to convert
     * @return the corresponding domain model, or null if input is null
     */
    XlcDroneStatus toDomain(XlcDroneStatusVO xlcdronestatusvo);

    /**
     * Converts a list of domain models to VOs.
     * 
     * @param xlcdronestatuss the list of domain models to convert
     * @return the list of corresponding VOs, or null if input is null
     */
    List<XlcDroneStatusVO> toVoList(List<XlcDroneStatus> xlcdronestatuss);

    /**
     * Converts a list of VOs to domain models.
     * 
     * @param xlcdronestatusvos the list of VOs to convert
     * @return the list of corresponding domain models, or null if input is null
     */
    List<XlcDroneStatus> toDomainList(List<XlcDroneStatusVO> xlcdronestatusvos);
}
