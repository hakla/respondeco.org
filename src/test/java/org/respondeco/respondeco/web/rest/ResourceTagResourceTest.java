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
import java.util.List;

import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.ResourceTag;
import org.respondeco.respondeco.repository.ResourceTagRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ResourceTagResource REST controller.
 *
 * @see ResourceTagResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ResourceTagResourceTest {
   private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    
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
    private ResourceTagRepository resourceTagRepository;

    @Inject
    private ResourcesService resourcesService;

    private MockMvc restResourceTagMockMvc;

    private ResourceTag resourceTag;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ResourceController resourceTagResource = new ResourceController(resourcesService);
        ReflectionTestUtils.setField(resourceTagResource, "resourceTagRepository", resourceTagRepository);
        this.restResourceTagMockMvc = MockMvcBuilders.standaloneSetup(resourceTagResource).build();
    }

    @Before
    public void initTest() {
        resourceTag = new ResourceTag();
        resourceTag.setName(DEFAULT_NAME);
        resourceTag.setCreatedBy(DEFAULT_CREATED_BY);
        resourceTag.setCreatedDate(DEFAULT_CREATED_DATE);
        resourceTag.setLastModifiedBy(DEFAULT_LAST_MODIFIED_BY);
        resourceTag.setLastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        resourceTag.setActive(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    public void createResourceTag() throws Exception {
        // Validate the database is empty
        assertThat(resourceTagRepository.findAll()).hasSize(0);

        // Create the ResourceTag
        restResourceTagMockMvc.perform(post("/app/rest/resourceTags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceTag)))
                .andExpect(status().isOk());

        // Validate the ResourceTag in the database
        List<ResourceTag> resourceTags = resourceTagRepository.findAll();
        assertThat(resourceTags).hasSize(1);
        ResourceTag testResourceTag = resourceTags.iterator().next();
        assertThat(testResourceTag.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testResourceTag.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testResourceTag.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testResourceTag.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testResourceTag.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
        assertThat(testResourceTag.isActive()).isEqualTo(DEFAULT_IS_ACTIVE);
    }

    @Test
    @Transactional
    public void getAllResourceTags() throws Exception {
        // Initialize the database
        resourceTagRepository.saveAndFlush(resourceTag);

        // Get all the resourceTags
        restResourceTagMockMvc.perform(get("/app/rest/resourceTags"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(resourceTag.getId().intValue()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.[0].createdBy").value(DEFAULT_CREATED_BY.toString()))
                .andExpect(jsonPath("$.[0].createdDate").value(DEFAULT_CREATED_DATE_STR))
                .andExpect(jsonPath("$.[0].lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
                .andExpect(jsonPath("$.[0].lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE_STR))
                .andExpect(jsonPath("$.[0].isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getResourceTag() throws Exception {
        // Initialize the database
        resourceTagRepository.saveAndFlush(resourceTag);

        // Get the resourceTag
        restResourceTagMockMvc.perform(get("/app/rest/resourceTags/{id}", resourceTag.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(resourceTag.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY.toString()))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE_STR))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY.toString()))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE_STR))
            .andExpect(jsonPath("$.isActive").value(DEFAULT_IS_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingResourceTag() throws Exception {
        // Get the resourceTag
        restResourceTagMockMvc.perform(get("/app/rest/resourceTags/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateResourceTag() throws Exception {
        // Initialize the database
        resourceTagRepository.saveAndFlush(resourceTag);

        // Update the resourceTag
        resourceTag.setName(UPDATED_NAME);
        resourceTag.setCreatedBy(UPDATED_CREATED_BY);
        resourceTag.setCreatedDate(UPDATED_CREATED_DATE);
        resourceTag.setLastModifiedBy(UPDATED_LAST_MODIFIED_BY);
        resourceTag.setLastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        resourceTag.setActive(UPDATED_IS_ACTIVE);
        restResourceTagMockMvc.perform(post("/app/rest/resourceTags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(resourceTag)))
                .andExpect(status().isOk());

        // Validate the ResourceTag in the database
        List<ResourceTag> resourceTags = resourceTagRepository.findAll();
        assertThat(resourceTags).hasSize(1);
        ResourceTag testResourceTag = resourceTags.iterator().next();
        assertThat(testResourceTag.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testResourceTag.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testResourceTag.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testResourceTag.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testResourceTag.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
        assertThat(testResourceTag.isActive()).isEqualTo(UPDATED_IS_ACTIVE);;
    }

    @Test
    @Transactional
    public void deleteResourceTag() throws Exception {
        // Initialize the database
        resourceTagRepository.saveAndFlush(resourceTag);

        // Get the resourceTag
        restResourceTagMockMvc.perform(delete("/app/rest/resourceTags/{id}", resourceTag.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<ResourceTag> resourceTags = resourceTagRepository.findAll();
        assertThat(resourceTags).hasSize(0);
    }
}
