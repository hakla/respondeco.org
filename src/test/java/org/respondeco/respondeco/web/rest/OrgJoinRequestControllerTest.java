package org.respondeco.respondeco.web.rest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.MailService;
import org.respondeco.respondeco.service.OrgJoinRequestService;
import org.respondeco.respondeco.service.OrganizationService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.NoSuchOrgJoinRequestException;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NotOwnerOfOrganizationException;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestDTO;
import org.respondeco.respondeco.web.rest.dto.OrganizationRequestDTO;
import org.respondeco.respondeco.web.rest.dto.OrganizationResponseDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
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
import org.respondeco.respondeco.Application;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Test class for the OrgJoinRequestResource REST controller.
 *
 * @see OrgJoinRequestController
 */
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class OrgJoinRequestControllerTest {
    private static final Long DEFAULT_ORGJOINREQUEST_ID = 1L;
    private static final Long DEFAULT_ORG_ID = 1L;
    private static final Long DEFAULT_USER_ID = 1L;
    @Mock
    private OrgJoinRequestRepository orgjoinrequestRepository;
    @Mock
    private OrgJoinRequestService orgJoinRequestService;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private AccountController accountController;
    @Mock
    private MailService mailService;
    @Mock
    private OrganizationService organizationService;
    private MockMvc restOrgJoinRequestMockMvc;
    private ArgumentCaptor<Object> voidInterceptor;
    private Organization organization;
    private User defaultUser;
    private User potMember;
    private Set<Authority> userAuthorities;
    private OrgJoinRequest orgJoinRequest;
    private OrgJoinRequestDTO orgjoinrequestDTO;
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        orgJoinRequestService = spy(new OrgJoinRequestService(orgjoinrequestRepository,
            userService,userRepository,organizationRepository));
        OrgJoinRequestController orgjoinrequestController = new OrgJoinRequestController(orgJoinRequestService, userService, mailService, accountController, organizationService);
        this.restOrgJoinRequestMockMvc = MockMvcBuilders.standaloneSetup(orgjoinrequestController).build();
        userAuthorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        userAuthorities.add(authority);
        this.defaultUser = new User();
        this.defaultUser.setId(1L);
        this.defaultUser.setCreatedDate(null);
        this.defaultUser.setLastModifiedDate(null);
        this.defaultUser.setLogin("testuser");
        this.defaultUser.setCreatedBy(this.defaultUser.getLogin());
        this.defaultUser.setTitle("Dr.");
        this.defaultUser.setGender(Gender.MALE);
        this.defaultUser.setFirstName("john");
        this.defaultUser.setLastName("doe");
        this.defaultUser.setEmail("john.doe@jhipter.com");
        this.defaultUser.setDescription("just a regular everyday normal guy");
        this.defaultUser.setAuthorities(userAuthorities);
// this.defaultUser.setOrgId(1L);
        this.potMember = new User();
        this.potMember.setId(2L);
        this.potMember.setCreatedDate(null);
        this.potMember.setLastModifiedDate(null);
        this.potMember.setLogin("potMember");
        this.potMember.setCreatedBy(this.defaultUser.getLogin());
        this.potMember.setTitle("Dr.");
        this.potMember.setGender(Gender.MALE);
        this.potMember.setFirstName("john");
        this.potMember.setLastName("doe");
        this.potMember.setEmail("john.doe@jhipter.com");
        this.potMember.setDescription("just a regular everyday normal guy");
        this.potMember.setAuthorities(userAuthorities);
        userRepository.save(defaultUser);
        organization = new Organization();
        organization.setId(1L);
        organization.setName("testorg");
        organization.setDescription("testdescription");
        organization.setEmail("test@email.com");
        organization.setOwner(defaultUser);
        organization.setIsNpo(false);
        organization.setVerified(true);
        organizationRepository.save(organization);
        orgjoinrequestDTO = new OrgJoinRequestDTO();
        orgjoinrequestDTO.setOrganization(OrganizationResponseDTO.fromEntity(organization,null));
        orgjoinrequestDTO.setUser(new UserDTO(defaultUser));
        orgJoinRequest = new OrgJoinRequest();
        orgJoinRequest.setId(1L);
        orgJoinRequest.setOrganization(organization);
        orgJoinRequest.setUser(this.potMember);
        voidInterceptor = ArgumentCaptor.forType(Object.class, 0, false);
    }
    @Test
    public void testCRUDOrgJoinRequest() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepository.findByLogin(defaultUser.getLogin())).thenReturn(defaultUser);
        when(organizationRepository.findByName(organization.getName())).thenReturn(organization);
        when(organizationRepository.findOne(organization.getId())).thenReturn(organization);
        when(orgjoinrequestRepository.findOne(orgJoinRequest.getId())).thenReturn(orgJoinRequest);
        when(userRepository.findByLogin(potMember.getLogin())).thenReturn(potMember);
        when(organizationRepository.findByOwner(defaultUser)).thenReturn(organization);
        when(userRepository.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(userRepository.findOne(potMember.getId())).thenReturn(potMember);
        when(orgjoinrequestRepository.findByIdAndActiveIsTrue(orgJoinRequest.getId())).thenReturn(orgJoinRequest);
// Create OrgJoinRequest
        restOrgJoinRequestMockMvc.perform(post("/app/rest/orgjoinrequests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orgjoinrequestDTO)))
            .andExpect(status().isCreated());
        doThrow(NoSuchOrganizationException.class).when(orgJoinRequestService).createOrgJoinRequest(any(OrganizationResponseDTO.class),
            any(UserDTO.class));
        restOrgJoinRequestMockMvc.perform(post("/app/rest/orgjoinrequests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(orgjoinrequestDTO)))
            .andExpect(status().isBadRequest());
    }
    @Test
    public void testGetAll_expectOk() throws Exception {
        List<OrgJoinRequest> orgJoinRequests = new ArrayList<>();
        OrgJoinRequest orgJoinRequest1 = new OrgJoinRequest();
        orgJoinRequest1.setId(1L);
        orgJoinRequest1.setOrganization(organization);
        orgJoinRequest1.setUser(defaultUser);
        orgJoinRequests.add(orgJoinRequest1);
        doReturn(orgJoinRequests).when(orgJoinRequestService).getAll();
        restOrgJoinRequestMockMvc.perform(get("/app/rest/orgjoinrequests")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
    @Test
    public void testAcceptRequestPOST_expectOk() throws Exception {
        OrgJoinRequestDTO dto = new OrgJoinRequestDTO();
        dto.setId(1L);
        doAnswer(voidInterceptor).when(orgJoinRequestService).acceptRequest(anyLong());
        restOrgJoinRequestMockMvc.perform(post("/app/rest/orgjoinrequests/accept")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());
    }
    @Test
    public void testAcceptRequestPOST_expectNoSuchOrgJoinRequest() throws Exception {
        OrgJoinRequestDTO id = new OrgJoinRequestDTO();
        id.setId(1L);
        doThrow(NoSuchOrgJoinRequestException.class).when(orgJoinRequestService).acceptRequest(anyLong());
        restOrgJoinRequestMockMvc.perform(post("/app/rest/orgjoinrequests/accept")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(id)))
            .andExpect(status().isNotFound());
    }
    @Test
    public void testAcceptRequestPOST_expectNoSuchOrganization() throws Exception {
        OrgJoinRequestDTO dto = new OrgJoinRequestDTO();
        dto.setId(1L);
        doThrow(NoSuchOrganizationException.class).when(orgJoinRequestService).acceptRequest(anyLong());
        restOrgJoinRequestMockMvc.perform(post("/app/rest/orgjoinrequests/accept")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isNotFound());
    }
    @Test
    public void testDeclineRequestPOST_expectOk() throws Exception {
        OrgJoinRequestDTO dto = new OrgJoinRequestDTO();
        dto.setId(1L);
        doNothing().when(orgJoinRequestService).declineRequest(anyLong());
        restOrgJoinRequestMockMvc.perform(post("/app/rest/orgjoinrequests/decline")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());
    }
    @Test
    public void testDeclineRequestPOST_expectNoSuchJoinRequest() throws Exception {
        OrgJoinRequestDTO dto = new OrgJoinRequestDTO();
        dto.setId(1L);
        doThrow(NoSuchOrgJoinRequestException.class).when(orgJoinRequestService).declineRequest(anyLong());
        restOrgJoinRequestMockMvc.perform(post("/app/rest/orgjoinrequests/decline")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isNotFound());
    }
    @Test
    public void testDeclineRequestPOST_expectNoSuchOrganization() throws Exception {
        OrgJoinRequestDTO id = new OrgJoinRequestDTO();
        id.setId(1L);
        doThrow(NoSuchOrganizationException.class).when(orgJoinRequestService).declineRequest(anyLong());
        restOrgJoinRequestMockMvc.perform(post("/app/rest/orgjoinrequests/decline")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(id)))
            .andExpect(status().isNotFound());
    }
    @Test
    public void testDelete_expectOk() throws Exception {
        doNothing().when(orgJoinRequestService).delete(anyLong());
        restOrgJoinRequestMockMvc.perform(delete("/app/rest/orgjoinrequests/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }
    @Test
    public void testDelete_expectNoSuchOrgJoinRequest() throws Exception {
        doThrow(NoSuchOrgJoinRequestException.class).when(orgJoinRequestService).delete(anyLong());
        restOrgJoinRequestMockMvc.perform(delete("/app/rest/orgjoinrequests/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
    @Test
    public void testDelete_expectNotOwnerOfOrganization() throws Exception {
        doThrow(NotOwnerOfOrganizationException.class).when(orgJoinRequestService).delete(anyLong());
        restOrgJoinRequestMockMvc.perform(delete("/app/rest/orgjoinrequests/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
    }
    @Test
    public void testDelete_expectNoSuchOrganization() throws Exception {
        doThrow(NoSuchOrganizationException.class).when(orgJoinRequestService).delete(anyLong());
        restOrgJoinRequestMockMvc.perform(delete("/app/rest/orgjoinrequests/1")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}
