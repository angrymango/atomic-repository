package nz.co.atomiclabs.service.mapper;

import nz.co.atomiclabs.domain.*;
import nz.co.atomiclabs.service.dto.NodeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Node} and its DTO {@link NodeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface NodeMapper extends EntityMapper<NodeDTO, Node> {

    @Mapping(source = "parent.id", target = "parentId")
    NodeDTO toDto(Node node);

    @Mapping(source = "parentId", target = "parent")
    @Mapping(target = "parentPath", ignore = true)
    @Mapping(target = "search", ignore = true)
    Node toEntity(NodeDTO nodeDTO);

    default Node fromId(Long id) {
        if (id == null) {
            return null;
        }
        Node node = new Node();
        node.setId(id);
        return node;
    }
}
