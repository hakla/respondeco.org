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
import org.respondeco.respondeco.repository.ImageRepository;
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

import javax.inject.Inject;
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
    private OrganizationRepository organizationRepository;

    @Mock
    private UserService userServiceMock;

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
        organizationService = new OrganizationService(organizationRepository, userServiceMock, userRepositoryMock, imageRepositoryMock);
        defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setLogin("testUser");
        orgOwner = new User();
        orgOwner.setId(2L);
        orgOwner.setLogin("testOwner");
        organizationRepository.deleteAll();
    }

    @Test
    public void testCreateOrganization() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation(name,description,email,isNpo,null);
        assertNotNull(organization);
        assertEquals(organization.getName(), name);
        assertEquals(organization.getDescription(), description);
        assertEquals(organization.getEmail(), email);
        assertEquals(organization.getIsNpo(),isNpo);
        assertEquals(organization.getOwner(),orgOwner);
    }

    @Test(expected = OrganizationAlreadyExistsException.class)
    public void testCreateOrganization_OrgNameAlreadyExists() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);
        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        organizationService.createOrganizationInformation(name,description,email,isNpo,null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrganization_NameMustNotBeEmpty() throws Exception {
        String name = "";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);
    }

    @Test(expected = AlreadyInOrganizationException.class)
    public void testCreateOrganization_AlreadyOwnerOfAnotherOrganization() throws Exception {
        String name = "testOrg1";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);

        name = "testOrg2";
        description = "testDescription";
        email = "test@email.com";
        isNpo = false;

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);
    }

    @Test
    public void testGetOrganizationByName() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);

        Organization organization = organizationService.getOrganizationByName(name);

        assertEquals(organization.getName(), name);
        assertEquals(organization.getDescription(),description);
        assertEquals(organization.getEmail(),email);
        assertEquals(organization.getIsNpo(),isNpo);
        assertEquals(organization.getOwner(), orgOwner);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testGetOrganizationByName_NotExisting() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);
        organizationService.getOrganizationByName("test");
    }

    @Test
     public void testGetOrganization() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        Organization organization = organizationService.createOrganizationInformation(name,description,email,isNpo,null);
        when(organizationRepository.findOne(1L)).thenReturn(organization);
        Organization organization2 = organizationService.getOrganization(1L);

        assertEquals(organization, organization2);
    }

    @Test
    public void testGetOrganizations() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        String name2 = "testOrg2";
        String description2 = "testDescription2";
        String email2 = "test2@email.com";
        Boolean isNpo2 = false;

        organizationService.createOrganizationInformation(name2,description2,email2,isNpo2,null);

        List<Organization> organizationList = organizationService.getOrganizations();

        assertTrue(organizationList.size()==2);
    }
    @Test
    public void testGetOrganizationByOwner() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.exists(1L)).thenReturn(true);

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);

        Organization organization = organizationService.getOrganizationByOwner();

        assertEquals(organization.getName(),name);
        assertEquals(organization.getDescription(),description);
        assertEquals(organization.getEmail(),email);
        assertEquals(organization.getIsNpo(),isNpo);
        assertEquals(organization.getOwner(), orgOwner);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testGetOrganizationByOwner_NotExisting() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);
        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        organizationService.getOrganizationByOwner();
    }

    @Test
     public void testUpdateOrganization() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);

        name = "testOrg2";
        description = "testDescription2";
        email = "test2@email.com";

        organizationService.updaterOrganizationInformation(name,description,email,isNpo,null);
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

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        organizationService.createOrganizationInformation(name,description,email,isNpo,null);

        name = "";
        description = "testDescription2";
        email = "test2@email.com";

        organizationService.updaterOrganizationInformation(name,description,email,isNpo,null);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testUpdateOrganization_NotExisting() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        String name = "testOrg2";
        String description = "testDescription2";
        String email = "test2@email.com";
        Boolean isNpo = false;

        organizationService.updaterOrganizationInformation(name, description, email, isNpo,null);
    }

    @Test
    public void testDeleteOrganization() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);

        Organization organization = organizationService.createOrganizationInformation(name, description, email, isNpo,null);

        organizationService.deleteOrganizationInformation();

        assertNull(organizationRepository.findOne(organization.getId()));
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testDeleteOrganization_NotExisting() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);

        organizationService.deleteOrganizationInformation();
    }
}
