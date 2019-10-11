package nz.co.atomiclabs.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import nz.co.atomiclabs.domain.JsModule;
import nz.co.atomiclabs.domain.*; // for static metamodels
import nz.co.atomiclabs.repository.JsModuleRepository;
import nz.co.atomiclabs.service.dto.JsModuleCriteria;
import nz.co.atomiclabs.service.dto.JsModuleDTO;
import nz.co.atomiclabs.service.mapper.JsModuleMapper;

/**
 * Service for executing complex queries for {@link JsModule} entities in the database.
 * The main input is a {@link JsModuleCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link JsModuleDTO} or a {@link Page} of {@link JsModuleDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class JsModuleQueryService extends QueryService<JsModule> {

    private final Logger log = LoggerFactory.getLogger(JsModuleQueryService.class);

    private final JsModuleRepository jsModuleRepository;

    private final JsModuleMapper jsModuleMapper;

    public JsModuleQueryService(JsModuleRepository jsModuleRepository, JsModuleMapper jsModuleMapper) {
        this.jsModuleRepository = jsModuleRepository;
        this.jsModuleMapper = jsModuleMapper;
    }

    /**
     * Return a {@link List} of {@link JsModuleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<JsModuleDTO> findByCriteria(JsModuleCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<JsModule> specification = createSpecification(criteria);
        return jsModuleMapper.toDto(jsModuleRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link JsModuleDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JsModuleDTO> findByCriteria(JsModuleCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<JsModule> specification = createSpecification(criteria);
        return jsModuleRepository.findAll(specification, page)
            .map(jsModuleMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(JsModuleCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<JsModule> specification = createSpecification(criteria);
        return jsModuleRepository.count(specification);
    }

    /**
     * Function to convert ConsumerCriteria to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<JsModule> createSpecification(JsModuleCriteria criteria) {
        Specification<JsModule> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), JsModule_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), JsModule_.name));
            }
            if (criteria.getSource() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSource(), JsModule_.source));
            }
            if (criteria.getAutoLoad() != null) {
                specification = specification.and(buildSpecification(criteria.getAutoLoad(), JsModule_.autoLoad));
            }
        }
        return specification;
    }
}
