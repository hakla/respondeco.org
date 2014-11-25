package org.respondeco.respondeco.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.service.ResourcesService;
import org.respondeco.respondeco.testutil.TestUtil;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.List;

import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.repository.ResourceRequirementRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ResourceRequirementResource REST controller.
 *
 * @see ResourceRequirementResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ResourceRequirementResourceTest {
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;
    private static final BigDecimal UPDATED_AMOUNT = BigDecimal.ONE;
    
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";
    
    private static final Long DEFAULT_PROJECT_ID = 0L;
    private static final Long UPDATED_PROJECT_ID = 1L;
    
    private static final Boolean DEFAULT_IS_ESSENTIAL = false;
    private static final Boolean UPDATED_IS_ESSENTIAL = true;
    private static final Long DEFAULT_CREATED_BY = 0L;
    private static final Long UPDATED_CREATED_BY = 1L;
    
   private static final DateTime DEFAULT_CREATED_DATE = new DateTime(0L);
   private static final DateTime UPDATED_CREATED_DATE = new DateTime().withMillisOfSecond(0);
   private static final String DEFAULT_CREATED_DATE_STR = dateTimeFormatter.print(DEFAULT_CREATED_DATE);
    
    private static final Long DEFAULT_LAST_MODIFIED_BY = 0L;
    private static final Long UPDATED_LAST_MODIFIED_BY = 1L;
    
   private static final DateTime DEFAULT_LAST_MODIFIED_DATE = new DateTime(0L);
   private static final DateTime UPDATED_LAST_MODIFIED_DATE = new DateTime().withMillisOfSecond(0);
   private static final String DEFAULT_LAST_MODIFIED_DATE_STR = dateTimeFormatter.print(DEFAULT_LAST_MODIFIED_DATE);
    
    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    @Inject
    private ResourceRequirementRepository resourceRequirementRepository;

    @Inject
    private ResourcesService resourcesService;

    private MockMvc restResourceRequirementMockMvc;

    private ResourceRequirement resourceRequirement;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ResourceController resourceRequirementResource = new ResourceController(resourcesService);
        ReflectionTestUtils.setField(resourceRequirementResource, "resourceRequirementRepository", resourceRequirementRepository);
        this.restResourceRequirementMockMvc = MockMvcBuilders.standaloneSetup(resourceRequirementResource).build();
    }

    @Before
    public void initTest() {
        resourceRequirement = new ResourceRequirement();
        resourceRequirement.setAmount(DEFAULT_AMOUNT);
        resourceRequirement.setDescription(DEFAULT_DESCRIPTION);
        resourceRequirement.setProjectId(DEFAULT_PROJECT_ID);
        resourceRequirement.setIsEssential(DEFAULT_IS_ESSENTIAL);
        resourceRequirement.setCreatedBy("");
        resourceRequirement.setCreatedDate(DEFAULT_CREATED_DATE);
        resourceRequirement.setLastModifiedBy("");
        resourceRequirement.setLastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        resourceRequirement.setActive(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    public void createResourceRequirement() throws Exception {
        // Validate the database is empty
        assertThat(resourceRequirementRepository.findAll()).hasSize(0);

        // Create the ResourceRequirement
        restResourceRequirementMockMvc.perform(post("/app/rest/resourceRequirements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceRequirement)))
                .andExpect(status().isOk());

        // Validate the ResourceRequirement in the database
        List<ResourceRequirement> resourceRequirements = resourceRequirementRepository.findAll();
        assertThat(resourceRequirements).hasSize(1);
        ResourceRequirement testResourceRequirement = resourceRequirements.iterator().next();
        assertThat(testResourceRequirement.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testResourceRequirement.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testResourceRequirement.getProjectId()).isEqualTo(DEFAULT_PROJECT_ID);
        assertThat(testResourceRequirement.getIsEssential()).isEqualTo(DEFAULT_IS_ESSENTIAL);
        assertThat(testResourceRequirement.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testResourceRequirement.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testResourceRequirement.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testResourceRequirement.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testResourceRequirement.isActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    public void getAllResourceRequirements() throws Exception {
        // Initialize the database
        resourceRequirementRepository.saveAndFlush(resourceRequirement);

        // Get all the resourceRequirements
        restResourceRequirementMockMvc.perform(get("/app/rest/resourceRequirements"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(resourceRequirement.getId().intValue()))
                .andExpect(jsonPath("$.[0].amount").value(DEFAULT_AMOUNT.intValue()))
                .andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION.toString()))
                .andExpect(jsonPath("$.[0].projectId").value(DEFAULT_PROJECT_ID.intValue()))
                .andExpect(jsonPath("$.[0].isEssential").value(DEFAULT_IS_ESSENTIAL.booleanValue()))
                .andExpect(jsonPath("$.[0].createdBy").value(DEFAULT_CREATED_BY.intValue()))
                .andExpect(jsonPath("$.[0].createdDate").value(DEFAULT_CREATED_DATE_STR))
                .andExpect(jsonPath("$.[0].lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.intValue()))
                .andExpect(jsonPath("$.[0].lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE_STR))
                .andExpect(jsonPath("$.[0].isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getResourceRequirement() throws Exception {
        // Initialize the database
        resourceRequirementRepository.saveAndFlush(resourceRequirement);

        // Get the resourceRequirement
        restResourceRequirementMockMvc.perform(get("/app/rest/resourceRequirements/{id}", resourceRequirement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(resourceRequirement.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.projectId").value(DEFAULT_PROJECT_ID.intValue()))
            .andExpect(jsonPath("$.isEssential").value(DEFAULT_IS_ESSENTIAL.booleanValue()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.intValue()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE_STR))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.intValue()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE_STR))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingResourceRequirement() throws Exception {
        // Get the resourceRequirement
        restResourceRequirementMockMvc.perform(get("/app/rest/resourceRequirements/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResourceRequirement() throws Exception {
        // Initialize the database
        resourceRequirementRepository.saveAndFlush(resourceRequirement);

        // Update the resourceRequirement
        resourceRequirement.setAmount(UPDATED_AMOUNT);
        resourceRequirement.setDescription(UPDATED_DESCRIPTION);
        resourceRequirement.setProjectId(UPDATED_PROJECT_ID);
        resourceRequirement.setIsEssential(UPDATED_IS_ESSENTIAL);
        resourceRequirement.setCreatedBy("");
        resourceRequirement.setCreatedDate(UPDATED_CREATED_DATE);
        resourceRequirement.setLastModifiedBy("");
        resourceRequirement.setLastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        resourceRequirement.setActive(UPDATED_IS_ACTIVE);
        restResourceRequirementMockMvc.perform(post("/app/rest/resourceRequirements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceRequirement)))
                .andExpect(status().isOk());

        // Validate the ResourceRequirement in the database
        List<ResourceRequirement> resourceRequirements = resourceRequirementRepository.findAll();
        assertThat(resourceRequirements).hasSize(1);
        ResourceRequirement testResourceRequirement = resourceRequirements.iterator().next();
        assertThat(testResourceRequirement.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testResourceRequirement.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testResourceRequirement.getProjectId()).isEqualTo(UPDATED_PROJECT_ID);
        assertThat(testResourceRequirement.getIsEssential()).isEqualTo(UPDATED_IS_ESSENTIAL);
        assertThat(testResourceRequirement.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testResourceRequirement.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testResourceRequirement.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testResourceRequirement.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testResourceRequirement.isActive()).isEqualTo(UPDATED_IS_ACTIVE);;
    }

    @Test
    @Transactional
    public void deleteResourceRequirement() throws Exception {
        // Initialize the database
        resourceRequirementRepository.saveAndFlush(resourceRequirement);

        // Get the resourceRequirement
        restResourceRequirementMockMvc.perform(delete("/app/rest/resourceRequirements/{id}", resourceRequirement.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ResourceRequirement> resourceRequirements = resourceRequirementRepository.findAll();
        assertThat(resourceRequirements).hasSize(0);
    }
}
