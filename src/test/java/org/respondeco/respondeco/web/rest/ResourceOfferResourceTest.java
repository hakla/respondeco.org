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
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.repository.ResourceOfferRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ResourceOfferResource REST controller.
 *
 * @see ResourceController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ResourceOfferResourceTest {
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.ZERO;
    private static final BigDecimal UPDATED_AMOUNT = BigDecimal.ONE;
    
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";
    
    private static final Long DEFAULT_ORGANISATION_ID = 0L;
    private static final Long UPDATED_ORGANISATION_ID = 1L;
    
    private static final Long DEFAULT_CREATE_BY = 0L;
    private static final Long UPDATED_CREATE_BY = 1L;
    
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
    private ResourceOfferRepository resourceOfferRepository;

    @Inject
    private ResourcesService resourcesService;

    private MockMvc restResourceOfferMockMvc;

    private ResourceOffer resourceOffer;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ResourceController resourceOfferResource = new ResourceController(resourcesService);
        ReflectionTestUtils.setField(resourceOfferResource, "resourceOfferRepository", resourceOfferRepository);
        this.restResourceOfferMockMvc = MockMvcBuilders.standaloneSetup(resourceOfferResource).build();
    }

    @Before
    public void initTest() {
        resourceOffer = new ResourceOffer();
        resourceOffer.setAmount(DEFAULT_AMOUNT);
        resourceOffer.setDescription(DEFAULT_DESCRIPTION);
        resourceOffer.setOrganisationId(DEFAULT_ORGANISATION_ID);
        resourceOffer.setCreatedBy("");
        resourceOffer.setCreatedDate(DEFAULT_CREATED_DATE);
        resourceOffer.setLastModifiedBy("");
        resourceOffer.setLastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        resourceOffer.setActive(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    public void createResourceOffer() throws Exception {
        // Validate the database is empty
        assertThat(resourceOfferRepository.findAll()).hasSize(0);

        // Create the ResourceOffer
        restResourceOfferMockMvc.perform(post("/app/rest/resourceOffers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceOffer)))
                .andExpect(status().isOk());

        // Validate the ResourceOffer in the database
        List<ResourceOffer> resourceOffers = resourceOfferRepository.findAll();
        assertThat(resourceOffers).hasSize(1);
        ResourceOffer testResourceOffer = resourceOffers.iterator().next();
        assertThat(testResourceOffer.getAmount()).isEqualTo(DEFAULT_AMOUNT);
        assertThat(testResourceOffer.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testResourceOffer.getOrganisationId()).isEqualTo(DEFAULT_ORGANISATION_ID);
        assertThat(testResourceOffer.getCreatedBy()).isEqualTo(DEFAULT_CREATE_BY);
        assertThat(testResourceOffer.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testResourceOffer.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testResourceOffer.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testResourceOffer.isActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    public void getAllResourceOffers() throws Exception {
        // Initialize the database
        resourceOfferRepository.saveAndFlush(resourceOffer);

        // Get all the resourceOffers
        restResourceOfferMockMvc.perform(get("/app/rest/resourceOffers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(resourceOffer.getId().intValue()))
                .andExpect(jsonPath("$.[0].amount").value(DEFAULT_AMOUNT.intValue()))
                .andExpect(jsonPath("$.[0].description").value(DEFAULT_DESCRIPTION.toString()))
                .andExpect(jsonPath("$.[0].organisationId").value(DEFAULT_ORGANISATION_ID.intValue()))
                .andExpect(jsonPath("$.[0].createBy").value(DEFAULT_CREATE_BY.intValue()))
                .andExpect(jsonPath("$.[0].createdDate").value(DEFAULT_CREATED_DATE_STR))
                .andExpect(jsonPath("$.[0].lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.intValue()))
                .andExpect(jsonPath("$.[0].lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE_STR))
                .andExpect(jsonPath("$.[0].isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getResourceOffer() throws Exception {
        // Initialize the database
        resourceOfferRepository.saveAndFlush(resourceOffer);

        // Get the resourceOffer
        restResourceOfferMockMvc.perform(get("/app/rest/resourceOffers/{id}", resourceOffer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(resourceOffer.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(DEFAULT_AMOUNT.intValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.organisationId").value(DEFAULT_ORGANISATION_ID.intValue()))
            .andExpect(jsonPath("$.createBy").value(DEFAULT_CREATE_BY.intValue()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE_STR))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.intValue()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE_STR))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingResourceOffer() throws Exception {
        // Get the resourceOffer
        restResourceOfferMockMvc.perform(get("/app/rest/resourceOffers/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResourceOffer() throws Exception {
        // Initialize the database
        resourceOfferRepository.saveAndFlush(resourceOffer);

        // Update the resourceOffer
        resourceOffer.setAmount(UPDATED_AMOUNT);
        resourceOffer.setDescription(UPDATED_DESCRIPTION);
        resourceOffer.setOrganisationId(UPDATED_ORGANISATION_ID);
        resourceOffer.setCreatedBy("");
        resourceOffer.setCreatedDate(UPDATED_CREATED_DATE);
        resourceOffer.setLastModifiedBy("");
        resourceOffer.setLastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        resourceOffer.setActive(UPDATED_IS_ACTIVE);
        restResourceOfferMockMvc.perform(post("/app/rest/resourceOffers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceOffer)))
                .andExpect(status().isOk());

        // Validate the ResourceOffer in the database
        List<ResourceOffer> resourceOffers = resourceOfferRepository.findAll();
        assertThat(resourceOffers).hasSize(1);
        ResourceOffer testResourceOffer = resourceOffers.iterator().next();
        assertThat(testResourceOffer.getAmount()).isEqualTo(UPDATED_AMOUNT);
        assertThat(testResourceOffer.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testResourceOffer.getOrganisationId()).isEqualTo(UPDATED_ORGANISATION_ID);
        assertThat(testResourceOffer.getCreatedBy()).isEqualTo(UPDATED_CREATE_BY);
        assertThat(testResourceOffer.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testResourceOffer.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testResourceOffer.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testResourceOffer.isActive()).isEqualTo(UPDATED_IS_ACTIVE);;
    }

    @Test
    @Transactional
    public void deleteResourceOffer() throws Exception {
        // Initialize the database
        resourceOfferRepository.saveAndFlush(resourceOffer);

        // Get the resourceOffer
        restResourceOfferMockMvc.perform(delete("/app/rest/resourceOffers/{id}", resourceOffer.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ResourceOffer> resourceOffers = resourceOfferRepository.findAll();
        assertThat(resourceOffers).hasSize(0);
    }
}
