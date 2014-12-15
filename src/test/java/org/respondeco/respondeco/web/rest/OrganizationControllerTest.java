package org.respondeco.respondeco.web.rest;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
import org.respondeco.respondeco.repository.ImageRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.*;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.OrganizationRequestDTO;
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
import org.respondeco.respondeco.repository.OrganizationRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test class for the OrganizationController REST controller.
 *
 * @see OrganizationController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class OrganizationControllerTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ResourceService resourceService;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private OrgJoinRequestService orgJoinRequestService;

    @Mock
    private RatingService ratingService;

    private MockMvc restOrganizationMockMvc;

    private OrganizationRequestDTO organizationRequestDTO;
    private User defaultUser;
    private Set<Authority> userAuthorities;
    private UserDTO defaultUserDTO;

    private static final String DEFAULT_ORGNAME = "testorg";
    private static final String UPDATED_ORGNAME = "testorg1";

    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final String DEFAULT_EMAIL = "SAMPLE@EMAIL.COM";
    private static final String UPDATED_EMAIL = "UPDATED@EMAIL.COM";

    private static final Boolean DEFAULT_NPO = true;
    private static final Boolean UPDATED_NPO = false;

    private static final Long ID = new Long(1L);
    private Organization defaultOrganization;
    private OrgJoinRequest orgJoinRequest;
    private User inviteAbleUser;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrganizationService organizationService = new OrganizationService(
                organizationRepository,
                userService,
                userRepository,
                imageRepository);

        OrganizationController organizationController =
            new OrganizationController(
                    organizationService,
                    userService,
                    resourceService,
                    orgJoinRequestService,
                    ratingService);

        userAuthorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        userAuthorities.add(authority);

        defaultUser = new User();
        defaultUser.setId(ID);
        defaultUser.setCreatedDate(null);
        defaultUser.setLastModifiedDate(null);
        defaultUser.setLogin("testuser");
        defaultUser.setCreatedBy(this.defaultUser.getLogin());
        defaultUser.setTitle("Dr.");
        defaultUser.setGender(Gender.MALE);
        defaultUser.setFirstName("john");
        defaultUser.setLastName("doe");
        defaultUser.setEmail("john.doe@jhipter.com");
        defaultUser.setDescription("just a regular everyday normal guy");
        defaultUser.setAuthorities(userAuthorities);

        defaultUserDTO = new UserDTO(defaultUser);
        defaultUserDTO.setId(defaultUser.getId());

        inviteAbleUser = new User();
        inviteAbleUser.setId(2L);
        inviteAbleUser.setLogin("inviteUser");

        restOrganizationMockMvc = MockMvcBuilders.standaloneSetup(organizationController).build();

        organizationRequestDTO = new OrganizationRequestDTO();
        organizationRequestDTO.setId(ID);
        organizationRequestDTO.setName(DEFAULT_ORGNAME);
        organizationRequestDTO.setDescription(DEFAULT_DESCRIPTION);
        organizationRequestDTO.setEmail(DEFAULT_EMAIL);
        organizationRequestDTO.setNpo(DEFAULT_NPO);
        organizationRequestDTO.setOwner(defaultUserDTO);

        defaultOrganization = new Organization();
        defaultOrganization.setId(1l);
        defaultOrganization.setName(DEFAULT_ORGNAME);
        defaultOrganization.setDescription(DEFAULT_DESCRIPTION);
        defaultOrganization.setEmail(DEFAULT_EMAIL);
        defaultOrganization.setIsNpo(DEFAULT_NPO);
        defaultOrganization.setOwner(defaultUser);

        orgJoinRequest = new OrgJoinRequest();
        orgJoinRequest.setId(1L);
        orgJoinRequest.setUser(inviteAbleUser);
        orgJoinRequest.setOrganization(defaultOrganization);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);

    }

    @Test
    public void testCRUDOrganization() throws Exception {

        doReturn(defaultOrganization).when(organizationService).createOrganizationInformation(
                organizationRequestDTO.getName(),
                organizationRequestDTO.getDescription(),
                organizationRequestDTO.getEmail(),
                organizationRequestDTO.isNpo(),
                organizationRequestDTO.getLogo());


        // Create Organization
        restOrganizationMockMvc.perform(post("/app/rest/organizations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationRequestDTO)))
                .andExpect(status().isOk());

        // Read All Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        doReturn(defaultOrganization).when(organizationRepository).findOne(organizationRequestDTO.getId());

        // Read Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}", organizationRequestDTO.getId()))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                 .andExpect(jsonPath("$.name").value(DEFAULT_ORGNAME))
                 .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
                 .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                 .andExpect(jsonPath("$.isNpo").value(DEFAULT_NPO));


        // Update Organization
        organizationRequestDTO.setId(defaultOrganization.getId());
        organizationRequestDTO.setName(UPDATED_ORGNAME);
        organizationRequestDTO.setDescription(UPDATED_DESCRIPTION);
        organizationRequestDTO.setEmail(UPDATED_EMAIL);
        organizationRequestDTO.setNpo(UPDATED_NPO);

        doReturn(defaultOrganization).when(organizationRepository).findByOwner(defaultUser);

        restOrganizationMockMvc.perform(put("/app/rest/organizations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationRequestDTO)))
                .andExpect(status().isOk());

        // Read updated Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}", organizationRequestDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(UPDATED_ORGNAME))
                .andExpect(jsonPath("$.description").value(UPDATED_DESCRIPTION))
                .andExpect(jsonPath("$.email").value(UPDATED_EMAIL))
                .andExpect(jsonPath("$.isNpo").value(UPDATED_NPO));

        // Delete Organization
        restOrganizationMockMvc.perform(delete("/app/rest/organizations")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        doReturn(null).when(organizationRepository).findOne(defaultOrganization.getId());

        // Read nonexisting Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}", organizationRequestDTO.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetOrgJoinRequestsByOrgId() throws Exception{
        doReturn(Arrays.asList(orgJoinRequest))
                .when(orgJoinRequestService).getOrgJoinRequestByOrganization(defaultOrganization.getId());

        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}/orgjoinrequests", defaultOrganization.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetMembers() throws Exception{
        defaultUser.setOrganization(defaultOrganization);
        inviteAbleUser.setOrganization(defaultOrganization);
        doReturn(defaultOrganization).when(organizationRepository).findOne(defaultOrganization.getId());
        doReturn(Arrays.asList(defaultUser,inviteAbleUser)).when(userRepository).findUsersByOrganizationId(defaultOrganization.getId());

        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}/members", defaultOrganization.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testDeleteMember() throws Exception{
        defaultUser.setOrganization(defaultOrganization);
        inviteAbleUser.setOrganization(defaultOrganization);
        doReturn(inviteAbleUser).when(userRepository).findOne(inviteAbleUser.getId());
        doReturn(defaultOrganization).when(organizationRepository).findOne(defaultOrganization.getId());


        restOrganizationMockMvc.perform(delete("/app/rest/organizations/{id}/members/{userId}", defaultOrganization.getId(), inviteAbleUser.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
    }
}
