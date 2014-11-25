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
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.OrganizationService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.OrganizationDTO;
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

    @Inject
    private OrganizationRepository organizationRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationService organizationService;

    private MockMvc restOrganizationMockMvc;

    private OrganizationDTO organizationDTO;
    private User defaultUser;
    private Set<Authority> userAuthorities;

    private static final String DEFAULT_ORGNAME = "testorg";
    private static final String UPDATED_ORGNAME = "testorg1";

    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";

    private static final String DEFAULT_EMAIL = "SAMPLE@EMAIL.COM";
    private static final String UPDATED_EMAIL = "UPDATED@EMAIL.COM";

    private static final Boolean DEFAULT_NPO = true;
    private static final Boolean UPDATED_NPO = false;

    private static final Long ID = new Long(1L);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrganizationService organizationService = new OrganizationService(organizationRepository, userService, userRepository);
        OrganizationController organizationController = new OrganizationController(organizationRepository, organizationService, userService);

        userAuthorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        userAuthorities.add(authority);

        this.defaultUser = new User();
        this.defaultUser.setId(ID);
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

        this.restOrganizationMockMvc = MockMvcBuilders.standaloneSetup(organizationController).build();

        organizationDTO = new OrganizationDTO();

        organizationDTO.setName(DEFAULT_ORGNAME);
        organizationDTO.setDescription(DEFAULT_DESCRIPTION);
        organizationDTO.setEmail(DEFAULT_EMAIL);
        organizationDTO.setNpo(DEFAULT_NPO);

        organizationRepository.deleteAll();
    }

    @Test
    public void testCRUDOrganization() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        // Create Organization
        restOrganizationMockMvc.perform(post("/app/rest/organizations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationDTO)))
                .andExpect(status().isOk());

        // Read All Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // Read Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{orgName}", DEFAULT_ORGNAME))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                 .andExpect(jsonPath("$.name").value(DEFAULT_ORGNAME))
                 .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
                 .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                 .andExpect(jsonPath("$.isNpo").value(DEFAULT_NPO));

        // Read my Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/myOrganization", DEFAULT_ORGNAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(DEFAULT_ORGNAME))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                .andExpect(jsonPath("$.isNpo").value(DEFAULT_NPO));

        // Update Organization
        organizationDTO.setName(UPDATED_ORGNAME);
        organizationDTO.setDescription(UPDATED_DESCRIPTION);
        organizationDTO.setEmail(UPDATED_EMAIL);
        organizationDTO.setNpo(UPDATED_NPO);

        restOrganizationMockMvc.perform(post("/app/rest/organizations/updateOrganization")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationDTO)))
                .andExpect(status().isOk());

        // Read updated Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{orgName}", UPDATED_ORGNAME))
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

        // Read nonexisting Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{orgName}", UPDATED_ORGNAME)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }
}
