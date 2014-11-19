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
import org.respondeco.respondeco.domain.ResourceOfferJoinResourceRequirement;
import org.respondeco.respondeco.repository.ResourceOfferJoinResourceRequirementRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ResourceOfferJoinResourceRequirementResource REST controller.
 *
 * @see ResourceController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ResourceOfferJoinResourceRequirementResourceTest {
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final Long DEFAULT_RESOURCE_OFFER_ID = 0L;
    private static final Long UPDATED_RESOURCE_OFFER_ID = 1L;
    
    private static final Long DEFAULT_RESOURCE_REQUIREMENT_ID = 0L;
    private static final Long UPDATED_RESOURCE_REQUIREMENT_ID = 1L;
    
    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;
    private static final BigDecimal UPDATED_AMOUNT = BigDecimal.ONE;
    
    private static final String DEFAULT_CREATED_BY = "SAMPLE_TEXT";
    private static final String UPDATED_CREATED_BY = "UPDATED_TEXT";
    
   private static final DateTime DEFAULT_CREATED_DATE = new DateTime(0L);
   private static final DateTime UPDATED_CREATED_DATE = new DateTime().withMillisOfSecond(0);
   private static final String DEFAULT_CREATED_DATE_STR = dateTimeFormatter.print(DEFAULT_CREATED_DATE);
    
    private static final String DEFAULT_LAST_MODIFIED_BY = "SAMPLE_TEXT";
    private static final String UPDATED_LAST_MODIFIED_BY = "UPDATED_TEXT";
    
   private static final DateTime DEFAULT_LAST_MODIFIED_DATE = new DateTime(0L);
   private static final DateTime UPDATED_LAST_MODIFIED_DATE = new DateTime().withMillisOfSecond(0);
   private static final String DEFAULT_LAST_MODIFIED_DATE_STR = dateTimeFormatter.print(DEFAULT_LAST_MODIFIED_DATE);
    
    private static final Boolean DEFAULT_IS_ACTIVE = false;
    private static final Boolean UPDATED_IS_ACTIVE = true;

    @Inject
    private ResourceOfferJoinResourceRequirementRepository resourceOfferJoinResourceRequirementRepository;

    @Inject
    private ResourcesService resourcesService;

    private MockMvc restResourceOfferJoinResourceRequirementMockMvc;

    private ResourceOfferJoinResourceRequirement resourceOfferJoinResourceRequirement;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ResourceController resourceOfferJoinResourceRequirementResource = new ResourceController(resourcesService);
        ReflectionTestUtils.setField(resourceOfferJoinResourceRequirementResource, "resourceOfferJoinResourceRequirementRepository", resourceOfferJoinResourceRequirementRepository);
        this.restResourceOfferJoinResourceRequirementMockMvc = MockMvcBuilders.standaloneSetup(resourceOfferJoinResourceRequirementResource).build();
    }

    @Before
    public void initTest() {
        resourceOfferJoinResourceRequirement = new ResourceOfferJoinResourceRequirement();
        resourceOfferJoinResourceRequirement.setResourceOfferId(DEFAULT_RESOURCE_OFFER_ID);
        resourceOfferJoinResourceRequirement.setResourceRequirementId(DEFAULT_RESOURCE_REQUIREMENT_ID);
        resourceOfferJoinResourceRequirement.setAmount(DEFAULT_AMOUNT);
        resourceOfferJoinResourceRequirement.setCreatedBy(DEFAULT_CREATED_BY);
        resourceOfferJoinResourceRequirement.setCreatedDate(DEFAULT_CREATED_DATE);
        resourceOfferJoinResourceRequirement.setLastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
        resourceOfferJoinResourceRequirement.setLastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        resourceOfferJoinResourceRequirement.setActive(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    public void createResourceOfferJoinResourceRequirement() throws Exception {
        // Validate the database is empty
        assertThat(resourceOfferJoinResourceRequirementRepository.findAll()).hasSize(0);

        // Create the ResourceOfferJoinResourceRequirement
        restResourceOfferJoinResourceRequirementMockMvc.perform(post("/app/rest/resourceOfferJoinResourceRequirements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceOfferJoinResourceRequirement)))
                .andExpect(status().isOk());

        // Validate the ResourceOfferJoinResourceRequirement in the database
        List<ResourceOfferJoinResourceRequirement> resourceOfferJoinResourceRequirements = resourceOfferJoinResourceRequirementRepository.findAll();
        assertThat(resourceOfferJoinResourceRequirements).hasSize(1);
        ResourceOfferJoinResourceRequirement testResourceOfferJoinResourceRequirement = resourceOfferJoinResourceRequirements.iterator().next();
        assertThat(testResourceOfferJoinResourceRequirement.getResourceOfferId()).isEqualTo(DEFAULT_RESOURCE_OFFER_ID);
        assertThat(testResourceOfferJoinResourceRequirement.getResourceRequirementId()).isEqualTo(DEFAULT_RESOURCE_REQUIREMENT_ID);
        assertThat(testResourceOfferJoinResourceRequirement.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testResourceOfferJoinResourceRequirement.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testResourceOfferJoinResourceRequirement.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testResourceOfferJoinResourceRequirement.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testResourceOfferJoinResourceRequirement.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testResourceOfferJoinResourceRequirement.isActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    public void getAllResourceOfferJoinResourceRequirements() throws Exception {
        // Initialize the database
        resourceOfferJoinResourceRequirementRepository.saveAndFlush(resourceOfferJoinResourceRequirement);

        // Get all the resourceOfferJoinResourceRequirements
        restResourceOfferJoinResourceRequirementMockMvc.perform(get("/app/rest/resourceOfferJoinResourceRequirements"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(resourceOfferJoinResourceRequirement.getId().intValue()))
                .andExpect(jsonPath("$.[0].resourceOfferId").value(DEFAULT_RESOURCE_OFFER_ID.intValue()))
                .andExpect(jsonPath("$.[0].resourceRequirementId").value(DEFAULT_RESOURCE_REQUIREMENT_ID.intValue()))
                .andExpect(jsonPath("$.[0].amount").value(DEFAULT_AMOUNT.intValue()))
                .andExpect(jsonPath("$.[0].createdBy").value(DEFAULT_CREATED_BY.toString()))
                .andExpect(jsonPath("$.[0].createdDate").value(DEFAULT_CREATED_DATE_STR))
                .andExpect(jsonPath("$.[0].lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
                .andExpect(jsonPath("$.[0].lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE_STR))
                .andExpect(jsonPath("$.[0].isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getResourceOfferJoinResourceRequirement() throws Exception {
        // Initialize the database
        resourceOfferJoinResourceRequirementRepository.saveAndFlush(resourceOfferJoinResourceRequirement);

        // Get the resourceOfferJoinResourceRequirement
        restResourceOfferJoinResourceRequirementMockMvc.perform(get("/app/rest/resourceOfferJoinResourceRequirements/{id}", resourceOfferJoinResourceRequirement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(resourceOfferJoinResourceRequirement.getId().intValue()))
            .andExpect(jsonPath("$.resourceOfferId").value(DEFAULT_RESOURCE_OFFER_ID.intValue()))
            .andExpect(jsonPath("$.resourceRequirementId").value(DEFAULT_RESOURCE_REQUIREMENT_ID.intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE_STR))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE_STR))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingResourceOfferJoinResourceRequirement() throws Exception {
        // Get the resourceOfferJoinResourceRequirement
        restResourceOfferJoinResourceRequirementMockMvc.perform(get("/app/rest/resourceOfferJoinResourceRequirements/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResourceOfferJoinResourceRequirement() throws Exception {
        // Initialize the database
        resourceOfferJoinResourceRequirementRepository.saveAndFlush(resourceOfferJoinResourceRequirement);

        // Update the resourceOfferJoinResourceRequirement
        resourceOfferJoinResourceRequirement.setResourceOfferId(UPDATED_RESOURCE_OFFER_ID);
        resourceOfferJoinResourceRequirement.setResourceRequirementId(UPDATED_RESOURCE_REQUIREMENT_ID);
        resourceOfferJoinResourceRequirement.setAmount(UPDATED_AMOUNT);
        resourceOfferJoinResourceRequirement.setCreatedBy(UPDATED_CREATED_BY);
        resourceOfferJoinResourceRequirement.setCreatedDate(UPDATED_CREATED_DATE);
        resourceOfferJoinResourceRequirement.setLastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        resourceOfferJoinResourceRequirement.setLastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        resourceOfferJoinResourceRequirement.setActive(UPDATED_IS_ACTIVE);
        restResourceOfferJoinResourceRequirementMockMvc.perform(post("/app/rest/resourceOfferJoinResourceRequirements")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceOfferJoinResourceRequirement)))
                .andExpect(status().isOk());

        // Validate the ResourceOfferJoinResourceRequirement in the database
        List<ResourceOfferJoinResourceRequirement> resourceOfferJoinResourceRequirements = resourceOfferJoinResourceRequirementRepository.findAll();
        assertThat(resourceOfferJoinResourceRequirements).hasSize(1);
        ResourceOfferJoinResourceRequirement testResourceOfferJoinResourceRequirement = resourceOfferJoinResourceRequirements.iterator().next();
        assertThat(testResourceOfferJoinResourceRequirement.getResourceOfferId()).isEqualTo(UPDATED_RESOURCE_OFFER_ID);
        assertThat(testResourceOfferJoinResourceRequirement.getResourceRequirementId()).isEqualTo(UPDATED_RESOURCE_REQUIREMENT_ID);
        assertThat(testResourceOfferJoinResourceRequirement.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testResourceOfferJoinResourceRequirement.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testResourceOfferJoinResourceRequirement.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testResourceOfferJoinResourceRequirement.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testResourceOfferJoinResourceRequirement.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testResourceOfferJoinResourceRequirement.isActive()).isEqualTo(UPDATED_IS_ACTIVE);;
    }

    @Test
    @Transactional
    public void deleteResourceOfferJoinResourceRequirement() throws Exception {
        // Initialize the database
        resourceOfferJoinResourceRequirementRepository.saveAndFlush(resourceOfferJoinResourceRequirement);

        // Get the resourceOfferJoinResourceRequirement
        restResourceOfferJoinResourceRequirementMockMvc.perform(delete("/app/rest/resourceOfferJoinResourceRequirements/{id}", resourceOfferJoinResourceRequirement.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ResourceOfferJoinResourceRequirement> resourceOfferJoinResourceRequirements = resourceOfferJoinResourceRequirementRepository.findAll();
        assertThat(resourceOfferJoinResourceRequirements).hasSize(0);
    }
}
