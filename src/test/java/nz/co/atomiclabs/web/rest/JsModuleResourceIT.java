package nz.co.atomiclabs.web.rest;

import nz.co.atomiclabs.Content2App;
import nz.co.atomiclabs.config.TestSecurityConfiguration;
import nz.co.atomiclabs.domain.JsModule;
import nz.co.atomiclabs.repository.JsModuleRepository;
import nz.co.atomiclabs.service.JsModuleService;
import nz.co.atomiclabs.service.dto.JsModuleDTO;
import nz.co.atomiclabs.service.mapper.JsModuleMapper;
import nz.co.atomiclabs.web.rest.errors.ExceptionTranslator;
import nz.co.atomiclabs.service.dto.JsModuleCriteria;
import nz.co.atomiclabs.service.JsModuleQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static nz.co.atomiclabs.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link JsModuleResource} REST controller.
 */
@SpringBootTest(classes = {Content2App.class, TestSecurityConfiguration.class})
public class JsModuleResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SOURCE = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_AUTO_LOAD = false;
    private static final Boolean UPDATED_AUTO_LOAD = true;

    @Autowired
    private JsModuleRepository jsModuleRepository;

    @Autowired
    private JsModuleMapper jsModuleMapper;

    @Autowired
    private JsModuleService jsModuleService;

    @Autowired
    private JsModuleQueryService jsModuleQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restJsModuleMockMvc;

    private JsModule jsModule;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final JsModuleResource jsModuleResource = new JsModuleResource(jsModuleService, jsModuleQueryService);
        this.restJsModuleMockMvc = MockMvcBuilders.standaloneSetup(jsModuleResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JsModule createEntity(EntityManager em) {
        JsModule jsModule = new JsModule()
            .name(DEFAULT_NAME)
            .source(DEFAULT_SOURCE)
            .autoLoad(DEFAULT_AUTO_LOAD);
        return jsModule;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JsModule createUpdatedEntity(EntityManager em) {
        JsModule jsModule = new JsModule()
            .name(UPDATED_NAME)
            .source(UPDATED_SOURCE)
            .autoLoad(UPDATED_AUTO_LOAD);
        return jsModule;
    }

    @BeforeEach
    public void initTest() {
        jsModule = createEntity(em);
    }

    @Test
    @Transactional
    public void createJsModule() throws Exception {
        int databaseSizeBeforeCreate = jsModuleRepository.findAll().size();

        // Create the JsModule
        JsModuleDTO jsModuleDTO = jsModuleMapper.toDto(jsModule);
        restJsModuleMockMvc.perform(post("/api/js-modules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jsModuleDTO)))
            .andExpect(status().isCreated());

        // Validate the JsModule in the database
        List<JsModule> jsModuleList = jsModuleRepository.findAll();
        assertThat(jsModuleList).hasSize(databaseSizeBeforeCreate + 1);
        JsModule testJsModule = jsModuleList.get(jsModuleList.size() - 1);
        assertThat(testJsModule.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testJsModule.getSource()).isEqualTo(DEFAULT_SOURCE);
        assertThat(testJsModule.isAutoLoad()).isEqualTo(DEFAULT_AUTO_LOAD);
    }

    @Test
    @Transactional
    public void createJsModuleWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jsModuleRepository.findAll().size();

        // Create the JsModule with an existing ID
        jsModule.setId(1L);
        JsModuleDTO jsModuleDTO = jsModuleMapper.toDto(jsModule);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJsModuleMockMvc.perform(post("/api/js-modules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jsModuleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the JsModule in the database
        List<JsModule> jsModuleList = jsModuleRepository.findAll();
        assertThat(jsModuleList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = jsModuleRepository.findAll().size();
        // set the field null
        jsModule.setName(null);

        // Create the JsModule, which fails.
        JsModuleDTO jsModuleDTO = jsModuleMapper.toDto(jsModule);

        restJsModuleMockMvc.perform(post("/api/js-modules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jsModuleDTO)))
            .andExpect(status().isBadRequest());

        List<JsModule> jsModuleList = jsModuleRepository.findAll();
        assertThat(jsModuleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkSourceIsRequired() throws Exception {
        int databaseSizeBeforeTest = jsModuleRepository.findAll().size();
        // set the field null
        jsModule.setSource(null);

        // Create the JsModule, which fails.
        JsModuleDTO jsModuleDTO = jsModuleMapper.toDto(jsModule);

        restJsModuleMockMvc.perform(post("/api/js-modules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jsModuleDTO)))
            .andExpect(status().isBadRequest());

        List<JsModule> jsModuleList = jsModuleRepository.findAll();
        assertThat(jsModuleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAutoLoadIsRequired() throws Exception {
        int databaseSizeBeforeTest = jsModuleRepository.findAll().size();
        // set the field null
        jsModule.setAutoLoad(null);

        // Create the JsModule, which fails.
        JsModuleDTO jsModuleDTO = jsModuleMapper.toDto(jsModule);

        restJsModuleMockMvc.perform(post("/api/js-modules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jsModuleDTO)))
            .andExpect(status().isBadRequest());

        List<JsModule> jsModuleList = jsModuleRepository.findAll();
        assertThat(jsModuleList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllJsModules() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get all the jsModuleList
        restJsModuleMockMvc.perform(get("/api/js-modules?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jsModule.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())))
            .andExpect(jsonPath("$.[*].autoLoad").value(hasItem(DEFAULT_AUTO_LOAD.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getJsModule() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get the jsModule
        restJsModuleMockMvc.perform(get("/api/js-modules/{id}", jsModule.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jsModule.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE.toString()))
            .andExpect(jsonPath("$.autoLoad").value(DEFAULT_AUTO_LOAD.booleanValue()));
    }

    @Test
    @Transactional
    public void getAllJsModulesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get all the jsModuleList where name equals to DEFAULT_NAME
        defaultJsModuleShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the jsModuleList where name equals to UPDATED_NAME
        defaultJsModuleShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllJsModulesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get all the jsModuleList where name in DEFAULT_NAME or UPDATED_NAME
        defaultJsModuleShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the jsModuleList where name equals to UPDATED_NAME
        defaultJsModuleShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllJsModulesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get all the jsModuleList where name is not null
        defaultJsModuleShouldBeFound("name.specified=true");

        // Get all the jsModuleList where name is null
        defaultJsModuleShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllJsModulesBySourceIsEqualToSomething() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get all the jsModuleList where source equals to DEFAULT_SOURCE
        defaultJsModuleShouldBeFound("source.equals=" + DEFAULT_SOURCE);

        // Get all the jsModuleList where source equals to UPDATED_SOURCE
        defaultJsModuleShouldNotBeFound("source.equals=" + UPDATED_SOURCE);
    }

    @Test
    @Transactional
    public void getAllJsModulesBySourceIsInShouldWork() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get all the jsModuleList where source in DEFAULT_SOURCE or UPDATED_SOURCE
        defaultJsModuleShouldBeFound("source.in=" + DEFAULT_SOURCE + "," + UPDATED_SOURCE);

        // Get all the jsModuleList where source equals to UPDATED_SOURCE
        defaultJsModuleShouldNotBeFound("source.in=" + UPDATED_SOURCE);
    }

    @Test
    @Transactional
    public void getAllJsModulesBySourceIsNullOrNotNull() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get all the jsModuleList where source is not null
        defaultJsModuleShouldBeFound("source.specified=true");

        // Get all the jsModuleList where source is null
        defaultJsModuleShouldNotBeFound("source.specified=false");
    }

    @Test
    @Transactional
    public void getAllJsModulesByAutoLoadIsEqualToSomething() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get all the jsModuleList where autoLoad equals to DEFAULT_AUTO_LOAD
        defaultJsModuleShouldBeFound("autoLoad.equals=" + DEFAULT_AUTO_LOAD);

        // Get all the jsModuleList where autoLoad equals to UPDATED_AUTO_LOAD
        defaultJsModuleShouldNotBeFound("autoLoad.equals=" + UPDATED_AUTO_LOAD);
    }

    @Test
    @Transactional
    public void getAllJsModulesByAutoLoadIsInShouldWork() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get all the jsModuleList where autoLoad in DEFAULT_AUTO_LOAD or UPDATED_AUTO_LOAD
        defaultJsModuleShouldBeFound("autoLoad.in=" + DEFAULT_AUTO_LOAD + "," + UPDATED_AUTO_LOAD);

        // Get all the jsModuleList where autoLoad equals to UPDATED_AUTO_LOAD
        defaultJsModuleShouldNotBeFound("autoLoad.in=" + UPDATED_AUTO_LOAD);
    }

    @Test
    @Transactional
    public void getAllJsModulesByAutoLoadIsNullOrNotNull() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        // Get all the jsModuleList where autoLoad is not null
        defaultJsModuleShouldBeFound("autoLoad.specified=true");

        // Get all the jsModuleList where autoLoad is null
        defaultJsModuleShouldNotBeFound("autoLoad.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultJsModuleShouldBeFound(String filter) throws Exception {
        restJsModuleMockMvc.perform(get("/api/js-modules?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jsModule.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
            .andExpect(jsonPath("$.[*].autoLoad").value(hasItem(DEFAULT_AUTO_LOAD.booleanValue())));

        // Check, that the count call also returns 1
        restJsModuleMockMvc.perform(get("/api/js-modules/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultJsModuleShouldNotBeFound(String filter) throws Exception {
        restJsModuleMockMvc.perform(get("/api/js-modules?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restJsModuleMockMvc.perform(get("/api/js-modules/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingJsModule() throws Exception {
        // Get the jsModule
        restJsModuleMockMvc.perform(get("/api/js-modules/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJsModule() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        int databaseSizeBeforeUpdate = jsModuleRepository.findAll().size();

        // Update the jsModule
        JsModule updatedJsModule = jsModuleRepository.findById(jsModule.getId()).get();
        // Disconnect from session so that the updates on updatedJsModule are not directly saved in db
        em.detach(updatedJsModule);
        updatedJsModule
            .name(UPDATED_NAME)
            .source(UPDATED_SOURCE)
            .autoLoad(UPDATED_AUTO_LOAD);
        JsModuleDTO jsModuleDTO = jsModuleMapper.toDto(updatedJsModule);

        restJsModuleMockMvc.perform(put("/api/js-modules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jsModuleDTO)))
            .andExpect(status().isOk());

        // Validate the JsModule in the database
        List<JsModule> jsModuleList = jsModuleRepository.findAll();
        assertThat(jsModuleList).hasSize(databaseSizeBeforeUpdate);
        JsModule testJsModule = jsModuleList.get(jsModuleList.size() - 1);
        assertThat(testJsModule.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testJsModule.getSource()).isEqualTo(UPDATED_SOURCE);
        assertThat(testJsModule.isAutoLoad()).isEqualTo(UPDATED_AUTO_LOAD);
    }

    @Test
    @Transactional
    public void updateNonExistingJsModule() throws Exception {
        int databaseSizeBeforeUpdate = jsModuleRepository.findAll().size();

        // Create the JsModule
        JsModuleDTO jsModuleDTO = jsModuleMapper.toDto(jsModule);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJsModuleMockMvc.perform(put("/api/js-modules")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jsModuleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the JsModule in the database
        List<JsModule> jsModuleList = jsModuleRepository.findAll();
        assertThat(jsModuleList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteJsModule() throws Exception {
        // Initialize the database
        jsModuleRepository.saveAndFlush(jsModule);

        int databaseSizeBeforeDelete = jsModuleRepository.findAll().size();

        // Delete the jsModule
        restJsModuleMockMvc.perform(delete("/api/js-modules/{id}", jsModule.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<JsModule> jsModuleList = jsModuleRepository.findAll();
        assertThat(jsModuleList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JsModule.class);
        JsModule jsModule1 = new JsModule();
        jsModule1.setId(1L);
        JsModule jsModule2 = new JsModule();
        jsModule2.setId(jsModule1.getId());
        assertThat(jsModule1).isEqualTo(jsModule2);
        jsModule2.setId(2L);
        assertThat(jsModule1).isNotEqualTo(jsModule2);
        jsModule1.setId(null);
        assertThat(jsModule1).isNotEqualTo(jsModule2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(JsModuleDTO.class);
        JsModuleDTO jsModuleDTO1 = new JsModuleDTO();
        jsModuleDTO1.setId(1L);
        JsModuleDTO jsModuleDTO2 = new JsModuleDTO();
        assertThat(jsModuleDTO1).isNotEqualTo(jsModuleDTO2);
        jsModuleDTO2.setId(jsModuleDTO1.getId());
        assertThat(jsModuleDTO1).isEqualTo(jsModuleDTO2);
        jsModuleDTO2.setId(2L);
        assertThat(jsModuleDTO1).isNotEqualTo(jsModuleDTO2);
        jsModuleDTO1.setId(null);
        assertThat(jsModuleDTO1).isNotEqualTo(jsModuleDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(jsModuleMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(jsModuleMapper.fromId(null)).isNull();
    }
}
