package cloud.catfish.api.converter;


import cloud.catfish.api.domain.IpTagRule;
import cloud.catfish.api.vo.IpTagRuleVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface IpTagRuleConverter {

    /**
     * Converts a domain model to a VO.
     * 
     * @param iptagrule the domain model to convert
     * @return the corresponding VO, or null if input is null
     */
    IpTagRuleVO toVo(IpTagRule iptagrule);

    /**
     * Converts a VO to a domain model.
     * 
     * @param iptagrulevo the VO to convert
     * @return the corresponding domain model, or null if input is null
     */
    IpTagRule toDomain(IpTagRuleVO iptagrulevo);

    /**
     * Converts a list of domain models to VOs.
     * 
     * @param iptagrules the list of domain models to convert
     * @return the list of corresponding VOs, or null if input is null
     */
    List<IpTagRuleVO> toVoList(List<IpTagRule> iptagrules);

    /**
     * Converts a list of VOs to domain models.
     * 
     * @param iptagrulevos the list of VOs to convert
     * @return the list of corresponding domain models, or null if input is null
     */
    List<IpTagRule> toDomainList(List<IpTagRuleVO> iptagrulevos);
}
