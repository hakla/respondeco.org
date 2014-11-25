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
import org.respondeco.respondeco.domain.ProjectLogo;
import org.respondeco.respondeco.repository.ProjectLogoRepository;

/**
 * Test class for the ProjectLogoResource REST controller.
 *
 * @see ProjectLogoController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class ProjectLogoControllerTest {
    
    private static final Long DEFAULT_ID = new Long(1L);
    
    private static final byte[] DEFAULT_DATA = new byte[] {1,2,3,4,5};
    private static final byte[] UPDATED_DATA = new byte[] {6,7,8,9,10};
        
    @Inject
    private ProjectLogoRepository projectlogoRepository;

    private MockMvc restProjectLogoMockMvc;

    private ProjectLogo projectlogo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectLogoController projectlogoController = new ProjectLogoController();
        ReflectionTestUtils.setField(projectlogoController, "projectlogoRepository", projectlogoRepository);

        this.restProjectLogoMockMvc = MockMvcBuilders.standaloneSetup(projectlogoController).build();

        projectlogo = new ProjectLogo();
        projectlogo.setId(DEFAULT_ID);

        projectlogo.setData(DEFAULT_DATA);
    }

    @Test
    public void testCRUDProjectLogo() throws Exception {

        // Create ProjectLogo
        restProjectLogoMockMvc.perform(post("/app/rest/projectlogos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectlogo)))
                .andExpect(status().isOk());

        // Read ProjectLogo
        restProjectLogoMockMvc.perform(get("/app/rest/projectlogos/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()));

        // Update ProjectLogo
        projectlogo.setData(UPDATED_DATA);

        restProjectLogoMockMvc.perform(post("/app/rest/projectlogos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectlogo)))
                .andExpect(status().isOk());

        // Read updated ProjectLogo
        restProjectLogoMockMvc.perform(get("/app/rest/projectlogos/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.data").value(UPDATED_DATA.toString()));

        // Delete ProjectLogo
        restProjectLogoMockMvc.perform(delete("/app/rest/projectlogos/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Read nonexisting ProjectLogo
        restProjectLogoMockMvc.perform(get("/app/rest/projectlogos/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }
}
