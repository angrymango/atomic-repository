package nz.co.atomiclabs.service;

import nz.co.atomiclabs.domain.JsModule;
import nz.co.atomiclabs.repository.JsModuleRepository;
import nz.co.atomiclabs.service.dto.JsModuleDTO;
import nz.co.atomiclabs.service.mapper.JsModuleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing {@link JsModule}.
 */
@Service
@Transactional
public class JsModuleService {

    private final Logger log = LoggerFactory.getLogger(JsModuleService.class);

    private final JsModuleRepository jsModuleRepository;

    private final JsModuleMapper jsModuleMapper;

    public JsModuleService(JsModuleRepository jsModuleRepository, JsModuleMapper jsModuleMapper) {
        this.jsModuleRepository = jsModuleRepository;
        this.jsModuleMapper = jsModuleMapper;
    }

    /**
     * Save a jsModule.
     *
     * @param jsModuleDTO the entity to save.
     * @return the persisted entity.
     */
    public JsModuleDTO save(JsModuleDTO jsModuleDTO) {
        log.debug("Request to save JsModule : {}", jsModuleDTO);
        JsModule jsModule = jsModuleMapper.toEntity(jsModuleDTO);
        jsModule = jsModuleRepository.save(jsModule);
        return jsModuleMapper.toDto(jsModule);
    }

    /**
     * Get all the jsModules.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<JsModuleDTO> findAll(Pageable pageable) {
        log.debug("Request to get all JsModules");
        return jsModuleRepository.findAll(pageable)
            .map(jsModuleMapper::toDto);
    }


    /**
     * Get one jsModule by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<JsModuleDTO> findOne(Long id) {
        log.debug("Request to get JsModule : {}", id);
        return jsModuleRepository.findById(id)
            .map(jsModuleMapper::toDto);
    }

    /**
     * Delete the jsModule by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete JsModule : {}", id);
        jsModuleRepository.deleteById(id);
    }
}
