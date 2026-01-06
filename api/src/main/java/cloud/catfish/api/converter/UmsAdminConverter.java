package cloud.catfish.api.converter;

import cloud.catfish.api.domain.UmsAdmin;
import cloud.catfish.api.req.UmsAdminParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UmsAdminConverter {

    UmsAdmin param2Entity(UmsAdminParam umsAdminParam);

}
