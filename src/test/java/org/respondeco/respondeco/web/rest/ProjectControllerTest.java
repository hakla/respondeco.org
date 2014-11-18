package org.respondeco.respondeco.web.rest;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.ProjectService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.testutil.ResultCaptor;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.ProjectDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.respondeco.respondeco.Application;
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
    private ProjectDTO projectDTO;
    private Organization defaultOrganization;
    private User orgAdmin;
    private User orgMember;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        projectService = spy(new ProjectService(
                projectRepository,
                userServiceMock,
                userRepositoryMock,
                organizationRepositoryMock));
        ProjectController projectController = new ProjectController(projectService, projectRepository);

        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        projectDTO = new ProjectDTO();
        projectDTO.setName(DEFAULT_NAME);
        projectDTO.setPurpose(DEFAULT_PURPOSE);
        projectDTO.setConcrete(false);

        orgAdmin = new User();
        orgAdmin.setId(1L);
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setOrgId(1L);

        orgMember = new User();
        orgMember.setId(1L);
        orgMember.setLogin("orgMember");
        orgMember.setOrgId(1L);

        defaultOrganization = new Organization();
        defaultOrganization.setId(1L);
        defaultOrganization.setName("testorg");
        defaultOrganization.setOwner(orgAdmin.getId());
    }

    @Test
    public void testCRUDProjectIdea() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).save(
                projectDTO.getId(),
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/project")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        verify(projectService, times(1)).save(
                projectDTO.getId(),
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        Long id = projectCaptor.getValue().getId();

        // Read Project
        restProjectMockMvc.perform(get("/app/rest/project/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.organizationId").value(defaultOrganization.getId().intValue()))
                .andExpect(jsonPath("$.managerId").value(orgMember.getId().intValue()))
                .andExpect(jsonPath("$.name").value(projectDTO.getName()))
                .andExpect(jsonPath("$.purpose").value(projectDTO.getPurpose()));

        // Update Project
        projectDTO.setId(id);
        projectDTO.setName(UPDATED_NAME);
        projectDTO.setPurpose(UPDATED_PURPOSE);

        restProjectMockMvc.perform(post("/app/rest/project")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        verify(projectService, times(1)).save(
                projectDTO.getId(),
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Read updated Project
        restProjectMockMvc.perform(get("/app/rest/project/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.organizationId").value(defaultOrganization.getId().intValue()))
                .andExpect(jsonPath("$.managerId").value(orgMember.getId().intValue()))
                .andExpect(jsonPath("$.name").value(UPDATED_NAME.toString()))
                .andExpect(jsonPath("$.purpose").value(UPDATED_PURPOSE.toString()));

        // Delete Project
        restProjectMockMvc.perform(delete("/app/rest/project/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        verify(projectService, times(1)).delete(id);

        // Read nonexisting Project
        restProjectMockMvc.perform(get("/app/rest/project/{id}", 100L)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testCREATE_shouldCreateConcreteProject() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectDTO.setConcrete(true);
        projectDTO.setStartDate(LocalDate.now());
        projectDTO.setEndDate(LocalDate.now().plusDays(5));

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).save(
                projectDTO.getId(),
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        System.out.println(new String(TestUtil.convertObjectToJsonBytes(projectDTO)));

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/project")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        Long id = projectCaptor.getValue().getId();

        restProjectMockMvc.perform(get("/app/rest/project/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.organizationId").value(defaultOrganization.getId().intValue()))
                .andExpect(jsonPath("$.managerId").value(orgMember.getId().intValue()))
                .andExpect(jsonPath("$.name").value(projectDTO.getName()))
                .andExpect(jsonPath("$.purpose").value(projectDTO.getPurpose()))
                .andExpect(jsonPath("$.concrete").value(true))
                .andExpect(jsonPath("$.startDate").value(projectDTO.getStartDate().toString("yyyy-MM-dd")))
                .andExpect(jsonPath("$.startDate").value(projectDTO.getStartDate().toString("yyyy-MM-dd")));

    }
}
