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
import org.respondeco.respondeco.service.ProjectService;
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
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.repository.ProjectRepository;

/**
 * Test class for the ProjectIdeaResource REST controller.
 *
 * @see ProjectController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class ProjectControllerTest {
    
    private static final Long DEFAULT_ID = new Long(1L);
    
    private static final Long DEFAULT_ORGANIZATION_ID = 0L;
    private static final Long UPDATED_ORGANIZATION_ID = 1L;
        
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
        
    private static final String DEFAULT_PURPOSE = "SAMPLE_TEXT";
    private static final String UPDATED_PURPOSE = "UPDATED_TEXT";
        
    private static final String DEFAULT_PROJECT_LOGO = "SAMPLE_TEXT";
    private static final String UPDATED_PROJECT_LOGO = "UPDATED_TEXT";
        
    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private ProjectService projectService;

    private MockMvc restProjectMockMvc;

    private Project project;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ProjectController projectController = new ProjectController(projectService, projectRepository);

        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        project = new Project();
        project.setId(DEFAULT_ID);

        project.setOrganizationId(DEFAULT_ORGANIZATION_ID);
        project.setName(DEFAULT_NAME);
        project.setPurpose(DEFAULT_PURPOSE);
    }

    @Test
    public void testCRUDProjectIdea() throws Exception {

        // Create ProjectIdea
        restProjectMockMvc.perform(post("/app/rest/projectideas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(project)))
                .andExpect(status().isOk());

        // Read ProjectIdea
        restProjectMockMvc.perform(get("/app/rest/projectideas/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.organizationId").value(DEFAULT_ORGANIZATION_ID.intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.purpose").value(DEFAULT_PURPOSE.toString()))
                .andExpect(jsonPath("$.projectLogo").value(DEFAULT_PROJECT_LOGO.toString()));

        // Update ProjectIdea
        project.setOrganizationId(UPDATED_ORGANIZATION_ID);
        project.setName(UPDATED_NAME);
        project.setPurpose(UPDATED_PURPOSE);

        restProjectMockMvc.perform(post("/app/rest/projectideas")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(project)))
                .andExpect(status().isOk());

        // Read updated ProjectIdea
        restProjectMockMvc.perform(get("/app/rest/projectideas/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.organizationId").value(UPDATED_ORGANIZATION_ID.intValue()))
                .andExpect(jsonPath("$.name").value(UPDATED_NAME.toString()))
                .andExpect(jsonPath("$.purpose").value(UPDATED_PURPOSE.toString()))
                .andExpect(jsonPath("$.projectLogo").value(UPDATED_PROJECT_LOGO.toString()));

        // Delete ProjectIdea
        restProjectMockMvc.perform(delete("/app/rest/projectideas/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Read nonexisting ProjectIdea
        restProjectMockMvc.perform(get("/app/rest/projectideas/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }
}
