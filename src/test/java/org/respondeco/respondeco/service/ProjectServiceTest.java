package org.respondeco.respondeco.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Clemens Puehringer on 16/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userService;

    private ProjectService projectService;
    private Project basicProject;
    private User defaultUser;
    private User orgOwner;
    private Organization defaultOrganization;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        projectService = new ProjectService(
                projectRepositoryMock,
                userService,
                userRepositoryMock,
                organizationRepositoryMock);

        defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setLogin("testuser");
        defaultUser.setOrgId(1L);

        orgOwner = new User();
        orgOwner.setId(2L);
        orgOwner.setLogin("org owner");
        orgOwner.setOrgId(1L);

        basicProject = new Project();
        basicProject.setName("test project");
        basicProject.setPurpose("test purpose");
        basicProject.setConcrete(false);
        basicProject.setOrganizationId(1L);

        defaultOrganization = new Organization();
        defaultOrganization.setName("test org");
        defaultOrganization.setId(1L);
        defaultOrganization.setOwner(orgOwner);

    }

    @Test
    public void testSaveProject_shouldCreateBasicProject() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.save(null, "test project", "test purpose", false, null, null, null);

        verify(userService, times(1)).getUserWithAuthorities();
        verify(organizationRepositoryMock, times(1)).findOne(defaultOrganization.getId());
        verify(projectRepositoryMock, times(1)).save(isA(Project.class));

    }

    @Test
    public void testSaveProject_shouldCreateConcreteProject() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.save(null,
                "test project",
                "test purpose",
                true,
                LocalDate.now(),
                LocalDate.now().plusDays(10),
                null);

        verify(userService, times(1)).getUserWithAuthorities();
        verify(organizationRepositoryMock, times(1)).findOne(defaultOrganization.getId());
        verify(projectRepositoryMock, times(1)).save(isA(Project.class));

    }

    @Test
    public void testSaveProject_shouldModifyExistingProject() throws Exception {
        basicProject.setId(1L);
        basicProject.setManagerId(defaultUser.getId());

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        Project modifiedProject = new Project();
        modifiedProject.setId(basicProject.getId());
        modifiedProject.setName("modified name");
        modifiedProject.setPurpose("modified name");
        modifiedProject.setConcrete(basicProject.isConcrete());

        projectService.save(basicProject.getId(),
                "modified name",
                "modified name",
                false,
                null,
                null,
                null);

        verify(userService, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findOne(basicProject.getId());
        verify(organizationRepositoryMock, times(1)).findOne(defaultOrganization.getId());
        verify(projectRepositoryMock, times(1)).save(isA(Project.class));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveProject_shouldThrowExceptionIfProjectDoesNotExist() throws Exception {
        basicProject.setId(1L);
        basicProject.setManagerId(defaultUser.getId());

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(null);

        Project modifiedProject = new Project();
        modifiedProject.setId(basicProject.getId());
        modifiedProject.setName("modified name");
        modifiedProject.setPurpose("modified name");
        modifiedProject.setConcrete(basicProject.isConcrete());

        projectService.save(basicProject.getId(),
                "modified name",
                "modified name",
                false,
                null,
                null,
                null);
    }

    @Test(expected = OperationForbiddenException.class)
    public void testSaveProject_shouldThrowExceptionIfCurrentUserIsNotManager() throws Exception {
        basicProject.setId(1L);
        basicProject.setManagerId(100L);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        Project modifiedProject = new Project();
        modifiedProject.setId(basicProject.getId());
        modifiedProject.setName("modified name");
        modifiedProject.setPurpose("modified name");
        modifiedProject.setConcrete(basicProject.isConcrete());

        projectService.save(basicProject.getId(),
                "modified name",
                "modified name",
                false,
                null,
                null,
                null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveProject_shouldThrowExceptionBecauseIsConcreteAndStartDateIsNull() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.save(null,
                "test project",
                "test purpose",
                true,
                null,
                LocalDate.now(),
                null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveProject_shouldThrowExceptionBecauseIsConcreteAndEndDateIsNull() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.save(null,
                "test project",
                "test purpose",
                true,
                LocalDate.now(),
                null,
                null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveProject_shouldThrowExceptionBecauseEndDateIsBeforeNow() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.save(null,
                "test project",
                "test purpose",
                true,
                LocalDate.now(),
                LocalDate.now().minusDays(10),
                null);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveProject_shouldThrowExceptionBecauseEndDateIsBeforeStartDate() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.save(null,
                "test project",
                "test purpose",
                true,
                LocalDate.now().plusDays(15),
                LocalDate.now().plusDays(10),
                null);

    }

    @Test
    public void testSetManager_shouldSetManager() throws Exception {
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrgId(defaultOrganization.getId());

        basicProject.setId(1L);
        basicProject.setManagerId(defaultUser.getId());

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByLogin(otherUser.getLogin())).thenReturn(otherUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.setManager(defaultOrganization.getId(), otherUser.getLogin());

        verify(userService, times(1)).getUserWithAuthorities();
        verify(userRepositoryMock, times(1)).findByLogin(otherUser.getLogin());
        verify(projectRepositoryMock, times(1)).findOne(basicProject.getId());
        verify(projectRepositoryMock, times(1)).save(isA(Project.class));

        assertEquals(basicProject.getManagerId(), otherUser.getId());
    }

    @Test(expected = NoSuchUserException.class)
    public void testSetManager_shouldThrowExceptionIfNewManagerDoesNotExist() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("other");
        otherUser.setOrgId(defaultOrganization.getId());

        basicProject.setManagerId(defaultUser.getId());

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.setManager(defaultOrganization.getId(), otherUser.getLogin());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetManager_shouldThrowExceptionIfProjectDoesNotExist() throws Exception {
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrgId(defaultOrganization.getId());

        basicProject.setManagerId(defaultUser.getId());

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByLogin(otherUser.getLogin())).thenReturn(otherUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.setManager(defaultOrganization.getId(), otherUser.getLogin());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetManager_shouldThrowExceptionIfCurrentUserIsNotManager() throws Exception {
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrgId(defaultOrganization.getId());

        basicProject.setManagerId(200L);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByLogin(otherUser.getLogin())).thenReturn(otherUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.setManager(defaultOrganization.getId(), otherUser.getLogin());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetManager_shouldThrowExceptionIfNewManagerDoesNotBelongToCorrectOrganization() throws Exception {
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrgId(100L);

        basicProject.setManagerId(defaultUser.getId());

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByLogin(otherUser.getLogin())).thenReturn(otherUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.setManager(defaultOrganization.getId(), otherUser.getLogin());

    }

    @Test
    public void testDelete_shouldDeleteIfCurrentUserIsManager() throws Exception {

        basicProject.setManagerId(defaultUser.getId());

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.delete(basicProject.getId());

        verify(userService, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findOne(basicProject.getId());
        verify(projectRepositoryMock, times(1)).save(isA(Project.class));

        assertFalse(basicProject.isActive());

    }

    @Test
    public void testDelete_shouldDeleteIfCurrentUserIsOrganizationOwner() throws Exception {

        basicProject.setManagerId(100L);

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.delete(basicProject.getId());

        verify(userService, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findOne(basicProject.getId());
        verify(organizationRepositoryMock, times(1)).findOne(defaultOrganization.getId());
        verify(projectRepositoryMock, times(1)).save(isA(Project.class));

        assertFalse(basicProject.isActive());

    }

    @Test(expected = OperationForbiddenException.class)
    public void testSetManager_shouldThrowExceptionIfUserIsNotAuthorized() throws Exception {
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrgId(100L);

        basicProject.setManagerId(defaultUser.getId());

        when(userService.getUserWithAuthorities()).thenReturn(otherUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.delete(basicProject.getId());

    }

}
