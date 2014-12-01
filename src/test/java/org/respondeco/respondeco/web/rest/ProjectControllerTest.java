package org.respondeco.respondeco.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.ProjectService;
import org.respondeco.respondeco.service.PropertyTagService;
import org.respondeco.respondeco.service.ResourceService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.ProjectRequestDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
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

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private PropertyTagService propertyTagServiceMock;

    private ImageRepository imageRepositoryMock;

    @Mock
    private ResourceService resourceServiceMock;

    @Mock
    private PropertyTagRepository propertyTagRepositoryMock;

    private ProjectService projectServiceMock;
    private MockMvc restProjectMockMvc;
    private ProjectRequestDTO projectRequestDTO;
    private Project project;
    private Organization defaultOrganization;
    private User orgAdmin;
    private User orgMember;
/*
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        projectServiceMock = spy(new ProjectService(
                projectRepositoryMock,
                userServiceMock,
                userRepositoryMock,
                organizationRepositoryMock,
                propertyTagServiceMock,
                imageRepositoryMock));
        ProjectController projectController = new ProjectController(projectServiceMock, resourceServiceMock);

        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        projectRequestDTO = new ProjectRequestDTO();
        projectRequestDTO.setName(DEFAULT_NAME);
        projectRequestDTO.setPurpose(DEFAULT_PURPOSE);
        projectRequestDTO.setConcrete(false);

        orgAdmin = new User();
        orgAdmin.setId(100L);
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setOrgId(100L);

        orgMember = new User();
        orgMember.setId(200L);
        orgMember.setLogin("orgMember");
        orgMember.setOrgId(100L);

        defaultOrganization = new Organization();
        defaultOrganization.setId(100L);
        defaultOrganization.setName("testorg");

        project = new Project();
        project.setId(100L);
        project.setOrganization(defaultOrganization);
        project.setManager(orgMember);
        project.setName(DEFAULT_NAME);
        project.setPurpose(DEFAULT_PURPOSE);
        project.setConcrete(false);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);
        doReturn(new ArrayList<PropertyTag>()).when(propertyTagServiceMock).getOrCreateTags(anyObject());

        defaultOrganization.setOwner(orgAdmin);
    }

    @Test
    public void testCRUDProject() throws Exception {

        doReturn(project).when(projectServiceMock).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getImageId());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isOk());

        verify(projectServiceMock, times(1)).create(
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements(),
                projectRequestDTO.getImageId());

        when(projectRepositoryMock.findByIdAndActiveIsTrue(project.getId())).thenReturn(project);
        // Read Project

        restProjectMockMvc.perform(get("/app/rest/projects/{id}", project.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(project.getId().intValue()))
                .andExpect(jsonPath("$.organizationId").value(defaultOrganization.getId().intValue()))
                .andExpect(jsonPath("$.managerId").value(orgMember.getId().intValue()))
                .andExpect(jsonPath("$.name").value(projectRequestDTO.getName()))
                .andExpect(jsonPath("$.purpose").value(projectRequestDTO.getPurpose()));

        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(project.getId());

        // Update Project
        projectRequestDTO.setId(project.getId());
        projectRequestDTO.setName(UPDATED_NAME);
        projectRequestDTO.setPurpose(UPDATED_PURPOSE);

        doReturn(project).when(projectServiceMock).update(
                projectRequestDTO.getId(),
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getImageId(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements());

        restProjectMockMvc.perform(put("/app/rest/projects")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(projectRequestDTO)))
                .andExpect(status().isOk());

        verify(projectServiceMock, times(1)).update(
                projectRequestDTO.getId(),
                projectRequestDTO.getName(),
                projectRequestDTO.getPurpose(),
                projectRequestDTO.getConcrete(),
                projectRequestDTO.getStartDate(),
                projectRequestDTO.getEndDate(),
                projectRequestDTO.getImageId(),
                projectRequestDTO.getPropertyTags(),
                projectRequestDTO.getResourceRequirements());

        project.setName(UPDATED_NAME);
        project.setPurpose(UPDATED_PURPOSE);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(project.getId())).thenReturn(project);
        // Read updated Project
        restProjectMockMvc.perform(get("/app/rest/projects/{id}", project.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(project.getId().intValue()))
                .andExpect(jsonPath("$.organizationId").value(defaultOrganization.getId().intValue()))
                .andExpect(jsonPath("$.managerId").value(orgMember.getId().intValue()))
                .andExpect(jsonPath("$.name").value(UPDATED_NAME.toString()))
                .andExpect(jsonPath("$.purpose").value(UPDATED_PURPOSE.toString()));

        verify(projectRepositoryMock, times(2)).findByIdAndActiveIsTrue(project.getId());

        doReturn(project).when(projectServiceMock).delete(project.getId());
        // Delete Project
        restProjectMockMvc.perform(delete("/app/rest/projects/{id}", project.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        verify(projectServiceMock, times(1)).delete(project.getId());

        when(projectRepositoryMock.findByIdAndActiveIsTrue(1000L)).thenReturn(null);
        // Read nonexisting Project
        restProjectMockMvc.perform(get("/app/rest/projects/{id}", 1000L)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(1000L);

    }

    @Test
    public void testGetByNameAndTags_shouldCallServiceFindProjects() throws Exception {
        Project project2 = new Project();
        project2.setId(200L);
        project2.setName("test2");
        project2.setPurpose("testpurpose 2");
        project2.setOrganization(defaultOrganization);
        project2.setManager(orgMember);
        project2.setConcrete(false);

        doReturn(Arrays.asList(project, project2)).when(projectServiceMock)
                .findProjects(isNull(String.class), isNull(String.class), isA(RestParameters.class));

        restProjectMockMvc.perform(get("/app/rest/projects"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(project.getId().intValue()))
                .andExpect(jsonPath("$[1].id").value(project2.getId().intValue()));

        verify(projectServiceMock, times(1))
                .findProjects(isNull(String.class), isNull(String.class), isA(RestParameters.class));
    }

    @Test
    public void testGetByOrganizationAndNameAndTags_shouldCallServiceFindProjectsFromOrganization() throws Exception {
        Project project2 = new Project();
        project2.setId(200L);
        project2.setName("test2");
        project2.setPurpose("testpurpose 2");
        project2.setOrganization(defaultOrganization);
        project2.setManager(orgMember);
        project2.setConcrete(false);

        doReturn(Arrays.asList(project, project2)).when(projectServiceMock)
                .findProjectsFromOrganization(isA(Long.class), isNull(String.class),
                        isNull(String.class), isA(RestParameters.class));

        restProjectMockMvc.perform(get("/app/rest/organizations/{id}/projects", defaultOrganization.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(project.getId().intValue()))
                .andExpect(jsonPath("$[1].id").value(project2.getId().intValue()));

        verify(projectServiceMock, times(1))
                .findProjectsFromOrganization(isA(Long.class), isNull(String.class),
                        isNull(String.class), isA(RestParameters.class));
    }
*/
}
