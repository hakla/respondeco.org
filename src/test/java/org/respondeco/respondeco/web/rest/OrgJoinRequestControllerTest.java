package org.respondeco.respondeco.web.rest;

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
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestDTO;
import org.respondeco.respondeco.web.rest.dto.OrganizationDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private static final Long UPDATED_ID = 3L;
        
    @Inject
    private OrgJoinRequestRepository orgjoinrequestRepository;

    private MockMvc restOrgJoinRequestMockMvc;

    private OrgJoinRequestDTO orgjoinrequestDTO;

    private Organization organization;
    private User defaultUser;
    private Set<Authority> userAuthorities;

    @Mock
    private OrgJoinRequestService orgJoinRequestService;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrgJoinRequestService orgJoinRequestService = new OrgJoinRequestService(orgjoinrequestRepository,userService,userRepository,organizationRepository);
        OrgJoinRequestController orgjoinrequestController = new OrgJoinRequestController(orgjoinrequestRepository, orgJoinRequestService);
        ReflectionTestUtils.setField(orgjoinrequestController, "orgjoinrequestRepository", orgjoinrequestRepository);

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

        organization = new Organization();

        organization.setId(1L);
        organization.setName("testorg");
        organization.setDescription("testdescription");
        organization.setEmail("test@email.com");
        organization.setOwner(defaultUser.getId());
        organization.setIsNpo(false);
        organizationRepository.save(organization);


        orgjoinrequestDTO = new OrgJoinRequestDTO();

        orgjoinrequestDTO.setOrgId(organization.getId());
        orgjoinrequestDTO.setUserlogin(defaultUser.getLogin());
    }

    @Test
    public void testCRUDOrgJoinRequest() throws Exception {

        // Create OrgJoinRequest
        restOrgJoinRequestMockMvc.perform(post("/app/rest/orgjoinrequests")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(orgjoinrequestDTO)))
                .andExpect(status().isOk());
/*
        // Read OrgJoinRequest
        restOrgJoinRequestMockMvc.perform(get("/app/rest/orgjoinrequests/{id}", UPDATED_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
                .andExpect(jsonPath("$.orgId").value(DEFAULT_ORG_ID.intValue()))
                .andExpect(jsonPath("$.userLogin").value(DEFAULT_USER_LOGIN.toString()));

        // Read nonexisting OrgJoinRequest
        restOrgJoinRequestMockMvc.perform(get("/app/rest/orgjoinrequests/{id}", DEFAULT_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    */
    }
}
