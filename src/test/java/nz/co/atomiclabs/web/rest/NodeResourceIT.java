package nz.co.atomiclabs.web.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.atomiclabs.Content2App;
import nz.co.atomiclabs.config.TestSecurityConfiguration;
import nz.co.atomiclabs.domain.Node;
import nz.co.atomiclabs.domain.Node;
import nz.co.atomiclabs.repository.NodeRepository;
import nz.co.atomiclabs.service.NodeService;
import nz.co.atomiclabs.service.dto.NodeDTO;
import nz.co.atomiclabs.service.mapper.NodeMapper;
import nz.co.atomiclabs.web.rest.errors.ExceptionTranslator;
import nz.co.atomiclabs.service.dto.NodeCriteria;
import nz.co.atomiclabs.service.NodeQueryService;

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
 * Integration tests for the {@link NodeResource} REST controller.
 */
@SpringBootTest(classes = {Content2App.class, TestSecurityConfiguration.class})
public class NodeResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static JsonNode DEFAULT_CONTENT;
    private static JsonNode UPDATED_CONTENT;

    static {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            DEFAULT_CONTENT = objectMapper.readValue("{\"data\": \"AAAAAAAAAA\"}", JsonNode.class);
            UPDATED_CONTENT = objectMapper.readValue("{\"data\": \"BBBBBBBBBB\"}", JsonNode.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String DEFAULT_SEARCH = "AAAAAAAAAA";
    private static final String UPDATED_SEARCH = "BBBBBBBBBB";

    private static final String DEFAULT_PARENT_PATH = "AAAAAAAAAA";
    private static final String UPDATED_PARENT_PATH = "BBBBBBBBBB";

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private NodeMapper nodeMapper;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private NodeQueryService nodeQueryService;

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

    private MockMvc restNodeMockMvc;

    private Node node;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final NodeResource nodeResource = new NodeResource(nodeService, nodeQueryService);
        this.restNodeMockMvc = MockMvcBuilders.standaloneSetup(nodeResource)
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
    public static Node createEntity(EntityManager em) {
        Node node = new Node()
            .name(DEFAULT_NAME)
            .content(DEFAULT_CONTENT);
        return node;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Node createUpdatedEntity(EntityManager em) {
        Node node = new Node()
            .name(UPDATED_NAME)
            .content(UPDATED_CONTENT);
        return node;
    }

    @BeforeEach
    public void initTest() {
        node = createEntity(em);
    }

    @Test
    @Transactional
    public void createNode() throws Exception {
        int databaseSizeBeforeCreate = nodeRepository.findAll().size();

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);
        restNodeMockMvc.perform(post("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(nodeDTO)))
            .andExpect(status().isCreated());

        // Validate the Node in the database
        List<Node> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeCreate + 1);
        Node testNode = nodeList.get(nodeList.size() - 1);
        assertThat(testNode.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testNode.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    public void createNodeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = nodeRepository.findAll().size();

        // Create the Node with an existing ID
        node.setId(1L);
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNodeMockMvc.perform(post("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(nodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        List<Node> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = nodeRepository.findAll().size();
        // set the field null
        node.setName(null);

        // Create the Node, which fails.
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        restNodeMockMvc.perform(post("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(nodeDTO)))
            .andExpect(status().isBadRequest());

        List<Node> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllNodes() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList
        restNodeMockMvc.perform(get("/api/nodes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(node.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())));
    }
    
    @Test
    @Transactional
    public void getNode() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get the node
        restNodeMockMvc.perform(get("/api/nodes/{id}", node.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(node.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()));
    }

    @Test
    @Transactional
    public void getAllNodesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where name equals to DEFAULT_NAME
        defaultNodeShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the nodeList where name equals to UPDATED_NAME
        defaultNodeShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllNodesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where name in DEFAULT_NAME or UPDATED_NAME
        defaultNodeShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the nodeList where name equals to UPDATED_NAME
        defaultNodeShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllNodesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where name is not null
        defaultNodeShouldBeFound("name.specified=true");

        // Get all the nodeList where name is null
        defaultNodeShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllNodesByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where content equals to DEFAULT_CONTENT
        defaultNodeShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the nodeList where content equals to UPDATED_CONTENT
        defaultNodeShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllNodesByContentIsInShouldWork() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultNodeShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the nodeList where content equals to UPDATED_CONTENT
        defaultNodeShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void getAllNodesByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where content is not null
        defaultNodeShouldBeFound("content.specified=true");

        // Get all the nodeList where content is null
        defaultNodeShouldNotBeFound("content.specified=false");
    }

    @Test
    @Transactional
    public void getAllNodesBySearchIsEqualToSomething() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where search equals to DEFAULT_SEARCH
        defaultNodeShouldBeFound("search.equals=" + DEFAULT_SEARCH);

        // Get all the nodeList where search equals to UPDATED_SEARCH
        defaultNodeShouldNotBeFound("search.equals=" + UPDATED_SEARCH);
    }

    @Test
    @Transactional
    public void getAllNodesBySearchIsInShouldWork() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where search in DEFAULT_SEARCH or UPDATED_SEARCH
        defaultNodeShouldBeFound("search.in=" + DEFAULT_SEARCH + "," + UPDATED_SEARCH);

        // Get all the nodeList where search equals to UPDATED_SEARCH
        defaultNodeShouldNotBeFound("search.in=" + UPDATED_SEARCH);
    }

    @Test
    @Transactional
    public void getAllNodesBySearchIsNullOrNotNull() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where search is not null
        defaultNodeShouldBeFound("search.specified=true");

        // Get all the nodeList where search is null
        defaultNodeShouldNotBeFound("search.specified=false");
    }

    @Test
    @Transactional
    public void getAllNodesByParentPathIsEqualToSomething() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where parentPath equals to DEFAULT_PARENT_PATH
        defaultNodeShouldBeFound("parentPath.equals=" + DEFAULT_PARENT_PATH);

        // Get all the nodeList where parentPath equals to UPDATED_PARENT_PATH
        defaultNodeShouldNotBeFound("parentPath.equals=" + UPDATED_PARENT_PATH);
    }

    @Test
    @Transactional
    public void getAllNodesByParentPathIsInShouldWork() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where parentPath in DEFAULT_PARENT_PATH or UPDATED_PARENT_PATH
        defaultNodeShouldBeFound("parentPath.in=" + DEFAULT_PARENT_PATH + "," + UPDATED_PARENT_PATH);

        // Get all the nodeList where parentPath equals to UPDATED_PARENT_PATH
        defaultNodeShouldNotBeFound("parentPath.in=" + UPDATED_PARENT_PATH);
    }

    @Test
    @Transactional
    public void getAllNodesByParentPathIsNullOrNotNull() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList where parentPath is not null
        defaultNodeShouldBeFound("parentPath.specified=true");

        // Get all the nodeList where parentPath is null
        defaultNodeShouldNotBeFound("parentPath.specified=false");
    }

    @Test
    @Transactional
    public void getAllNodesByParentIsEqualToSomething() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);
        Node parent = NodeResourceIT.createEntity(em);
        em.persist(parent);
        em.flush();
        node.setParent(parent);
        nodeRepository.saveAndFlush(node);
        Long parentId = parent.getId();

        // Get all the nodeList where parent equals to parentId
        defaultNodeShouldBeFound("parentId.equals=" + parentId);

        // Get all the nodeList where parent equals to parentId + 1
        defaultNodeShouldNotBeFound("parentId.equals=" + (parentId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNodeShouldBeFound(String filter) throws Exception {
        restNodeMockMvc.perform(get("/api/nodes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(node.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));

        // Check, that the count call also returns 1
        restNodeMockMvc.perform(get("/api/nodes/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNodeShouldNotBeFound(String filter) throws Exception {
        restNodeMockMvc.perform(get("/api/nodes?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNodeMockMvc.perform(get("/api/nodes/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(content().string("0"));
    }


    @Test
    @Transactional
    public void getNonExistingNode() throws Exception {
        // Get the node
        restNodeMockMvc.perform(get("/api/nodes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNode() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        int databaseSizeBeforeUpdate = nodeRepository.findAll().size();

        // Update the node
        Node updatedNode = nodeRepository.findById(node.getId()).get();
        // Disconnect from session so that the updates on updatedNode are not directly saved in db
        em.detach(updatedNode);
        updatedNode
            .name(UPDATED_NAME)
            .content(UPDATED_CONTENT);
        NodeDTO nodeDTO = nodeMapper.toDto(updatedNode);

        restNodeMockMvc.perform(put("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(nodeDTO)))
            .andExpect(status().isOk());

        // Validate the Node in the database
        List<Node> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
        Node testNode = nodeList.get(nodeList.size() - 1);
        assertThat(testNode.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNode.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    public void updateNonExistingNode() throws Exception {
        int databaseSizeBeforeUpdate = nodeRepository.findAll().size();

        // Create the Node
        NodeDTO nodeDTO = nodeMapper.toDto(node);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNodeMockMvc.perform(put("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(nodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Node in the database
        List<Node> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteNode() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        int databaseSizeBeforeDelete = nodeRepository.findAll().size();

        // Delete the node
        restNodeMockMvc.perform(delete("/api/nodes/{id}", node.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Node> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Node.class);
        Node node1 = new Node();
        node1.setId(1L);
        Node node2 = new Node();
        node2.setId(node1.getId());
        assertThat(node1).isEqualTo(node2);
        node2.setId(2L);
        assertThat(node1).isNotEqualTo(node2);
        node1.setId(null);
        assertThat(node1).isNotEqualTo(node2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(NodeDTO.class);
        NodeDTO nodeDTO1 = new NodeDTO();
        nodeDTO1.setId(1L);
        NodeDTO nodeDTO2 = new NodeDTO();
        assertThat(nodeDTO1).isNotEqualTo(nodeDTO2);
        nodeDTO2.setId(nodeDTO1.getId());
        assertThat(nodeDTO1).isEqualTo(nodeDTO2);
        nodeDTO2.setId(2L);
        assertThat(nodeDTO1).isNotEqualTo(nodeDTO2);
        nodeDTO1.setId(null);
        assertThat(nodeDTO1).isNotEqualTo(nodeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(nodeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(nodeMapper.fromId(null)).isNull();
    }
}
