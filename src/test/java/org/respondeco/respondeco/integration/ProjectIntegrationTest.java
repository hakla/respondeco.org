package org.respondeco.respondeco.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.*;
import org.respondeco.respondeco.testutil.ResultCaptor;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.ProjectController;
import org.respondeco.respondeco.web.rest.dto.ImageDTO;
import org.respondeco.respondeco.web.rest.dto.ProjectRequestDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.respondeco.respondeco.Application;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

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
public class ProjectIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {

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

    @Inject
    private PropertyTagService propertyTagService;

    @Inject
    private ImageRepository imageRepository;

    @Mock
    private UserService userServiceMock;

    @Inject
    private ResourceService resourceService;

    @Inject
    private RatingService ratingService;

    @Inject
    private PlatformTransactionManager txManager;

    private ProjectService projectService;
    private MockMvc restProjectMockMvc;
    private ProjectRequestDTO projectRequestDTO;
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
                propertyTagService,
                resourceService,
                imageRepository));
        ProjectController projectController = new ProjectController(projectService, resourceService, ratingService);

        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        projectRequestDTO = new ProjectRequestDTO();
        projectRequestDTO.setName(DEFAULT_NAME);
        projectRequestDTO.setPurpose(DEFAULT_PURPOSE);
        projectRequestDTO.setConcrete(false);
        projectRequestDTO.setLogo(new ImageDTO());

        orgAdmin = new User();
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setGender(Gender.UNSPECIFIED);
        orgAdmin = userRepository.save(orgAdmin);

        defaultOrganization = new Organization();
        defaultOrganization.setName("testorg");
        defaultOrganization.setOwner(orgAdmin);
        defaultOrganization = organizationRepository.save(defaultOrganization);
        orgAdmin.setOrganization(defaultOrganization);
        orgAdmin = userRepository.save(orgAdmin);

        orgMember = new User();
        orgMember.setLogin("orgMember");
        orgMember.setGender(Gender.UNSPECIFIED);
        orgMember.setOrganization(defaultOrganization);
        orgMember = userRepository.save(orgMember);
    }

    @Test
    @Transactional
    public void testCRUDProject() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getLogo().getId());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isOk());

        verify(projectService, times(1)).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getLogo().getId());

        Long id = projectCaptor.getValue().getId();

        // Read Project
        restProjectMockMvc.perform(get("/app/rest/projects/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.organizationId").value(defaultOrganization.getId().intValue()))
                .andExpect(jsonPath("$.managerId").value(orgMember.getId().intValue()))
                .andExpect(jsonPath("$.name").value(projectRequestDTO.getName()))
                .andExpect(jsonPath("$.purpose").value(projectRequestDTO.getPurpose()));

        // Update Project
        projectRequestDTO.setId(id);
        projectRequestDTO.setName(UPDATED_NAME);
        projectRequestDTO.setPurpose(UPDATED_PURPOSE);

        restProjectMockMvc.perform(put("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isOk());

        verify(projectService, times(1)).update(
                projectRequestDTO.getId(),
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getLogo().getId(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements());

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
    @Transactional
    public void testPOST_shouldCreateConcreteProject() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        projectRequestDTO.setConcrete(true);
        projectRequestDTO.setStartDate(LocalDate.now());
        projectRequestDTO.setEndDate(LocalDate.now().plusDays(5));

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getLogo().getId());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isOk());

        Long id = projectCaptor.getValue().getId();

        restProjectMockMvc.perform(get("/app/rest/projects/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.intValue()))
                .andExpect(jsonPath("$.organizationId").value(defaultOrganization.getId().intValue()))
                .andExpect(jsonPath("$.managerId").value(orgMember.getId().intValue()))
                .andExpect(jsonPath("$.name").value(projectRequestDTO.getName()))
                .andExpect(jsonPath("$.purpose").value(projectRequestDTO.getPurpose()))
                .andExpect(jsonPath("$.concrete").value(true))
                .andExpect(jsonPath("$.startDate").value(projectRequestDTO.getStartDate().toString("yyyy-MM-dd")))
                .andExpect(jsonPath("$.endDate").value(projectRequestDTO.getEndDate().toString("yyyy-MM-dd")));

    }

    @Test
    public void testPOST_expectBAD_REQEST_endDateMustNotBeBeforeNow() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        projectRequestDTO.setConcrete(true);
        projectRequestDTO.setStartDate(LocalDate.now().minusDays(5));
        projectRequestDTO.setEndDate(LocalDate.now().minusDays(3));

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testPOST_expectBAD_REQEST_endDateMustNotBeBeforeStartDate() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        projectRequestDTO.setConcrete(true);
        projectRequestDTO.setStartDate(LocalDate.now().plusDays(5));
        projectRequestDTO.setEndDate(LocalDate.now().plusDays(3));

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void testPOST_expectOK_shouldChangeManager() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("otherOrgMember");
        otherUser.setGender(Gender.UNSPECIFIED);
        otherUser.setOrganization(defaultOrganization);
        userRepository.save(otherUser);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getLogo().getId());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isOk());

        Long projectId = projectCaptor.getValue().getId();
        doAnswer(projectCaptor).when(projectService).setManager(projectId, otherUser.getLogin());

        restProjectMockMvc.perform(put("/app/rest/projects/{id}/manager", projectId)
                .content(otherUser.getLogin())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        assertEquals(projectCaptor.getValue().getManager(), otherUser);
    }

    @Test
    @Transactional
    public void testPOST_expectOK_orgOwnerCanSetManager() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("otherOrgMember");
        otherUser.setGender(Gender.UNSPECIFIED);
        otherUser.setOrganization(defaultOrganization);
        userRepository.save(otherUser);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getLogo().getId());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isOk());

        Long projectId = projectCaptor.getValue().getId();
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgAdmin);
        doAnswer(projectCaptor).when(projectService).setManager(projectId, otherUser.getLogin());

        restProjectMockMvc.perform(put("/app/rest/projects/{id}/manager", projectId)
                .content(otherUser.getLogin())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        assertEquals(projectCaptor.getValue().getManager(), otherUser);
    }

    @Test
    @Transactional
    public void testPOST_expectBAD_REQUEST_newManagerHasToBeInSameOrganization() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("otherOrgMember");
        otherUser.setGender(Gender.UNSPECIFIED);
        otherUser.setOrganization(null);
        userRepository.save(otherUser);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getLogo().getId());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isOk());

        Long projectId = projectCaptor.getValue().getId();
        doAnswer(projectCaptor).when(projectService).setManager(projectId, otherUser.getLogin());

        restProjectMockMvc.perform(put("/app/rest/projects/{id}/manager", projectId)
                .content(otherUser.getLogin())
                .contentType(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    public void testPOST_expectFORBIDDEN_userHasToBeProjectManagerToChangeManager() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("otherOrgMember");
        otherUser.setGender(Gender.UNSPECIFIED);
        otherUser.setOrganization(defaultOrganization);

        User unauthorizedUser = new User();
        unauthorizedUser.setLogin("unauthorizedOrgMember");
        unauthorizedUser.setGender(Gender.UNSPECIFIED);
        unauthorizedUser.setOrganization(defaultOrganization);

        userRepository.save(otherUser);
        userRepository.save(unauthorizedUser);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getLogo().getId());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
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
    @Transactional
    public void testDELETE_expectOK_orgOwnerCanDeleteProject() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getLogo().getId());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isOk());

        Long id = projectCaptor.getValue().getId();
        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgAdmin);

        // Delete Project
        restProjectMockMvc.perform(delete("/app/rest/projects/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    public void testDELETE_expectFORBIDDEN_onlyManagerOrOrgAdminCanDeleteProject() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("otherOrgMember");
        otherUser.setGender(Gender.UNSPECIFIED);
        otherUser.setOrganization(defaultOrganization);
        userRepository.save(otherUser);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);

        ResultCaptor<Project> projectCaptor = ResultCaptor.forType(Project.class);
        doAnswer(projectCaptor).when(projectService).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getLogo().getId());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isOk());

        Long id = projectCaptor.getValue().getId();
        when(userServiceMock.getUserWithAuthorities()).thenReturn(otherUser);

        // Delete Project
        restProjectMockMvc.perform(delete("/app/rest/projects/{id}", id)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isForbidden());
    }

}

