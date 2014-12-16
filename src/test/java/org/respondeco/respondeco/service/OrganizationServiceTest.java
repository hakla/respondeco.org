package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.ImageRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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
    private UserService userService;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ImageRepository imageRepositoryMock;

    private OrganizationService organizationService;

    private User defaultUser;
    private User orgOwner;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        organizationService = new OrganizationService(organizationRepositoryMock, userService, userRepositoryMock, imageRepositoryMock);
        defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setLogin("testUser");
        orgOwner = new User();
        orgOwner.setId(2L);
        orgOwner.setLogin("testOwner");
    }

    @Test
    public void testCreateOrganization() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);

        verify(userService, times(1)).getUserWithAuthorities();
        verify(organizationRepositoryMock, times(1)).save(isA(Organization.class));
    }

    @Test(expected = OrganizationAlreadyExistsException.class)
    public void testCreateOrganization_OrgNameAlreadyExists() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByName("testOrg")).thenReturn(organization);

        organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganization_NameMustNotBeEmpty() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        organizationService.createOrganizationInformation("","testDescription","test@email.com",false, 1L);
    }

    @Test(expected = AlreadyInOrganizationException.class)
    public void testCreateOrganization_AlreadyOwnerOfAnotherOrganization() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByOwner(orgOwner)).thenReturn(organization);
        organizationService.createOrganizationInformation("testOrg2","testDescription","test@email.com",false, 1L);
    }

    @Test
    public void testGetOrganizationByName() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByName("testOrg")).thenReturn(organization);
        organizationService.getOrganizationByName("testOrg");

        assertEquals(organization.getName(), "testOrg");
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testGetOrganizationByName_NotExisting() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByName("testOrg")).thenReturn(organization);
        organizationService.getOrganizationByName("test");
    }

    @Test
     public void testGetOrganization() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        Organization organization = organizationService.createOrganizationInformation("testOrg","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findOne(organization.getId())).thenReturn(organization);

        Organization organization2 = organizationService.getOrganization(organization.getId());

        assertEquals(organization, organization2);
    }

    @Test
    public void testGetOrganizations() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization1 = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);

        Organization organization2 = organizationService.createOrganizationInformation("testOrg2","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByActiveIsTrue()).thenReturn(Arrays.asList(organization1,organization2));

        List<Organization> organizationList = organizationService.getOrganizations();

        assertTrue(organizationList.size()==2);
        verify(organizationRepositoryMock, times(1)).findByActiveIsTrue();
    }

    @Test
     public void testUpdateOrganization() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        when(organizationRepositoryMock.findByOwner(orgOwner)).thenReturn(organization);
        organizationService.updaterOrganizationInformation("testOrg2", "testDescription", "test@email.com", false, 1L);

        when(organizationRepositoryMock.findByName("testOrg2")).thenReturn(organization);
        Organization organization2 = organizationService.getOrganizationByName("testOrg2");

        verify(organizationRepositoryMock, times(2)).save(organization);
        assertEquals(organization2.getName(), "testOrg2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateOrganization_WithEmptyName() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        when(organizationRepositoryMock.findByOwner(orgOwner)).thenReturn(organization);
        organizationService.updaterOrganizationInformation("","testDescription","test@email.com",false, 1L);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testUpdateOrganization_NotExisting() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        organizationService.updaterOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
    }

    @Test
    public void testDeleteOrganization() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);

        when(organizationRepositoryMock.findByOwner(orgOwner)).thenReturn(organization);

        organizationService.deleteOrganizationInformation();

        verify(organizationRepositoryMock, times(1)).delete(organization);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testDeleteOrganization_NotExisting() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);

        organizationService.deleteOrganizationInformation();
    }

    @Test
    public void testDeleteMember() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(organization);
        organizationService.deleteMember(defaultUser.getId());

        verify(userRepositoryMock, times(1)).save(defaultUser);
    }

    @Test(expected = NoSuchUserException.class)
    public void testDeleteMember_shouldThrowNoSuchUserException() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(organization);
        organizationService.deleteMember(100L);

    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testDeleteMember_shouldThrowNoSuchOrganizationException() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        defaultUser.setOrganization(organization);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultUser.getOrganization().getId())).thenReturn(null);
        organizationService.deleteMember(defaultUser.getId());

    }

    @Test(expected = NotOwnerOfOrganizationException.class)
    public void testDeleteMember_shouldThrowNotOwnerOrganizationException() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
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

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
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

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        when(organizationRepositoryMock.findOne(organization.getId())).thenReturn(null);
        organizationService.getUserByOrgId(organization.getId());
    }

    @Test
    public void testFindInvitableUsersByOrgId() throws Exception {
        User user3 = new User();
        user3.setId(3L);
        user3.setLogin("testUser3");

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        Organization organization = organizationService.createOrganizationInformation("testOrg1","testDescription","test@email.com",false, 1L);
        when(organizationRepositoryMock.getOne(organization.getId())).thenReturn(organization);
        when(userRepositoryMock.findInvitableUsers()).thenReturn(Arrays.asList(defaultUser,user3));
        List<User> users = organizationService.findInvitableUsersByOrgId(organization.getId());
        assertTrue(users.size()==2);
    }
}
