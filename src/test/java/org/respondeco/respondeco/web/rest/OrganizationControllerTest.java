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
import org.respondeco.respondeco.web.rest.dto.OrganizationDTO;
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
import org.respondeco.respondeco.domain.Organization;
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
    private OrganizationService organizationService;

    private MockMvc restOrganizationMockMvc;

    private OrganizationDTO organizationDTO;
    private User defaultUser;
    private Set<Authority> userAuthorities;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrganizationService organizationService = new OrganizationService(organizationRepository, userService);
        OrganizationController organizationController = new OrganizationController(organizationRepository, organizationService, userService);

        userAuthorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        userAuthorities.add(authority);

        this.defaultUser = new User();
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

        organizationDTO.setName("testorg");
        organizationDTO.setDescription("testdescription");
        organizationDTO.setEmail("testorg@email.com");
        organizationDTO.setOwner(defaultUser.getLogin());
        organizationDTO.setNpo(false);
    }

    @Test
    public void testCRUDOrganization() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        // Create Organization
        restOrganizationMockMvc.perform(post("/app/rest/organizations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationDTO)))
                .andExpect(status().isOk());

        // Read Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{orgName}", "testorg"))
        .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                 .andExpect(jsonPath("$.name").value("testorg"))
                 .andExpect(jsonPath("$.description").value("testdescription"))
                 .andExpect(jsonPath("$.email").value("testorg@email.com"))
                 .andExpect(jsonPath("$.owner").value(this.defaultUser.getLogin()))
                 .andExpect(jsonPath("$.isNpo").value(false));
    /*
        // Update Organization
        organizationDTO.setDescription(UPDATED_DESCRIPTION);
        organizationDTO.setEmail(UPDATED_EMAIL);
        organizationDTO.setOwner(UPDATED_OWNER);
        organizationDTO.setNpo(UPDATED_IS_NPO);

        restOrganizationMockMvc.perform(post("/app/rest/organizations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationDTO)))
                .andExpect(status().isOk());

        // Read updated Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{orgName}", DEFAULT_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.description").value(UPDATED_DESCRIPTION.toString()))
                .andExpect(jsonPath("$.email").value(UPDATED_EMAIL.toString()))
                .andExpect(jsonPath("$.owner").value(UPDATED_OWNER.toString()))
                .andExpect(jsonPath("$.isNpo").value(UPDATED_IS_NPO.booleanValue()));

        // Delete Organization
        restOrganizationMockMvc.perform(delete("/app/rest/organizations/{orgName}", DEFAULT_NAME)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Read nonexisting Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{orgName}", DEFAULT_NAME)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
*/
    }
}
