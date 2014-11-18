package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrgJoinRequestRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.AlreadyInOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.OrganizationAlreadyExistsException;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Created by Christoph Schiessl on 16/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class OrgJoinRequestServiceTest {

    @Mock
    private OrgJoinRequestRepository orgJoinRequestRepositoryMock;

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    private OrgJoinRequestService orgJoinRequestService;

    private OrganizationService organizationService;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        orgJoinRequestService = new OrgJoinRequestService(orgJoinRequestRepositoryMock, userServiceMock, userRepositoryMock, organizationRepositoryMock);
        organizationService = new OrganizationService(organizationRepositoryMock,userServiceMock,userRepositoryMock);
    }

    @Test
    public void testCreateOrgJoinRequest() throws Exception {
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

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(organization.getName(),currentUser.getLogin());
        assertNotNull(orgJoinRequest);
        assertEquals(orgJoinRequest.getOrgId(),organization.getId());
        assertEquals(orgJoinRequest.getUserId(),currentUser.getId());


        verify(orgJoinRequestRepositoryMock, times(1)).save(isA(OrgJoinRequest.class));
        verify(organizationRepositoryMock, times(1)).save(isA(Organization.class));
        verify(userServiceMock, times(1)).getUserWithAuthorities();
        verify(userRepositoryMock, times(1)).exists(1L);
    }

    @Test(expected = NoSuchUserException.class)
    public void testCreateOrgJoinRequest_NotExistingUser() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("testUser");

        User currentUser = null;

        when(userServiceMock.getUserWithAuthorities()).thenReturn(user1);

        Organization organization = organizationService.createOrganizationInformation(name,description,email,isNpo);
        assertNotNull(organization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(organization.getName(),currentUser.getLogin());
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testCreateOrgJoinRequest_NotExistingOrganization() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("testUser");

        when(userServiceMock.getUserWithAuthorities()).thenReturn(user1);

        Organization organization = organizationService.createOrganizationInformation(name,description,email,isNpo);
        assertNotNull(organization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest("testOrg1",user1.getLogin());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrgJoinRequest_NotOwnerOfOrganization() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("testUser");

        User user2 = new User();
        user2.setId(2L);
        user2.setLogin("testUser2");
        when(userServiceMock.getUserWithAuthorities()).thenReturn(user2);

        Organization organization = organizationService.createOrganizationInformation(name,description,email,isNpo);
        assertNotNull(organization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(organization.getName(),user1.getLogin());
    }

    @Test
        public void testGetOrgJoinRequestByOrgName() throws Exception {
            String name = "testOrg";
            String description = "testDescription";
            String email = "test@email.com";
            Boolean isNpo = false;
            User user1 = new User();
            user1.setId(1L);
            user1.setLogin("testUser");


            when(userServiceMock.getUserWithAuthorities()).thenReturn(user1);

            Organization organization = organizationService.createOrganizationInformation(name,description,email,isNpo);
            assertNotNull(organization);

            OrgJoinRequest orgJoinRequest1 = orgJoinRequestService.createOrgJoinRequest(organization.getName(),user1.getLogin());
            assertNotNull(orgJoinRequest1);

            List<OrgJoinRequest> orgJoinRequestList = orgJoinRequestService.getOrgJoinRequestByOrgName(organization.getName());
            OrgJoinRequest orgJoinRequest2 = orgJoinRequestList.get(0);
            assertNotNull(orgJoinRequest2);

            assertEquals(orgJoinRequest1.getOrgId(),orgJoinRequest2.getOrgId());
            assertEquals(orgJoinRequest1.getUserId(),orgJoinRequest2.getUserId());
        }

    @Test(expected = NoSuchOrganizationException.class)
    public void testGetOrgJoinRequestByOrgName_NoSuchOrganization() throws Exception {
        orgJoinRequestService.getOrgJoinRequestByOrgName("test");
    }

    @Test
     public void testGetOrgJoinRequestByOwner() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("testUser");


        when(userServiceMock.getUserWithAuthorities()).thenReturn(user1);

        Organization organization = organizationService.createOrganizationInformation(name,description,email,isNpo);
        assertNotNull(organization);

        OrgJoinRequest orgJoinRequest1 = orgJoinRequestService.createOrgJoinRequest(organization.getName(),user1.getLogin());
        assertNotNull(orgJoinRequest1);

        List<OrgJoinRequest> orgJoinRequestList = orgJoinRequestService.getOrgJoinRequestByCurrentUser();
        OrgJoinRequest orgJoinRequest2 = orgJoinRequestList.get(0);
        assertNotNull(orgJoinRequest2);

        assertEquals(orgJoinRequest1.getOrgId(),orgJoinRequest2.getOrgId());
        assertEquals(orgJoinRequest1.getUserId(),orgJoinRequest2.getUserId());
    }

    @Test
    public void testAcceptOrgJoinRequest() throws Exception {
        String name = "testOrg";
        String description = "testDescription";
        String email = "test@email.com";
        Boolean isNpo = false;
        User user1 = new User();
        user1.setId(1L);
        user1.setLogin("testUser");


        when(userServiceMock.getUserWithAuthorities()).thenReturn(user1);

        Organization organization = organizationService.createOrganizationInformation(name,description,email,isNpo);
        assertNotNull(organization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(organization.getName(),user1.getLogin());
        assertNotNull(orgJoinRequest);
        Long id = orgJoinRequest.getId();
        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());

        assertNull(orgJoinRequestRepositoryMock.findOne(id));
    }

}
