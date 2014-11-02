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
import org.mockito.MockitoAnnotations;
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
    
    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
        
    private static final String DEFAULT_DESCRIPTION = "SAMPLE_TEXT";
    private static final String UPDATED_DESCRIPTION = "UPDATED_TEXT";
        
    private static final String DEFAULT_EMAIL = "SAMPLE_TEXT";
    private static final String UPDATED_EMAIL = "UPDATED_TEXT";
        
    private static final String DEFAULT_OWNER = "SAMPLE_TEXT";
    private static final String UPDATED_OWNER = "UPDATED_TEXT";
        
    private static final Boolean DEFAULT_IS_NPO = false;
    private static final Boolean UPDATED_IS_NPO = true;
    @Inject
    private OrganizationRepository organizationRepository;

    private MockMvc restOrganizationMockMvc;

    private Organization organization;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        OrganizationController organizationController = new OrganizationController();
        ReflectionTestUtils.setField(organizationController, "organizationRepository", organizationRepository);

        this.restOrganizationMockMvc = MockMvcBuilders.standaloneSetup(organizationController).build();

        organization = new Organization();

        organization.setName(DEFAULT_NAME);
        organization.setDescription(DEFAULT_DESCRIPTION);
        organization.setEmail(DEFAULT_EMAIL);
        organization.setOwner(DEFAULT_OWNER);
        organization.setIsNpo(DEFAULT_IS_NPO);
    }

    @Test
    public void testCRUDOrganization() throws Exception {

        // Create Organization
        restOrganizationMockMvc.perform(post("/app/rest/organizations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organization)))
                .andExpect(status().isOk());

        // Read Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{orgName}", DEFAULT_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
                .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER.toString()))
                .andExpect(jsonPath("$.isNpo").value(DEFAULT_IS_NPO.booleanValue()));

        // Update Organization
        organization.setName(UPDATED_NAME);
        organization.setDescription(UPDATED_DESCRIPTION);
        organization.setEmail(UPDATED_EMAIL);
        organization.setOwner(UPDATED_OWNER);
        organization.setIsNpo(UPDATED_IS_NPO);

        restOrganizationMockMvc.perform(post("/app/rest/organizations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organization)))
                .andExpect(status().isOk());

        // Read updated Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{orgName}", DEFAULT_NAME))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(UPDATED_NAME.toString()))
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

    }
}
