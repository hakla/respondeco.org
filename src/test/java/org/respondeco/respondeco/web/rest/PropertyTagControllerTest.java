package org.respondeco.respondeco.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.testutil.TestUtil;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.repository.PropertyTagRepository;

/**
 * Test class for the PropertyTagResource REST controller.
 *
 * @see PropertyTagController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class PropertyTagControllerTest {
    
    private static final Long DEFAULT_ID = new Long(1L);
    
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
        
    @Inject
    private PropertyTagRepository propertytagRepository;

    private MockMvc restPropertyTagMockMvc;

    private PropertyTag propertytag;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PropertyTagController propertytagController = new PropertyTagController();
        ReflectionTestUtils.setField(propertytagController, "propertytagRepository", propertytagRepository);

        this.restPropertyTagMockMvc = MockMvcBuilders.standaloneSetup(propertytagController).build();

        propertytag = new PropertyTag();
        propertytag.setId(DEFAULT_ID);

        propertytag.setName(DEFAULT_NAME);
    }

    @Test
    public void testCRUDPropertyTag() throws Exception {

        // Create PropertyTag
        restPropertyTagMockMvc.perform(post("/app/rest/propertytags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(propertytag)))
                .andExpect(status().isOk());

        // Read PropertyTag
        restPropertyTagMockMvc.perform(get("/app/rest/propertytags/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));

        // Update PropertyTag
        propertytag.setName(UPDATED_NAME);

        restPropertyTagMockMvc.perform(post("/app/rest/propertytags")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(propertytag)))
                .andExpect(status().isOk());

        // Read updated PropertyTag
        restPropertyTagMockMvc.perform(get("/app/rest/propertytags/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.name").value(UPDATED_NAME.toString()));

        // Delete PropertyTag
        restPropertyTagMockMvc.perform(delete("/app/rest/propertytags/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Read nonexisting PropertyTag
        restPropertyTagMockMvc.perform(get("/app/rest/propertytags/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }
}
