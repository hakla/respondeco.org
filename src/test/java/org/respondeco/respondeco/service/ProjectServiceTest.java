package org.respondeco.respondeco.service;

import com.mysema.query.types.Predicate;
import com.sun.jmx.remote.internal.ArrayQueue;
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
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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
    private UserService userServiceMock;

    @Mock
    private ResourceService resourceService;

    @Mock
    private ResourceMatchRepository resourceMatchRepository;

    @Mock
    private PostingFeedRepository postingFeedRepository;

    @Mock
    private ProjectLocationRepository projectLocationRepositoryMock;

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
                userServiceMock,
                userRepositoryMock,
                propertyTagServiceMock,
                resourceService,
                imageRepositoryMock,
                resourceMatchRepository,
                postingFeedRepository,
                projectLocationRepositoryMock);


        defaultOrganization = new Organization();
        defaultOrganization.setName("test org");
        defaultOrganization.setId(1L);
        defaultOrganization.setVerified(true);

        defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setLogin("testuser");
        defaultUser.setOrganization(defaultOrganization);
        defaultUser.setFollowProjects(new ArrayList<>());

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
        basicProject.setPropertyTags(new ArrayList<>());

        basicProject.setOrganization(defaultOrganization);

        ArrayList<User> usrList = new ArrayList<>();
        usrList.add(defaultUser);
        basicProject.setFollowingUsers(usrList);

        doReturn(new ArrayList<PropertyTag>()).when(propertyTagServiceMock).getOrCreateTags(anyObject());
    }

    @Test
    public void testCreateProject_shouldCreateBasicProject() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.create("test project", "test purpose", false, null,  null, null, null);

        verify(userServiceMock, times(1)).getUserWithAuthorities();

        // Check if called two times
        // One time to create the project and one time after the requirements have been created
        // A requirement needs a project id. That's why there should be two invocations
        verify(projectRepositoryMock, times(2)).save(isA(Project.class));
    }

    @Test
    public void testCreateProject_shouldCreateConcreteProject() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);

        projectService.create(
                "test project",
                "test purpose",
                true,
                LocalDate.now(),
                null,
                null,
                null);

        verify(userServiceMock, times(1)).getUserWithAuthorities();

        // Check if called two times
        // One time to create the project and one time after the requirements have been created
        // A requirement needs a project id. That's why there should be two invocations
        verify(projectRepositoryMock, times(2)).save(isA(Project.class));
    }

    @Test(expected = IllegalValueException.class)
    public void testCreateProject_shouldThrowExceptionBecauseIsConcreteAndStartDateIsNull() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);

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
        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

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
        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(1L)).thenReturn(null);

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

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

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

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

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

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByIdAndActiveIsTrue(otherUser.getId())).thenReturn(otherUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

        projectService.setManager(defaultOrganization.getId(), otherUser.getId());

        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(basicProject.getId());
        verify(projectRepositoryMock, times(1)).save(isA(Project.class));

        assertEquals(basicProject.getManager(), otherUser);
    }

    @Test(expected = NoSuchUserException.class)
    public void testSetManager_shouldThrowExceptionIfNewManagerDoesNotExist() throws Exception {
        User otherUser = new User();
        otherUser.setLogin("other");
        otherUser.setOrganization(defaultOrganization);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

        projectService.setManager(defaultOrganization.getId(), otherUser.getId());

    }

    @Test(expected = NoSuchProjectException.class)
    public void testSetManager_shouldThrowExceptionIfProjectDoesNotExist() throws Exception {
        User otherUser = new User();
        otherUser.setId(100L);
        otherUser.setLogin("other");
        otherUser.setOrganization(defaultOrganization);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByIdAndActiveIsTrue(otherUser.getId())).thenReturn(otherUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);

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

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByIdAndActiveIsTrue(otherUser.getId())).thenReturn(otherUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

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

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findByIdAndActiveIsTrue(otherUser.getId())).thenReturn(otherUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

        projectService.setManager(defaultOrganization.getId(), otherUser.getId());

    }

    @Test
    public void testDelete_shouldDeleteIfCurrentUserIsManager() throws Exception {
        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

        projectService.delete(basicProject.getId());

        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(basicProject.getId());
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

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

        projectService.delete(basicProject.getId());

        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(basicProject.getId());
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

        when(userServiceMock.getUserWithAuthorities()).thenReturn(otherUser);
        when(organizationRepositoryMock.findByIdAndActiveIsTrue(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

        projectService.delete(basicProject.getId());

    }

    @Test
    public void testFindProjects_shouldCallRepository() throws Exception {
        Project secondProject = new Project();
        secondProject.setId(2L);
        secondProject.setName("test project 2");
        secondProject.setPurpose("test purpose 2");
        secondProject.setConcrete(false);
        secondProject.setManager(defaultUser);
        secondProject.setOrganization(defaultOrganization);
        secondProject.setPropertyTags(new ArrayList<>());

        String name = "project";

        Page<Project> page = new PageImpl<>(Arrays.asList(basicProject, secondProject));

        when(projectRepositoryMock.findAll(any(Predicate.class), any(Pageable.class)))
                .thenReturn(page);

        List<Project> projects = projectService.findProjects(name, null).getContent();

        verify(projectRepositoryMock, times(1)).findAll(any(Predicate.class), any(Pageable.class));
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
        when(projectRepositoryMock.findAll(any(Predicate.class), any(Pageable.class)))
            .thenReturn(new PageImpl<Project>(Arrays.asList(basicProject, secondProject)));

        List<Project> projects = projectService
                .findProjectsFromOrganization(defaultOrganization.getId(), name, null).getContent();

        verify(projectRepositoryMock, times(1))
                .findAll(any(Predicate.class), any(Pageable.class));
    }

    @Test
    public void testFollowingState_TRUE() throws Exception{
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        doReturn(basicProject).when(projectRepositoryMock).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());

        Boolean testObject = projectService.followingState(basicProject.getId());

        verify(projectRepositoryMock, times(1)).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());
        verify(userServiceMock, times(1)).getUserWithAuthorities();

        assertTrue(testObject);
    }

    @Test
    public void testFollowingState_FALSE() throws Exception{
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        doReturn(null).when(projectRepositoryMock).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());

        Boolean testObject = projectService.followingState(basicProject.getId());

        verify(projectRepositoryMock, times(1)).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());
        verify(userServiceMock, times(1)).getUserWithAuthorities();
        assertFalse(testObject);
    }

    @Test
    public void testFollow_SUCCESS() throws Exception{
        // set default user as follower of the project
        ArrayList<User> usrList = new ArrayList<>();
        usrList.add(defaultUser);
        basicProject.setFollowingUsers(usrList);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(null).when(projectRepositoryMock).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());
        doReturn(basicProject).when(projectRepositoryMock).findByIdAndActiveIsTrue(basicProject.getId());
        //ignore
        //doReturn(null).when(userRepositoryMock).save(defaultUser);

        projectService.follow(basicProject.getId());

        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());
        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(basicProject.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testFollow_FAIL_AlreadyFollows() throws Exception{

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(basicProject).when(projectRepositoryMock).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());

        projectService.follow(basicProject.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testFollow_FAIL_ProjectNotFound() throws Exception{

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(null).when(projectRepositoryMock).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());
        // this trigger our Exception for Project NULL Value
        doReturn(null).when(projectRepositoryMock).findByIdAndActiveIsTrue(basicProject.getId());
        //ignore
        //doReturn(null).when(userRepositoryMock).save(defaultUser);

        projectService.follow(basicProject.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testFollow_FAIL_ProjectInactive() throws Exception{

        // we need an inactive project.
        basicProject.setActive(false);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(null).when(projectRepositoryMock).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());
        // this trigger our Exception for Project NULL Value
        doReturn(basicProject).when(projectRepositoryMock).findByIdAndActiveIsTrue(basicProject.getId());

        projectService.follow(basicProject.getId());
    }

    @Test
    public void testUnfollow_SUCCESS() throws Exception {
        // set default user as follower of the project
        ArrayList<User> usrList = new ArrayList<>();
        basicProject.setFollowingUsers(usrList);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(basicProject).when(projectRepositoryMock).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());
        doReturn(basicProject).when(projectRepositoryMock).findByIdAndActiveIsTrue(basicProject.getId());
        //ignore
        //doReturn(null).when(userRepositoryMock).save(defaultUser);

        projectService.unfollow(basicProject.getId());

        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(projectRepositoryMock, times(1)).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testUnfollow_FAIL_ProjectNotFound() throws Exception{

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new folldefaultUser.getId(), basicProject.getId());

        projectService.follow(basicProject.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testUnfollow_FAIL_ProjectInactive() throws Exception{
        // we need an inactive project.
        basicProject.setActive(false);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(basicProject).when(projectRepositoryMock).findByUserIdAndProjectId(defaultUser.getId(), basicProject.getId());

        projectService.unfollow(basicProject.getId());
    }

}
