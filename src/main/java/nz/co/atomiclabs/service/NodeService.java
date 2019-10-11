package nz.co.atomiclabs.service;

import nz.co.atomiclabs.domain.Node;
import nz.co.atomiclabs.repository.NodeRepository;
import nz.co.atomiclabs.service.dto.NodeDTO;
import nz.co.atomiclabs.service.mapper.NodeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link Node}.
 */
@Service
@Transactional
public class NodeService {

    private final Logger log = LoggerFactory.getLogger(NodeService.class);

    private final NodeRepository nodeRepository;

    private final NodeMapper nodeMapper;

    public NodeService(NodeRepository nodeRepository, NodeMapper nodeMapper) {
        this.nodeRepository = nodeRepository;
        this.nodeMapper = nodeMapper;
    }

    /**
     * Save a node.
     *
     * @param nodeDTO the entity to save.
     * @return the persisted entity.
     */
    public NodeDTO save(NodeDTO nodeDTO) {
        log.debug("Request to save Node : {}", nodeDTO);
        Node node = nodeMapper.toEntity(nodeDTO);
        node = nodeRepository.save(node);
        return nodeMapper.toDto(node);
    }

    /**
     * Get all the nodes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NodeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Nodes");
        return nodeRepository.findAll(pageable)
            .map(nodeMapper::toDto);
    }


    /**
     * Get one node by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NodeDTO> findOne(Long id) {
        log.debug("Request to get Node : {}", id);
        return nodeRepository.findById(id)
            .map(nodeMapper::toDto);
    }

    /**
     * Delete the node by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Node : {}", id);
        nodeRepository.deleteById(id);
    }
}
