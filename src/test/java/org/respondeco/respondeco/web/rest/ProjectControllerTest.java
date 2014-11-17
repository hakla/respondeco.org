package org.respondeco.respondeco.web.rest;

import static org.mockito.Mockito.when;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.ProjectService;
import org.respondeco.respondeco.service.UserService;
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

    private static final Long DEFAULT_MANAGER_ID = 0L;
    private static final Long UPDATED_MANAGER_ID = 1L;
        
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
        
    private static final String DEFAULT_PURPOSE = "SAMPLE_TEXT";
    private static final String UPDATED_PURPOSE = "UPDATED_TEXT";

        
    @Inject
    private ProjectRepository projectRepository;

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    private ProjectService projectService;
    private MockMvc restProjectMockMvc;
    private Project project;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        projectService = new ProjectService(
                projectRepository,
                userServiceMock,
                userRepositoryMock,
                organizationRepositoryMock);
        ProjectController projectController = new ProjectController(projectService, projectRepository);

        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        project = new Project();

        //projectidea.setOrganizationId(DEFAULT_ORGANIZATION_ID);


        project.setName(DEFAULT_NAME);
        project.setPurpose(DEFAULT_PURPOSE);
        project.setConcrete(false);

    }

    @Test
    public void testCRUDProjectIdea() throws Exception {
        User orgAdmin = new User();
        orgAdmin.setId(1L);
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setOrgId(1L);

        User orgMember = new User();
        orgMember.setId(1L);
        orgMember.setLogin("orgMember");
        orgMember.setOrgId(1L);

        Organization organization = new Organization();
        organization.setId(1L);
        organization.setName("testorg");
        organization.setOwner(orgAdmin.getId());

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);
        when(organizationRepositoryMock.findOne(1L)).thenReturn(organization);

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/project")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(project)))
                .andExpect(status().isOk());

        // Read Project
        restProjectMockMvc.perform(get("/app/rest/project/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.organizationId").value(DEFAULT_ORGANIZATION_ID.intValue()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.purpose").value(DEFAULT_PURPOSE.toString()));

        // Update ProjectIdea
        //projectidea.setOrganizationId(UPDATED_ORGANIZATION_ID);
        project.setName(UPDATED_NAME);
        project.setPurpose(UPDATED_PURPOSE);

        restProjectMockMvc.perform(post("/app/rest/project")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(project)))
                .andExpect(status().isOk());

        // Read updated Project
        restProjectMockMvc.perform(get("/app/rest/project/{id}", DEFAULT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.organizationId").value(DEFAULT_ORGANIZATION_ID.intValue()))
                .andExpect(jsonPath("$.name").value(UPDATED_NAME.toString()))
                .andExpect(jsonPath("$.purpose").value(UPDATED_PURPOSE.toString()));

        // Delete Project
        restProjectMockMvc.perform(delete("/app/rest/project/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Read nonexisting Project
        restProjectMockMvc.perform(get("/app/rest/project/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }
}
