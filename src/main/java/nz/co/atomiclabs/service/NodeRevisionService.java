package nz.co.atomiclabs.service;

import nz.co.atomiclabs.domain.Node;
import nz.co.atomiclabs.repository.NodeRepository;
import nz.co.atomiclabs.service.dto.NodeDTO;
import nz.co.atomiclabs.service.mapper.NodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class NodeRevisionService extends RevisionService<Node, NodeDTO, Long, Long> {
    public NodeRevisionService(NodeRepository revisionRepository, NodeMapper mapper) {
        super(revisionRepository, mapper);
    }
}
