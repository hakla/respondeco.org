package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Christoph Schiessl on 16/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ImageRepository imageRepositoryMock;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private PostingFeedRepository postingFeedRepositoryMock;

    private OrganizationService organizationService;

    private User defaultUser;
    private User orgOwner;
    private Organization testOrganization;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        organizationService = new OrganizationService(
            organizationRepositoryMock,
            userServiceMock,
            userRepositoryMock,
            imageRepositoryMock,
            projectServiceMock,
            postingFeedRepositoryMock);
        defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setLogin("testUser");
        defaultUser.setFollowOrganizations(new ArrayList<>());
        orgOwner = new User();
        orgOwner.setId(2L);
        orgOwner.setLogin("testOwner");

        testOrganization = new Organization();
        testOrganization.setFollowingUsers(new ArrayList<>());
        testOrganization.setId(1L);
    }

    @Test
    public void testCreateOrganization() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);

        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(organizationRepositoryMock, times(1)).save(isA(Organization.class));
    }

    @Test(expected = OrganizationAlreadyExistsException.class)
    public void testCreateOrganization_OrgNameAlreadyExists() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByName("testOrg")).thenReturn(organization);

        organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganization_NameMustNotBeEmpty() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        organizationService.createOrganizationInformation("","testDescription","test@email.com",false, 1L);
    }

    @Test(expected = AlreadyInOrganizationException.class)
    public void testCreateOrganization_AlreadyOwnerOfAnotherOrganization() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByOwner(orgOwner)).thenReturn(organization);
        organizationService.createOrganizationInformation("testOrg2","testDescription","test@email.com",false, 1L);
    }

    @Test
    public void testGetOrganizationByName() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByName("testOrg")).thenReturn(organization);
        organizationService.getOrganizationByName("testOrg");

        assertEquals(organization.getName(), "testOrg");
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testGetOrganizationByName_NotExisting() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByName("testOrg")).thenReturn(organization);
        organizationService.getOrganizationByName("test");
    }

    @Test
     public void testGetOrganization() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        Organization organization = organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findOne(organization.getId())).thenReturn(organization);

        Organization organization2 = organizationService.getOrganization(organization.getId());

        assertEquals(organization, organization2);
    }

    @Test
    public void testGetOrganizations() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization1 = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);

        Organization organization2 = organizationService.createOrganizationInformation("testOrg2","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByActiveIsTrue()).thenReturn(Arrays.asList(organization1,organization2));

        List<Organization> organizationList = organizationService.getOrganizations();

        assertTrue(organizationList.size()==2);
        verify(organizationRepositoryMock, times(1)).findByActiveIsTrue();
    }

    @Test
     public void testUpdateOrganization() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        when(organizationRepositoryMock.findByOwner(orgOwner)).thenReturn(organization);
        organizationService.updateOrganizationInformation("testOrg2", "testDescription", "test@email.com", false, 1L);

        when(organizationRepositoryMock.findByName("testOrg2")).thenReturn(organization);
        Organization organization2 = organizationService.getOrganizationByName("testOrg2");

        verify(organizationRepositoryMock, times(2)).save(organization);
        assertEquals(organization2.getName(), "testOrg2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateOrganization_WithEmptyName() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        when(organizationRepositoryMock.findByOwner(orgOwner)).thenReturn(organization);
        organizationService.updateOrganizationInformation("", "testDescription", "test@email.com", false, 1L);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testUpdateOrganization_NotExisting() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        organizationService.updateOrganizationInformation("testOrg1", "testDescription", "test@email.com", false, 1L);
    }

    @Test
    public void testDeleteOrganization() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByOwner(orgOwner)).thenReturn(organization);

        organizationService.deleteOrganizationInformation();

        verify(organizationRepositoryMock, times(1)).delete(organization);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testDeleteOrganization_NotExisting() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);

        organizationService.deleteOrganizationInformation();
    }

    @Test
    public void testDeleteMember() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService
            .createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        organization.setVerified(true);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(organization);
        organizationService.deleteMember(defaultUser.getId());

        verify(userRepositoryMock, times(1)).save(defaultUser);
    }

    @Test(expected = NoSuchUserException.class)
    public void testDeleteMember_shouldThrowNoSuchUserException() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(organization);
        organizationService.deleteMember(100L);

    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testDeleteMember_shouldThrowNoSuchOrganizationException() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(null);
        organizationService.deleteMember(defaultUser.getId());

    }

    @Test(expected = NotOwnerOfOrganizationException.class)
    public void testDeleteMember_shouldThrowNotOwnerOrganizationException() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService
            .createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        organization.setVerified(true);
        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(organization);
        organizationService.deleteMember(defaultUser.getId());

    }

    @Test
    public void testGetUserByOrgId() throws Exception {
        User user3 = new User();
        user3.setId(3L);
        user3.setLogin("testUser3");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        when(organizationRepositoryMock.findOne(organization.getId())).thenReturn(organization);
        defaultUser.setOrganization(organization);
        user3.setOrganization(organization);
        when(userRepositoryMock.findUsersByOrganizationId(organization.getId())).thenReturn(Arrays.asList(defaultUser, user3));
        List<User> members = organizationService.getUserByOrgId(organization.getId());
        assertTrue(members.size()==2);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testGetUserByOrgId_shouldThrowNoSuchOrganizationException() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        when(organizationRepositoryMock.findOne(organization.getId())).thenReturn(null);
        organizationService.getUserByOrgId(organization.getId());
    }

    @Test
    public void testFindInvitableUsersByOrgId() throws Exception {
        User user3 = new User();
        user3.setId(3L);
        user3.setLogin("testUser3");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        when(organizationRepositoryMock.getOne(organization.getId())).thenReturn(organization);
        when(userRepositoryMock.findInvitableUsers()).thenReturn(Arrays.asList(defaultUser,user3));
        List<User> users = organizationService.findInvitableUsersByOrgId(organization.getId());
        assertTrue(users.size()==2);
    }



    @Test
    public void testFollowingState_TRUE() throws Exception{
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        doReturn(testOrganization).when(organizationRepositoryMock).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());

        Boolean testObject = organizationService.followingState(testOrganization.getId());

        verify(organizationRepositoryMock, times(1)).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());
        verify(userServiceMock, times(1)).getUserWithAuthorities();

        assertTrue(testObject);
    }

    @Test
    public void testFollowingState_FALSE() throws Exception{
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        doReturn(null).when(organizationRepositoryMock).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());

        Boolean testObject = organizationService.followingState(testOrganization.getId());

        verify(organizationRepositoryMock, times(1)).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());
        verify(userServiceMock, times(1)).getUserWithAuthorities();
        assertFalse(testObject);
    }

    @Test
    public void testFollow_SUCCESS() throws Exception{
        // set default user as follower of the project
        ArrayList<User> usrList = new ArrayList<>();
        usrList.add(defaultUser);
        testOrganization.setFollowingUsers(usrList);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(null).when(organizationRepositoryMock).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());
        doReturn(testOrganization).when(organizationRepositoryMock).findOne(testOrganization.getId());
        //ignore
        //doReturn(null).when(userRepositoryMock).save(defaultUser);

        organizationService.follow(testOrganization.getId());

        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(organizationRepositoryMock, times(1)).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());
        verify(organizationRepositoryMock, times(1)).findOne(testOrganization.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testFollow_FAIL_AlreadyFollows() throws Exception{

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(testOrganization).when(organizationRepositoryMock).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());

        organizationService.follow(testOrganization.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testFollow_FAIL_OrganizationNotFound() throws Exception{

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(null).when(organizationRepositoryMock).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());
        // this trigger our Exception for Project NULL Value
        doReturn(null).when(organizationRepositoryMock).findOne(testOrganization.getId());
        //ignore
        //doReturn(null).when(userRepositoryMock).save(defaultUser);

        organizationService.follow(testOrganization.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testFollow_FAIL_OrganizationInactive() throws Exception{

        // we need an inactive project.
        testOrganization.setActive(false);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(null).when(organizationRepositoryMock).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());
        // this trigger our Exception for Project NULL Value
        doReturn(testOrganization).when(organizationRepositoryMock).findOne(testOrganization.getId());

        organizationService.follow(testOrganization.getId());
    }

    @Test
    public void testUnfollow_SUCCESS() throws Exception {
        // set default user as follower of the project
        ArrayList<User> usrList = new ArrayList<>();
        testOrganization.setFollowingUsers(usrList);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(testOrganization).when(organizationRepositoryMock).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());
        doReturn(testOrganization).when(organizationRepositoryMock).findOne(testOrganization.getId());
        //ignore
        //doReturn(null).when(userRepositoryMock).save(defaultUser);

        organizationService.unfollow(testOrganization.getId());

        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(organizationRepositoryMock, times(1)).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testUnfollow_FAIL_OrganizationNotFound() throws Exception{

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new folldefaultUser.getId(), basicProject.getId());

        organizationService.unfollow(testOrganization.getId());
    }

    @Test(expected = IllegalValueException.class)
    public void testUnfollow_FAIL_OrganizationInactive() throws Exception{
        // we need an inactive project.
        testOrganization.setActive(false);

        //config the results for some checks
        doReturn(defaultUser).when(userServiceMock).getUserWithAuthorities();
        // we need here returns null, else we won't be able to add new follower
        doReturn(testOrganization).when(organizationRepositoryMock).findByUserAndOrganization(defaultUser.getId(), testOrganization.getId());

        organizationService.unfollow(testOrganization.getId());
    }
}
