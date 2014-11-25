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
import java.util.List;

import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.ResourceOfferJoinResourceTag;
import org.respondeco.respondeco.repository.ResourceOfferJoinResourceTagRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ResourceOfferJoinResourceTagResource REST controller.
 *
 * @see ResourceController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ResourceOfferJoinResourceTagResourceTest {

    private static final Long DEFAULT_RESOURCE_OFFER_ID = 0L;
    private static final Long UPDATED_RESOURCE_OFFER_ID = 1L;
    
    private static final Long DEFAULT_RESOURCE_TAG_ID = 0L;
    private static final Long UPDATED_RESOURCE_TAG_ID = 1L;
    

    @Inject
    private ResourceOfferJoinResourceTagRepository resourceOfferJoinResourceTagRepository;

    @Inject
    private ResourcesService resourcesService;

    private MockMvc restResourceOfferJoinResourceTagMockMvc;

    private ResourceOfferJoinResourceTag resourceOfferJoinResourceTag;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ResourceController resourceOfferJoinResourceTagResource = new ResourceController(resourcesService);
        ReflectionTestUtils.setField(resourceOfferJoinResourceTagResource, "resourceOfferJoinResourceTagRepository", resourceOfferJoinResourceTagRepository);
        this.restResourceOfferJoinResourceTagMockMvc = MockMvcBuilders.standaloneSetup(resourceOfferJoinResourceTagResource).build();
    }

    @Before
    public void initTest() {
        resourceOfferJoinResourceTag = new ResourceOfferJoinResourceTag();
        resourceOfferJoinResourceTag.setResourceOfferId(DEFAULT_RESOURCE_OFFER_ID);
        resourceOfferJoinResourceTag.setResourceTagId(DEFAULT_RESOURCE_TAG_ID);
    }

    @Test
    @Transactional
    public void createResourceOfferJoinResourceTag() throws Exception {
        // Validate the database is empty
        assertThat(resourceOfferJoinResourceTagRepository.findAll()).hasSize(0);

        // Create the ResourceOfferJoinResourceTag
        restResourceOfferJoinResourceTagMockMvc.perform(post("/app/rest/resourceOfferJoinResourceTags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceOfferJoinResourceTag)))
                .andExpect(status().isOk());

        // Validate the ResourceOfferJoinResourceTag in the database
        List<ResourceOfferJoinResourceTag> resourceOfferJoinResourceTags = resourceOfferJoinResourceTagRepository.findAll();
        assertThat(resourceOfferJoinResourceTags).hasSize(1);
        ResourceOfferJoinResourceTag testResourceOfferJoinResourceTag = resourceOfferJoinResourceTags.iterator().next();
        assertThat(testResourceOfferJoinResourceTag.getResourceOfferId()).isEqualTo(DEFAULT_RESOURCE_OFFER_ID);
        assertThat(testResourceOfferJoinResourceTag.getResourceTagId()).isEqualTo(DEFAULT_RESOURCE_TAG_ID);
    }

    @Test
    @Transactional
    public void getAllResourceOfferJoinResourceTags() throws Exception {
        // Initialize the database
        resourceOfferJoinResourceTagRepository.saveAndFlush(resourceOfferJoinResourceTag);

        // Get all the resourceOfferJoinResourceTags
        restResourceOfferJoinResourceTagMockMvc.perform(get("/app/rest/resourceOfferJoinResourceTags"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(resourceOfferJoinResourceTag.getId().intValue()))
                .andExpect(jsonPath("$.[0].resourceOfferId").value(DEFAULT_RESOURCE_OFFER_ID.intValue()))
                .andExpect(jsonPath("$.[0].resourceTagId").value(DEFAULT_RESOURCE_TAG_ID.intValue()));
    }

    @Test
    @Transactional
    public void getResourceOfferJoinResourceTag() throws Exception {
        // Initialize the database
        resourceOfferJoinResourceTagRepository.saveAndFlush(resourceOfferJoinResourceTag);

        // Get the resourceOfferJoinResourceTag
        restResourceOfferJoinResourceTagMockMvc.perform(get("/app/rest/resourceOfferJoinResourceTags/{id}", resourceOfferJoinResourceTag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(resourceOfferJoinResourceTag.getId().intValue()))
            .andExpect(jsonPath("$.resourceOfferId").value(DEFAULT_RESOURCE_OFFER_ID.intValue()))
            .andExpect(jsonPath("$.resourceTagId").value(DEFAULT_RESOURCE_TAG_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingResourceOfferJoinResourceTag() throws Exception {
        // Get the resourceOfferJoinResourceTag
        restResourceOfferJoinResourceTagMockMvc.perform(get("/app/rest/resourceOfferJoinResourceTags/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResourceOfferJoinResourceTag() throws Exception {
        // Initialize the database
        resourceOfferJoinResourceTagRepository.saveAndFlush(resourceOfferJoinResourceTag);

        // Update the resourceOfferJoinResourceTag
        resourceOfferJoinResourceTag.setResourceOfferId(UPDATED_RESOURCE_OFFER_ID);
        resourceOfferJoinResourceTag.setResourceTagId(UPDATED_RESOURCE_TAG_ID);
        restResourceOfferJoinResourceTagMockMvc.perform(post("/app/rest/resourceOfferJoinResourceTags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceOfferJoinResourceTag)))
                .andExpect(status().isOk());

        // Validate the ResourceOfferJoinResourceTag in the database
        List<ResourceOfferJoinResourceTag> resourceOfferJoinResourceTags = resourceOfferJoinResourceTagRepository.findAll();
        assertThat(resourceOfferJoinResourceTags).hasSize(1);
        ResourceOfferJoinResourceTag testResourceOfferJoinResourceTag = resourceOfferJoinResourceTags.iterator().next();
        assertThat(testResourceOfferJoinResourceTag.getResourceOfferId()).isEqualTo(UPDATED_RESOURCE_OFFER_ID);
        assertThat(testResourceOfferJoinResourceTag.getResourceTagId()).isEqualTo(UPDATED_RESOURCE_TAG_ID);;
    }

    @Test
    @Transactional
    public void deleteResourceOfferJoinResourceTag() throws Exception {
        // Initialize the database
        resourceOfferJoinResourceTagRepository.saveAndFlush(resourceOfferJoinResourceTag);

        // Get the resourceOfferJoinResourceTag
        restResourceOfferJoinResourceTagMockMvc.perform(delete("/app/rest/resourceOfferJoinResourceTags/{id}", resourceOfferJoinResourceTag.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ResourceOfferJoinResourceTag> resourceOfferJoinResourceTags = resourceOfferJoinResourceTagRepository.findAll();
        assertThat(resourceOfferJoinResourceTags).hasSize(0);
    }
}
