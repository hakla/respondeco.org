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
import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.NoSuchProjectException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.OperationForbiddenException;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.respondeco.respondeco.web.rest.dto.ProjectResponseDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

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
    private PropertyTagService propertyTagServiceMock;

    @Mock
    private ImageRepository imageRepositoryMock;

    @Mock
    private UserService userService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceMatchRepository resourceMatchRepository;

    @Mock
    private PostingFeedRepository postingFeedRepository;

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
                propertyTagServiceMock,
                resourceService,
                imageRepositoryMock,
                resourceMatchRepository,
                postingFeedRepository);


        defaultOrganization = new Organization();
        defaultOrganization.setName("test org");
        defaultOrganization.setId(1L);

        defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setLogin("testuser");
        defaultUser.setOrganization(defaultOrganization);

        orgOwner = new User();
        orgOwner.setId(2L);
        orgOwner.setLogin("org owner");
        orgOwner.setOrganization(defaultOrganization);
        defaultOrganization.setOwner(orgOwner);


        basicProject = new Project();
        basicProject.setId(1L);
        basicProject.setName("test project");
        basicProject.setPurpose("test purpose");
        basicProject.setConcrete(false);
        basicProject.setManager(defaultUser);

        basicProject.setOrganization(defaultOrganization);

        doReturn(new ArrayList<PropertyTag>()).when(propertyTagServiceMock).getOrCreateTags(anyObject());


    }

    @Test
    public void testCreateProject_shouldCreateBasicProject() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.create("test project", "test purpose", false, null,  null, null, null);

        verify(userService, times(1)).getUserWithAuthorities();

        // Check if called two times
        // One time to create the project and one time after the requirements have been created
        // A requirement needs a project id. That's why there should be two invocations
        verify(projectRepositoryMock, times(2)).save(isA(Project.class));
    }

    @Test
    public void testCreateProject_shouldCreateConcreteProject() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.create(
                "test project",
                "test purpose",
                true,
                LocalDate.now(),
                null,
                null,
                null);

        verify(userService, times(1)).getUserWithAuthorities();

        // Check if called two times
        // One time to create the project and one time after the requirements have been created
        // A requirement needs a project id. That's why there should be two invocations
        verify(projectRepositoryMock, times(2)).save(isA(Project.class));
    }

    @Test(expected = IllegalValueException.class)
    public void testCreateProject_shouldThrowExceptionBecauseIsConcreteAndStartDateIsNull() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.create(
                "test project",
                "test purpose",
                true,
                null,
                null,
                null,
                null);

    }

    @Test(expected = IllegalValueException.class)
    public void testUpdateProject_shouldThrowExceptionBecauseIsConcreteAndStartDateIsNull() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.update(basicProject.getId(),
                "modified name",
                "modified name",
                true,
                null,
                null,
                null,
                null);

    }

    @Test(expected = NoSuchProjectException.class)
    public void testUpdateProject_shouldThrowExceptionBecauseProjectDoesNotExist() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(1L)).thenReturn(null);

        projectService.update(1L,
                "modified name",
                "modified name",
                false,
                null,
                null,
                null,
                null);
    }

    @Test(expected = OperationForbiddenException.class)
    public void testUpdateProject_shouldThrowExceptionIfCurrentUserIsNotManager() throws Exception {
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrganization(defaultOrganization);

        basicProject.setId(1L);
        basicProject.setManager(otherUser);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        Project modifiedProject = new Project();
        modifiedProject.setId(basicProject.getId());
        modifiedProject.setName("modified name");
        modifiedProject.setPurpose("modified name");
        modifiedProject.setConcrete(basicProject.isConcrete());

        projectService.update(basicProject.getId(),
                "modified name",
                "modified name",
                false,
                null,
                null,
                null,
                null);
    }

    @Test
    public void testUpdateProject_shouldUpdateExistingProject() throws Exception {
        basicProject.setId(1L);
        basicProject.setManager(defaultUser);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        Project modifiedProject = new Project();
        modifiedProject.setId(basicProject.getId());
        modifiedProject.setName("modified name");
        modifiedProject.setPurpose("modified name");
        modifiedProject.setConcrete(basicProject.isConcrete());

        projectService.update(basicProject.getId(),
                "modified name",
                "modified name",
                false,
                null,
                null,
                null,
                null);
    }

    @Test
    public void testSetManager_shouldSetManager() throws Exception {
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrganization(defaultOrganization);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByIdAndActiveIsTrue(otherUser.getId())).thenReturn(otherUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.setManager(defaultOrganization.getId(), otherUser.getId());

        verify(userService, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findOne(basicProject.getId());
        verify(projectRepositoryMock, times(1)).save(isA(Project.class));

        assertEquals(basicProject.getManager(), otherUser);
    }

    @Test(expected = NoSuchUserException.class)
    public void testSetManager_shouldThrowExceptionIfNewManagerDoesNotExist() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("other");
        otherUser.setOrganization(defaultOrganization);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.setManager(defaultOrganization.getId(), otherUser.getId());

    }

    @Test(expected = NoSuchProjectException.class)
    public void testSetManager_shouldThrowExceptionIfProjectDoesNotExist() throws Exception {
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrganization(defaultOrganization);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByIdAndActiveIsTrue(otherUser.getId())).thenReturn(otherUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.setManager(defaultOrganization.getId(), otherUser.getId());

    }

    @Test(expected = OperationForbiddenException.class)
    public void testSetManager_shouldThrowExceptionIfCurrentUserIsNotManager() throws Exception {
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrganization(defaultOrganization);

        User thirdUser = new User();
        thirdUser.setId(200L);
        thirdUser.setLogin("third");
        thirdUser.setOrganization(defaultOrganization);

        basicProject.setManager(thirdUser);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByIdAndActiveIsTrue(otherUser.getId())).thenReturn(otherUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.setManager(defaultOrganization.getId(), otherUser.getId());

    }

    @Test(expected = IllegalValueException.class)
    public void testSetManager_shouldThrowExceptionIfNewManagerDoesNotBelongToCorrectOrganization() throws Exception {
        Organization otherOrg = new Organization();
        otherOrg.setId(100L);
        otherOrg.setName("otherOrg");

        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrganization(otherOrg);
        otherOrg.setOwner(otherUser);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByIdAndActiveIsTrue(otherUser.getId())).thenReturn(otherUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.setManager(defaultOrganization.getId(), otherUser.getId());

    }

    @Test
    public void testDelete_shouldDeleteIfCurrentUserIsManager() throws Exception {
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
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrganization(defaultOrganization);

        basicProject.setManager(otherUser);

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.delete(basicProject.getId());

        verify(userService, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findOne(basicProject.getId());
        verify(projectRepositoryMock, times(1)).save(isA(Project.class));

        assertFalse(basicProject.isActive());

    }

    @Test(expected = OperationForbiddenException.class)
    public void testSetManager_shouldThrowExceptionIfUserIsNotAuthorized() throws Exception {
        Organization otherOrg = new Organization();
        otherOrg.setId(100L);
        otherOrg.setName("otherOrg");

        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrganization(otherOrg);
        otherOrg.setOwner(otherUser);

        when(userService.getUserWithAuthorities()).thenReturn(otherUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findOne(basicProject.getId())).thenReturn(basicProject);

        projectService.delete(basicProject.getId());

    }

    @Test
    public void testFindProjects_shouldCallRepositoryFindByNameAndTags() throws Exception {
        Project secondProject = new Project();
        secondProject.setId(2L);
        secondProject.setName("test project 2");
        secondProject.setPurpose("test purpose 2");
        secondProject.setConcrete(false);
        secondProject.setManager(defaultUser);
        secondProject.setOrganization(defaultOrganization);

        String name = "project";
        String tagsString = "tag2, tag3,   tag5,  ";
        List<String> tagsList = Arrays.asList("tag2", "tag3", "tag5");
        when(projectRepositoryMock.findByNameAndTags(name, tagsList, null))
                .thenReturn(Arrays.asList(basicProject, secondProject));

        List<Project> projects = projectService.findProjects(name, tagsString, null);

        verify(projectRepositoryMock, times(1)).findByNameAndTags("%" + name + "%", tagsList, null);
    }

    @Test
    public void testFindProjectsFromOrganization_shouldCallRepositoryFindByOrganizationAndNameAndTags()
            throws Exception {
        Project secondProject = new Project();
        secondProject.setId(2L);
        secondProject.setName("test project 2");
        secondProject.setPurpose("test purpose 2");
        secondProject.setConcrete(false);
        secondProject.setManager(defaultUser);
        secondProject.setOrganization(defaultOrganization);

        String name = "project";
        String tagsString = "tag2, tag3,   tag5,  ";
        List<String> tagsList = Arrays.asList("tag2", "tag3", "tag5");
        when(projectRepositoryMock.findByOrganizationAndNameAndTags(defaultOrganization.getId(), name, tagsList, null))
                .thenReturn(Arrays.asList(basicProject, secondProject));

        List<Project> projects = projectService
                .findProjectsFromOrganization(defaultOrganization.getId(), name, tagsString, null);

        verify(projectRepositoryMock, times(1))
                .findByOrganizationAndNameAndTags(defaultOrganization.getId(), "%" + name + "%", tagsList, null);
    }

}
