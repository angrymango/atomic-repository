package nz.co.atomiclabs.service.mapper;

import nz.co.atomiclabs.domain.*;
import nz.co.atomiclabs.service.dto.JsModuleDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link JsModule} and its DTO {@link JsModuleDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface JsModuleMapper extends EntityMapper<JsModuleDTO, JsModule> {



    default JsModule fromId(Long id) {
        if (id == null) {
            return null;
        }
        JsModule jsModule = new JsModule();
        jsModule.setId(id);
        return jsModule;
    }
}
