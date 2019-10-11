package nz.co.atomiclabs.web.rest;

import io.github.jhipster.web.util.ResponseUtil;
import nz.co.atomiclabs.service.NodeRevisionService;
import nz.co.atomiclabs.service.dto.NodeDTO;
import nz.co.atomiclabs.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.history.Revision;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Node revisions.
 */
@RestController
@RequestMapping("/api")
public class NodeRevisionResource {
    private final Logger log = LoggerFactory.getLogger(NodeRevisionResource.class);

    private final NodeRevisionService nodeRevisionService;

    public NodeRevisionResource(NodeRevisionService nodeRevisionService) {
        this.nodeRevisionService = nodeRevisionService;
    }

    /**
     * GET  /nodes/:id/revisions/latest : get the "id" node.
     *
     * @param id the id of the nodeDTO revision to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the nodeDTO revision, or with status 404 (Not Found)
     */
    @GetMapping("/nodes/{id}/revisions/latest")
    public ResponseEntity<Revision<Long, NodeDTO>> getNodeLatestRevision(@PathVariable Long id) {
        log.debug("REST request to get Node latest revision : {}", id);
        Optional<Revision<Long, NodeDTO>> revision = nodeRevisionService.findLastChangeRevision(id);
        return ResponseUtil.wrapOrNotFound(revision);
    }

    /**
     * GET  /nodes/:id/revisions : get the "id" node.
     *
     * @param id the id of the nodeDTO revision to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the nodeDTO revisions, or with status 404 (Not Found)
     */
    @GetMapping("/nodes/{id}/revisions")
    public ResponseEntity<List<Revision<Long, NodeDTO>>> getNodeRevisions(@PathVariable Long id, Pageable pageable) {
        log.debug("REST request to get Node : {}", id);
        Page<Revision<Long, NodeDTO>> page = nodeRevisionService.findRevisions(id, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, String.format("/nodes/%s/revisions", id));
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

