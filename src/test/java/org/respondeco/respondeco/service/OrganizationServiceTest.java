package org.respondeco.respondeco.service;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.TextMessage;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.TextMessageRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.AlreadyInOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.OrganizationAlreadyExistsException;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
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

    private OrganizationService organizationService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        organizationService = new OrganizationService(organizationRepositoryMock, userServiceMock);
    }

    @Test
    public void testCreateOrganization() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        Organization organization = organizationService.createOrganizationInformation(name,description,email,isNpo);
        assertNotNull(organization);
        assertEquals(organization.getName(), name);
        assertEquals(organization.getDescription(), description);
        assertEquals(organization.getEmail(), email);
        assertEquals(organization.getIsNpo(),isNpo);
        assertEquals(organization.getOwner(),currentUser.getId());

        verify(organizationRepositoryMock, times(1)).save(isA(Organization.class));
        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(userRepositoryMock, times(1)).exists(1L);
    }

    @Test(expected = OrganizationAlreadyExistsException.class)
    public void testCreateOrganization_OrgNameAlreadyExists() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo);
        organizationService.createOrganizationInformation(name,description,email,isNpo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganization_NameMustNotBeEmpty() throws Exception {
        String name = "";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo);
    }
    @Test(expected = AlreadyInOrganizationException.class)
    public void testCreateOrganization_AlreadyOwnerOfAnotherOrganization() throws Exception {
        String name = "testOrg1";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo);

        name = "testOrg2";
        description = "testDescription";
        email = "test@email.com";
        isNpo = false;

        organizationService.createOrganizationInformation(name,description,email,isNpo);
    }

    @Test
    public void testGetOrganizationByName() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        Organization organization1 = organizationService.createOrganizationInformation(name,description,email,isNpo);

        Organization organization2 = organizationService.getOrganizationByName(name);

        assertEquals(organization1.getName(),organization2.getName());
        assertEquals(organization1.getDescription(),organization2.getDescription());
        assertEquals(organization1.getEmail(),organization2.getEmail());
        assertEquals(organization1.getIsNpo(),organization2.getIsNpo());
        assertEquals(organization1.getOwner(), organization2.getOwner());
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testGetOrganizationByName_NotExisting() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo);
        organizationService.getOrganizationByName("test");
    }

    @Test
    public void testGetOrganizationByOwner() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        Organization organization1 = organizationService.createOrganizationInformation(name,description,email,isNpo);

        Organization organization2 = organizationService.getOrganizationByOwner();

        assertEquals(organization1.getName(),organization2.getName());
        assertEquals(organization1.getDescription(),organization2.getDescription());
        assertEquals(organization1.getEmail(),organization2.getEmail());
        assertEquals(organization1.getIsNpo(),organization2.getIsNpo());
        assertEquals(organization1.getOwner(), organization2.getOwner());
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testGetOrganizationByOwner_NotExisting() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("testUser");

        User currentUser = new User();
        currentUser.setId(2L);
        currentUser.setLogin("currentUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        organizationService.createOrganizationInformation(name,description,email,isNpo);
        organizationService.getOrganizationByOwner();
    }

    @Test
     public void testUpdateOrganization() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        organizationService.createOrganizationInformation(name,description,email,isNpo);

        name = "testOrg2";
        description = "testDescription2";
        email = "test2@email.com";

        organizationService.updaterOrganizationInformation(name,description,email,isNpo);
        Organization organization = organizationService.getOrganizationByName(name);

        assertEquals(organization.getName(),name);
        assertEquals(organization.getDescription(),description);
        assertEquals(organization.getEmail(),email);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateOrganization_WithEmptyName() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        organizationService.createOrganizationInformation(name,description,email,isNpo);

        name = "";
        description = "testDescription2";
        email = "test2@email.com";

        organizationService.updaterOrganizationInformation(name,description,email,isNpo);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testUpdateOrganization_NotExisting() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("testUser");

        User currentUser = new User();
        currentUser.setId(2L);
        currentUser.setLogin("currentUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        name = "testOrg2";
        description = "testDescription2";
        email = "test2@email.com";

        organizationService.updaterOrganizationInformation(name, description, email, isNpo);
    }

    @Test
    public void testDeleteOrganization() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User currentUser = new User();
        currentUser.setId(1L);
        currentUser.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        Organization organization = organizationService.createOrganizationInformation(name, description, email, isNpo);

        organizationService.deleteOrganizationInformation();

        assertNull(organization);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testDeleteOrganization_NotExisting() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("testUser");

        User currentUser = new User();
        currentUser.setId(2L);
        currentUser.setLogin("currentUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(currentUser);

        organizationService.deleteOrganizationInformation();
    }

}
