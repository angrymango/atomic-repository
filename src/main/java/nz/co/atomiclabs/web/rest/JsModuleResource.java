package nz.co.atomiclabs.web.rest;

import nz.co.atomiclabs.service.JsModuleService;
import nz.co.atomiclabs.web.rest.errors.BadRequestAlertException;
import nz.co.atomiclabs.service.dto.JsModuleDTO;
import nz.co.atomiclabs.service.dto.JsModuleCriteria;
import nz.co.atomiclabs.service.JsModuleQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link nz.co.atomiclabs.domain.JsModule}.
 */
@RestController
@RequestMapping("/api")
public class JsModuleResource {

    private final Logger log = LoggerFactory.getLogger(JsModuleResource.class);

    private static final String ENTITY_NAME = "content2JsModule";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final JsModuleService jsModuleService;

    private final JsModuleQueryService jsModuleQueryService;

    public JsModuleResource(JsModuleService jsModuleService, JsModuleQueryService jsModuleQueryService) {
        this.jsModuleService = jsModuleService;
        this.jsModuleQueryService = jsModuleQueryService;
    }

    /**
     * {@code POST  /js-modules} : Create a new jsModule.
     *
     * @param jsModuleDTO the jsModuleDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new jsModuleDTO, or with status {@code 400 (Bad Request)} if the jsModule has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/js-modules")
    public ResponseEntity<JsModuleDTO> createJsModule(@Valid @RequestBody JsModuleDTO jsModuleDTO) throws URISyntaxException {
        log.debug("REST request to save JsModule : {}", jsModuleDTO);
        if (jsModuleDTO.getId() != null) {
            throw new BadRequestAlertException("A new jsModule cannot already have an ID", ENTITY_NAME, "idexists");
        }
        JsModuleDTO result = jsModuleService.save(jsModuleDTO);
        return ResponseEntity.created(new URI("/api/js-modules/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /js-modules} : Updates an existing jsModule.
     *
     * @param jsModuleDTO the jsModuleDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated jsModuleDTO,
     * or with status {@code 400 (Bad Request)} if the jsModuleDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the jsModuleDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/js-modules")
    public ResponseEntity<JsModuleDTO> updateJsModule(@Valid @RequestBody JsModuleDTO jsModuleDTO) throws URISyntaxException {
        log.debug("REST request to update JsModule : {}", jsModuleDTO);
        if (jsModuleDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        JsModuleDTO result = jsModuleService.save(jsModuleDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, jsModuleDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /js-modules} : get all the jsModules.
     *

     * @param pageable the pagination information.

     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of jsModules in body.
     */
    @GetMapping("/js-modules")
    public ResponseEntity<List<JsModuleDTO>> getAllJsModules(JsModuleCriteria criteria, Pageable pageable) {
        log.debug("REST request to get JsModules by criteria: {}", criteria);
        Page<JsModuleDTO> page = jsModuleQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
    * {@code GET  /js-modules/count} : count all the jsModules.
    *
    * @param criteria the criteria which the requested entities should match.
    * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
    */
    @GetMapping("/js-modules/count")
    public ResponseEntity<Long> countJsModules(JsModuleCriteria criteria) {
        log.debug("REST request to count JsModules by criteria: {}", criteria);
        return ResponseEntity.ok().body(jsModuleQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /js-modules/:id} : get the "id" jsModule.
     *
     * @param id the id of the jsModuleDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the jsModuleDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/js-modules/{id}")
    public ResponseEntity<JsModuleDTO> getJsModule(@PathVariable Long id) {
        log.debug("REST request to get JsModule : {}", id);
        Optional<JsModuleDTO> jsModuleDTO = jsModuleService.findOne(id);
        return ResponseUtil.wrapOrNotFound(jsModuleDTO);
    }

    /**
     * {@code DELETE  /js-modules/:id} : delete the "id" jsModule.
     *
     * @param id the id of the jsModuleDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/js-modules/{id}")
    public ResponseEntity<Void> deleteJsModule(@PathVariable Long id) {
        log.debug("REST request to delete JsModule : {}", id);
        jsModuleService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
