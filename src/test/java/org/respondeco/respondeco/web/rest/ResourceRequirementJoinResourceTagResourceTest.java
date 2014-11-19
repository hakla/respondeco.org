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
import org.respondeco.respondeco.domain.ResourceRequirementJoinResourceTag;
import org.respondeco.respondeco.repository.ResourceRequirementJoinResourceTagRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ResourceRequirementJoinResourceTagResource REST controller.
 *
 * @see ResourceController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ResourceRequirementJoinResourceTagResourceTest {

    private static final Long DEFAULT_RESOURCE_REQUIREMENT_ID = 0L;
    private static final Long UPDATED_RESOURCE_REQUIREMENT_ID = 1L;
    
    private static final Long DEFAULT_RESOURCE_TAG_ID = 0L;
    private static final Long UPDATED_RESOURCE_TAG_ID = 1L;
    

    @Inject
    private ResourceRequirementJoinResourceTagRepository resourceRequirementJoinResourceTagRepository;

    @Inject
    private ResourcesService resourcesService;

    private MockMvc restResourceRequirementJoinResourceTagMockMvc;

    private ResourceRequirementJoinResourceTag resourceRequirementJoinResourceTag;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ResourceController resourceRequirementJoinResourceTagResource = new ResourceController(resourcesService);
        ReflectionTestUtils.setField(resourceRequirementJoinResourceTagResource, "resourceRequirementJoinResourceTagRepository", resourceRequirementJoinResourceTagRepository);
        this.restResourceRequirementJoinResourceTagMockMvc = MockMvcBuilders.standaloneSetup(resourceRequirementJoinResourceTagResource).build();
    }

    @Before
    public void initTest() {
        resourceRequirementJoinResourceTag = new ResourceRequirementJoinResourceTag();
        resourceRequirementJoinResourceTag.setResourceRequirementId(DEFAULT_RESOURCE_REQUIREMENT_ID);
        resourceRequirementJoinResourceTag.setResourceTagId(DEFAULT_RESOURCE_TAG_ID);
    }

    @Test
    @Transactional
    public void createResourceRequirementJoinResourceTag() throws Exception {
        // Validate the database is empty
        assertThat(resourceRequirementJoinResourceTagRepository.findAll()).hasSize(0);

        // Create the ResourceRequirementJoinResourceTag
        restResourceRequirementJoinResourceTagMockMvc.perform(post("/app/rest/resourceRequirementJoinResourceTags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceRequirementJoinResourceTag)))
                .andExpect(status().isOk());

        // Validate the ResourceRequirementJoinResourceTag in the database
        List<ResourceRequirementJoinResourceTag> resourceRequirementJoinResourceTags = resourceRequirementJoinResourceTagRepository.findAll();
        assertThat(resourceRequirementJoinResourceTags).hasSize(1);
        ResourceRequirementJoinResourceTag testResourceRequirementJoinResourceTag = resourceRequirementJoinResourceTags.iterator().next();
        assertThat(testResourceRequirementJoinResourceTag.getResourceRequirementId()).isEqualTo(DEFAULT_RESOURCE_REQUIREMENT_ID);
        assertThat(testResourceRequirementJoinResourceTag.getResourceTagId()).isEqualTo(DEFAULT_RESOURCE_TAG_ID);
    }

    @Test
    @Transactional
    public void getAllResourceRequirementJoinResourceTags() throws Exception {
        // Initialize the database
        resourceRequirementJoinResourceTagRepository.saveAndFlush(resourceRequirementJoinResourceTag);

        // Get all the resourceRequirementJoinResourceTags
        restResourceRequirementJoinResourceTagMockMvc.perform(get("/app/rest/resourceRequirementJoinResourceTags"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(resourceRequirementJoinResourceTag.getId().intValue()))
                .andExpect(jsonPath("$.[0].resourceRequirementId").value(DEFAULT_RESOURCE_REQUIREMENT_ID.intValue()))
                .andExpect(jsonPath("$.[0].resourceTagId").value(DEFAULT_RESOURCE_TAG_ID.intValue()));
    }

    @Test
    @Transactional
    public void getResourceRequirementJoinResourceTag() throws Exception {
        // Initialize the database
        resourceRequirementJoinResourceTagRepository.saveAndFlush(resourceRequirementJoinResourceTag);

        // Get the resourceRequirementJoinResourceTag
        restResourceRequirementJoinResourceTagMockMvc.perform(get("/app/rest/resourceRequirementJoinResourceTags/{id}", resourceRequirementJoinResourceTag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(resourceRequirementJoinResourceTag.getId().intValue()))
            .andExpect(jsonPath("$.resourceRequirementId").value(DEFAULT_RESOURCE_REQUIREMENT_ID.intValue()))
            .andExpect(jsonPath("$.resourceTagId").value(DEFAULT_RESOURCE_TAG_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingResourceRequirementJoinResourceTag() throws Exception {
        // Get the resourceRequirementJoinResourceTag
        restResourceRequirementJoinResourceTagMockMvc.perform(get("/app/rest/resourceRequirementJoinResourceTags/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResourceRequirementJoinResourceTag() throws Exception {
        // Initialize the database
        resourceRequirementJoinResourceTagRepository.saveAndFlush(resourceRequirementJoinResourceTag);

        // Update the resourceRequirementJoinResourceTag
        resourceRequirementJoinResourceTag.setResourceRequirementId(UPDATED_RESOURCE_REQUIREMENT_ID);
        resourceRequirementJoinResourceTag.setResourceTagId(UPDATED_RESOURCE_TAG_ID);
        restResourceRequirementJoinResourceTagMockMvc.perform(post("/app/rest/resourceRequirementJoinResourceTags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceRequirementJoinResourceTag)))
                .andExpect(status().isOk());

        // Validate the ResourceRequirementJoinResourceTag in the database
        List<ResourceRequirementJoinResourceTag> resourceRequirementJoinResourceTags = resourceRequirementJoinResourceTagRepository.findAll();
        assertThat(resourceRequirementJoinResourceTags).hasSize(1);
        ResourceRequirementJoinResourceTag testResourceRequirementJoinResourceTag = resourceRequirementJoinResourceTags.iterator().next();
        assertThat(testResourceRequirementJoinResourceTag.getResourceRequirementId()).isEqualTo(UPDATED_RESOURCE_REQUIREMENT_ID);
        assertThat(testResourceRequirementJoinResourceTag.getResourceTagId()).isEqualTo(UPDATED_RESOURCE_TAG_ID);;
    }

    @Test
    @Transactional
    public void deleteResourceRequirementJoinResourceTag() throws Exception {
        // Initialize the database
        resourceRequirementJoinResourceTagRepository.saveAndFlush(resourceRequirementJoinResourceTag);

        // Get the resourceRequirementJoinResourceTag
        restResourceRequirementJoinResourceTagMockMvc.perform(delete("/app/rest/resourceRequirementJoinResourceTags/{id}", resourceRequirementJoinResourceTag.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ResourceRequirementJoinResourceTag> resourceRequirementJoinResourceTags = resourceRequirementJoinResourceTagRepository.findAll();
        assertThat(resourceRequirementJoinResourceTags).hasSize(0);
    }
}
