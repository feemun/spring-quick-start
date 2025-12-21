package cloud.catfish.api.converter;

import cloud.catfish.api.domain.XlcSignalDataStatus;
import cloud.catfish.api.vo.XlcSignalDataStatusVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface XlcSignalDataStatusConverter {

    /**
     * Converts a domain model to a VO.
     * 
     * @param xlcsignaldatastatus the domain model to convert
     * @return the corresponding VO, or null if input is null
     */
    XlcSignalDataStatusVO toVo(XlcSignalDataStatus xlcsignaldatastatus);

    /**
     * Converts a VO to a domain model.
     * 
     * @param xlcsignaldatastatusvo the VO to convert
     * @return the corresponding domain model, or null if input is null
     */
    XlcSignalDataStatus toDomain(XlcSignalDataStatusVO xlcsignaldatastatusvo);

    /**
     * Converts a list of domain models to VOs.
     * 
     * @param xlcsignaldatastatuss the list of domain models to convert
     * @return the list of corresponding VOs, or null if input is null
     */
    List<XlcSignalDataStatusVO> toVoList(List<XlcSignalDataStatus> xlcsignaldatastatuss);

    /**
     * Converts a list of VOs to domain models.
     * 
     * @param xlcsignaldatastatusvos the list of VOs to convert
     * @return the list of corresponding domain models, or null if input is null
     */
    List<XlcSignalDataStatus> toDomainList(List<XlcSignalDataStatusVO> xlcsignaldatastatusvos);
}
