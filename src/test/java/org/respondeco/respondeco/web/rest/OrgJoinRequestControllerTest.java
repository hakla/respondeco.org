package org.respondeco.respondeco.web.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.OrgJoinRequestService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.respondeco.respondeco.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * Test class for the OrgJoinRequestResource REST controller.
 *
 * @see OrgJoinRequestController
 */
@RunWith(SpringJUnit4ClassRunner.class)
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

    private MockMvc restOrgJoinRequestMockMvc;

    private OrgJoinRequestDTO orgjoinrequestDTO;

    private Organization organization;
    private User defaultUser;
    private User potMember;
    private Set<Authority> userAuthorities;
    private OrgJoinRequest orgJoinRequest;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrgJoinRequestService orgJoinRequestService = new OrgJoinRequestService(orgjoinrequestRepository,userService,userRepository,organizationRepository);
        OrgJoinRequestController orgjoinrequestController = new OrgJoinRequestController(orgjoinrequestRepository, orgJoinRequestService);

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
        this.defaultUser.setOrgId(1L);

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
        organizationRepository.save(organization);


        orgjoinrequestDTO = new OrgJoinRequestDTO();

        orgjoinrequestDTO.setOrgName(organization.getName());
        orgjoinrequestDTO.setUserLogin(defaultUser.getLogin());

        orgJoinRequest = new OrgJoinRequest();
        orgJoinRequest.setId(1L);
        orgJoinRequest.setOrganization(organization);
        orgJoinRequest.setUser(this.potMember);
    }

    @Test
    public void testCRUDOrgJoinRequest() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepository.findByLogin(defaultUser.getLogin())).thenReturn(defaultUser);
        when(organizationRepository.findByName(organization.getName())).thenReturn(organization);
        when(organizationRepository.findOne(1L)).thenReturn(organization);
        when(orgjoinrequestRepository.findOne(1L)).thenReturn(orgJoinRequest);

        // Create OrgJoinRequest
        restOrgJoinRequestMockMvc.perform(post("/app/rest/orgjoinrequests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orgjoinrequestDTO)))
                .andExpect(status().isOk());

        // Read OrgJoinRequest
        restOrgJoinRequestMockMvc.perform(get("/app/rest/orgjoinrequests/{orgName}", organization.getName()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        when(userService.getUserWithAuthorities()).thenReturn(potMember);

        // Accept OrgJoinRequest
        restOrgJoinRequestMockMvc.perform(delete("/app/rest/orgjoinrequests/accept/{id}", 1L))
                .andExpect(status().isOk());

        // Decline OrgJoinRequest
        restOrgJoinRequestMockMvc.perform(delete("/app/rest/orgjoinrequests/decline/{id}", 1L))
                .andExpect(status().isOk());


        // Read nonexisting OrgJoinRequest
        restOrgJoinRequestMockMvc.perform(get("/app/rest/orgjoinrequests/{id}", 1L)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }
}
