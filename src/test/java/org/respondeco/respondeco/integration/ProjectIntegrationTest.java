package org.respondeco.respondeco.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.ProjectService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.testutil.ResultCaptor;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.ProjectController;
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
 * @see org.respondeco.respondeco.web.rest.ProjectController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class ProjectIntegrationTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
        
    private static final String DEFAULT_PURPOSE = "SAMPLE_TEXT";
    private static final String UPDATED_PURPOSE = "UPDATED_TEXT";

        
    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private UserRepository userRepository;

    @Mock
    private UserService userServiceMock;

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
                userRepository,
                organizationRepository));
        ProjectController projectController = new ProjectController(projectService, projectRepository);

        projectRepository.deleteAll();
        organizationRepository.deleteAll();
        userRepository.deleteAll();
        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        projectDTO = new ProjectDTO();
        projectDTO.setName(DEFAULT_NAME);
        projectDTO.setPurpose(DEFAULT_PURPOSE);
        projectDTO.setConcrete(false);

        orgAdmin = new User();
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setGender(Gender.UNSPECIFIED);
        orgAdmin = userRepository.save(orgAdmin);

        defaultOrganization = new Organization();
        defaultOrganization.setName("testorg");
        defaultOrganization.setOwner(orgAdmin.getId());
        defaultOrganization = organizationRepository.save(defaultOrganization);
        orgAdmin.setOrgId(defaultOrganization.getId());
        orgAdmin = userRepository.save(orgAdmin);

        orgMember = new User();
        orgMember.setLogin("orgMember");
        orgMember.setGender(Gender.UNSPECIFIED);
        orgMember.setOrgId(defaultOrganization.getId());
        userRepository.save(orgMember);
    }

    @Test
    public void testCRUDProject() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        verify(projectService, times(1)).create(
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        Long id = projectCaptor.getValue().getId();

        // Read Project
        restProjectMockMvc.perform(get("/app/rest/projects/{id}", id))
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

        restProjectMockMvc.perform(put("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        verify(projectService, times(1)).update(
                projectDTO.getId(),
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Read updated Project
        restProjectMockMvc.perform(get("/app/rest/projects/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.organizationId").value(defaultOrganization.getId().intValue()))
                .andExpect(jsonPath("$.managerId").value(orgMember.getId().intValue()))
                .andExpect(jsonPath("$.name").value(UPDATED_NAME.toString()))
                .andExpect(jsonPath("$.purpose").value(UPDATED_PURPOSE.toString()));

        doAnswer(projectCaptor).when(projectService).delete(id);

        // Delete Project
        restProjectMockMvc.perform(delete("/app/rest/projects/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        verify(projectService, times(1)).delete(id);
        assertFalse(projectCaptor.getValue().isActive());

        // Read nonexisting Project
        restProjectMockMvc.perform(get("/app/rest/projects/{id}", 1000L)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testPOST_shouldCreateConcreteProject() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        projectDTO.setConcrete(true);
        projectDTO.setStartDate(LocalDate.now());
        projectDTO.setEndDate(LocalDate.now().plusDays(5));

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        Long id = projectCaptor.getValue().getId();

        restProjectMockMvc.perform(get("/app/rest/projects/{id}", id))
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

    @Test
    public void testPOST_expectBAD_REQEST_endDateMustNotBeBeforeNow() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        projectDTO.setConcrete(true);
        projectDTO.setStartDate(LocalDate.now().minusDays(5));
        projectDTO.setEndDate(LocalDate.now().minusDays(3));

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPOST_expectBAD_REQEST_endDateMustNotBeBeforeStartDate() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        projectDTO.setConcrete(true);
        projectDTO.setStartDate(LocalDate.now().plusDays(5));
        projectDTO.setEndDate(LocalDate.now().plusDays(3));

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPOST_expectOK_shouldChangeManager() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("otherOrgMember");
        otherUser.setGender(Gender.UNSPECIFIED);
        otherUser.setOrgId(defaultOrganization.getId());
        userRepository.save(otherUser);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        Long projectId = projectCaptor.getValue().getId();
        doAnswer(projectCaptor).when(projectService).setManager(projectId, otherUser.getLogin());

        restProjectMockMvc.perform(put("/app/rest/projects/{id}/manager", projectId)
                .content(otherUser.getLogin())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        assertEquals(projectCaptor.getValue().getManagerId(), otherUser.getId());
    }

    @Test
    public void testPOST_expectOK_orgOwnerCanSetManager() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("otherOrgMember");
        otherUser.setGender(Gender.UNSPECIFIED);
        otherUser.setOrgId(defaultOrganization.getId());
        userRepository.save(otherUser);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        Long projectId = projectCaptor.getValue().getId();
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgAdmin);
        doAnswer(projectCaptor).when(projectService).setManager(projectId, otherUser.getLogin());

        restProjectMockMvc.perform(put("/app/rest/projects/{id}/manager", projectId)
                .content(otherUser.getLogin())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        assertEquals(projectCaptor.getValue().getManagerId(), otherUser.getId());
    }

    @Test
    public void testPOST_expectBAD_REQUEST_newManagerHasToBeInSameOrganization() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("otherOrgMember");
        otherUser.setGender(Gender.UNSPECIFIED);
        otherUser.setOrgId(null);
        userRepository.save(otherUser);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        Long projectId = projectCaptor.getValue().getId();
        doAnswer(projectCaptor).when(projectService).setManager(projectId, otherUser.getLogin());

        restProjectMockMvc.perform(put("/app/rest/projects/{id}/manager", projectId)
                .content(otherUser.getLogin())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testPOST_expectFORBIDDEN_userHasToBeProjectManagerToChangeManager() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("otherOrgMember");
        otherUser.setGender(Gender.UNSPECIFIED);
        otherUser.setOrgId(defaultOrganization.getId());

        User unauthorizedUser = new User();
        unauthorizedUser.setLogin("unauthorizedOrgMember");
        unauthorizedUser.setGender(Gender.UNSPECIFIED);
        unauthorizedUser.setOrgId(defaultOrganization.getId());

        userRepository.save(otherUser);
        userRepository.save(unauthorizedUser);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        Long projectId = projectCaptor.getValue().getId();
        when(userServiceMock.getUserWithAuthorities()).thenReturn(unauthorizedUser);
        doAnswer(projectCaptor).when(projectService).setManager(projectId, otherUser.getLogin());

        restProjectMockMvc.perform(put("/app/rest/projects/{id}/manager", projectId)
                .content(otherUser.getLogin())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDELETE_expectOK_orgOwnerCanDeleteProject() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        Long id = projectCaptor.getValue().getId();
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgAdmin);

        // Delete Project
        restProjectMockMvc.perform(delete("/app/rest/projects/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    public void testDELETE_expectFORBIDDEN_onlyManagerOrOrgAdminCanDeleteProject() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("otherOrgMember");
        otherUser.setGender(Gender.UNSPECIFIED);
        otherUser.setOrgId(defaultOrganization.getId());
        userRepository.save(otherUser);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectDTO.getName(),
                projectDTO.getPurpose(),
                projectDTO.getConcrete(),
                projectDTO.getStartDate(),
                projectDTO.getEndDate(),
                projectDTO.getProjectLogo());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
                .andExpect(status().isOk());

        Long id = projectCaptor.getValue().getId();
        when(userServiceMock.getUserWithAuthorities()).thenReturn(otherUser);

        // Delete Project
        restProjectMockMvc.perform(delete("/app/rest/projects/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }
}
