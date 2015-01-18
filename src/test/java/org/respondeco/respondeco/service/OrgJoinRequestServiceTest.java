package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.OrganizationResponseDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
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
public class OrgJoinRequestServiceTest {

    @Mock
    private OrgJoinRequestRepository orgJoinRequestRepositoryMock;

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private ImageRepository imageRepositoryMock;

    @Mock
    private ProjectService projectService;

    @Mock
    private PostingFeedRepository postingFeedRepository;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private ResourceOfferRepository resourceOfferRepositoryMock;

    private OrgJoinRequestService orgJoinRequestService;

    private User orgOwner;
    private User defaultUser;
    private Organization defaultOrganization;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        orgJoinRequestService = new OrgJoinRequestService(
                orgJoinRequestRepositoryMock,
                userService,
                userRepositoryMock,
                organizationRepositoryMock);

        organizationService = new OrganizationService(
                organizationRepositoryMock,
                userService,
                userRepositoryMock,
                imageRepositoryMock,
                projectService,
                projectRepositoryMock,
                postingFeedRepository,
                resourceOfferRepositoryMock);

        defaultUser = new User();
        defaultUser.setId(2L);
        defaultUser.setLogin("testUser");

        orgOwner = new User();
        orgOwner.setId(1L);
        orgOwner.setLogin("orgOwner");

        defaultOrganization = new Organization();
        defaultOrganization.setName("testOrg");
        defaultOrganization.setId(1L);
        defaultOrganization.setOwner(orgOwner);
        defaultOrganization.setVerified(true);

        orgOwner.setOrganization(defaultOrganization);
    }

    @Test
    public void testCreateOrgJoinRequest() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);
        assertEquals(orgJoinRequest.getOrganization(),defaultOrganization);
        assertEquals(orgJoinRequest.getUser(), defaultUser);

        verify(orgJoinRequestRepositoryMock, times(1)).save(isA(OrgJoinRequest.class));
    }

    @Test(expected = NoSuchUserException.class)
    public void testCreateOrgJoinRequest_NotExistingUser() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(null);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO());
    }


    @Test(expected = NoSuchOrganizationException.class)
    public void testCreateOrgJoinRequest_NotExistingOrganization() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(null);

        orgJoinRequestService.createOrgJoinRequest(new OrganizationResponseDTO(), new UserDTO(defaultUser));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrgJoinRequest_NotOwnerOfOrganization() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(defaultUser));
    }

    @Test(expected = AlreadyInvitedToOrganizationException.class)
    public void testCreateOrgJoinRequest_AlreadyInvitedToOrganization() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(defaultUser));

        when(orgJoinRequestRepositoryMock.findByUserAndOrganizationAndActiveIsTrue(defaultUser,defaultOrganization)).thenReturn(orgJoinRequest);
        orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(defaultUser));
    }

    @Test
    public void testGetOrgJoinRequestByOrganization() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(defaultUser));

        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        when(orgJoinRequestRepositoryMock.findByOrganizationAndActiveIsTrue(defaultOrganization)).thenReturn(Arrays.asList(orgJoinRequest));
        List<OrgJoinRequest> orgJoinRequestList = orgJoinRequestService.getOrgJoinRequestByOrganization(defaultOrganization.getId());

        assertNotNull(orgJoinRequest);
        assertTrue(orgJoinRequestList.size()==1);
        verify(orgJoinRequestRepositoryMock, times(1)).findByOrganizationAndActiveIsTrue(defaultOrganization);

    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testGetOrgJoinRequestByOrganization_NoSuchOrganization() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(new OrganizationResponseDTO(), new UserDTO(defaultUser));

        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(null);

        orgJoinRequestService.getOrgJoinRequestByOrganization(defaultOrganization.getId());

    }

    @Test
     public void testGetOrgJoinRequestByCurrentUser() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(defaultUser));

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(orgJoinRequestRepositoryMock.findByUserAndActiveIsTrue(defaultUser)).thenReturn(Arrays.asList(orgJoinRequest));
        List<OrgJoinRequest> orgJoinRequestList = orgJoinRequestService.getOrgJoinRequestByCurrentUser();

        assertNotNull(orgJoinRequestList);
        verify(orgJoinRequestRepositoryMock, times(1)).findByUserAndActiveIsTrue(defaultUser);
    }

    @Test
    public void testGetAllOrgJoinRequests() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(defaultUser));

        when(orgJoinRequestRepositoryMock.findAll()).thenReturn(Arrays.asList(orgJoinRequest));
        List<OrgJoinRequest> orgJoinRequestList = orgJoinRequestService.getAll();

        assertNotNull(orgJoinRequestList);
        verify(orgJoinRequestRepositoryMock, times(1)).findAll();
    }

    @Test
    public void testAcceptOrgJoinRequest() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(orgJoinRequestRepositoryMock.findByIdAndActiveIsTrue(orgJoinRequest.getId())).thenReturn(orgJoinRequest);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());
        verify(userRepositoryMock, times(1)).save(isA(User.class));
        verify(orgJoinRequestRepositoryMock, times(2)).save(isA(OrgJoinRequest.class));
    }

    @Test(expected = NoSuchOrgJoinRequestException.class)
    public void testAcceptOrgJoinRequest_NotExistingOrgJoinRequest() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(orgJoinRequestRepositoryMock.findByIdAndActiveIsTrue(orgJoinRequest.getId())).thenReturn(null);
        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testAcceptOrgJoinRequest_NotExistingOrganization() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(orgJoinRequestRepositoryMock.findByIdAndActiveIsTrue(orgJoinRequest.getId())).thenReturn(orgJoinRequest);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(null);
        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAcceptOrgJoinRequest_UserNotOfOrgJoinRequest() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(orgJoinRequestRepositoryMock.findByIdAndActiveIsTrue(orgJoinRequest.getId())).thenReturn(orgJoinRequest);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());
    }

//    @Test(expected = AlreadyInOrganizationException.class)
//    public void testAcceptOrgJoinRequest_AlreadyInOrganization() throws Exception {
//
//        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
//        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
//        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
//
//        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
//        assertNotNull(orgJoinRequest);
//
//      //  defaultUser.setOrgId(1L);
//        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
//        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
//        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());
//    }

    @Test
    public void testDeclineOrgJoinRequest() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(orgJoinRequestRepositoryMock.findByIdAndActiveIsTrue(orgJoinRequest.getId())).thenReturn(orgJoinRequest);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        orgJoinRequestService.declineRequest(orgJoinRequest.getId());

        verify(orgJoinRequestRepositoryMock, times(2)).save(isA(OrgJoinRequest.class));
    }

    @Test(expected = NoSuchOrgJoinRequestException.class)
    public void testDeclineOrgJoinRequest_NotExistingOrgJoinRequest() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(orgJoinRequestRepositoryMock.findByIdAndActiveIsTrue(orgJoinRequest.getId())).thenReturn(null);
        orgJoinRequestService.declineRequest(orgJoinRequest.getId());
    }

//    @Test(expected = NoSuchOrganizationException.class)
//    public void testDeclineOrgJoinRequest_NotExistingOrganization() throws Exception {
//
//        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
//        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
//        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
//
//        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
//        assertNotNull(orgJoinRequest);
//
//        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
//        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(null);
//        orgJoinRequestService.declineRequest(orgJoinRequest.getId());
//    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeclineOrgJoinRequest_UserNotOfOrgJoinRequest() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(orgJoinRequestRepositoryMock.findByIdAndActiveIsTrue(orgJoinRequest.getId())).thenReturn(orgJoinRequest);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());
    }

    @Test
    public void testDeleteOrgJoinRequest() throws Exception {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(orgJoinRequestRepositoryMock.findOne(orgJoinRequest.getId())).thenReturn(orgJoinRequest);

        orgJoinRequestService.delete(orgJoinRequest.getId()); //orgOwner.getOrganization==null ?!?!

        verify(orgJoinRequestRepositoryMock, times(1)).delete(orgJoinRequest.getId());
    }

    @Test(expected = NoSuchOrgJoinRequestException.class)
    public void testDeleteOrgJoinRequest_NotExistingOrgJoinRequest() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(orgJoinRequestRepositoryMock.findOne(orgJoinRequest.getId())).thenReturn(null);
        orgJoinRequestService.delete(orgJoinRequest.getId());
    }

    @Test(expected = NotOwnerOfOrganizationException.class)
    public void testDeleteOrgJoinRequest_NotOwnerOfOrganization() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(orgJoinRequestRepositoryMock.findOne(orgJoinRequest.getId())).thenReturn(orgJoinRequest);
        orgJoinRequestService.delete(orgJoinRequest.getId());
    }
}
