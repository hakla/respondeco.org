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
import org.respondeco.respondeco.testutil.TestUtil;
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
import org.respondeco.respondeco.domain.Logo;
import org.respondeco.respondeco.repository.LogoRepository;

/**
 * Test class for the LogoController REST controller.
 *
 * @see LogoController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class LogoControllerTest {
    
    private static final Long DEFAULT_ID = new Long(1L);
    
    private static final Long DEFAULT_ORG_ID = new Long(0L);
        
    private static final String DEFAULT_LABEL = "SAMPLE_TEXT";
    private static final String UPDATED_LABEL = "UPDATED_TEXT";

    private static final byte[] DEFAULT_DATA = new byte[] { 1,1,1,1,1,1,1,1,1,1,1,1,1,1 };
    private static final byte[] UPDATED_DATA = new byte[] { 2,2,2,2,2,2,2,2,2,2,2,2,2,2 };
        
    @Inject
    private LogoRepository logoRepository;

    private MockMvc restLogoMockMvc;

    private Logo logo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LogoController logoController = new LogoController();
        ReflectionTestUtils.setField(logoController, "logoRepository", logoRepository);

        this.restLogoMockMvc = MockMvcBuilders.standaloneSetup(logoController).build();

        logo = new Logo();

        logo.setOrgId(DEFAULT_ORG_ID);
        logo.setLabel(DEFAULT_LABEL);
        logo.setData(DEFAULT_DATA);
    }

    @Test
    public void testCRUDLogo() throws Exception {

        // Create Logo
        restLogoMockMvc.perform(post("/app/rest/logos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(logo)))
                .andExpect(status().isOk());

        // Read Logo
        restLogoMockMvc.perform(get("/app/rest/logos/{orgId}", DEFAULT_ORG_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orgId").value(DEFAULT_ORG_ID.toString()))
                .andExpect(jsonPath("$.label").value(DEFAULT_LABEL.toString()))
                .andExpect(jsonPath("$.data").value(DEFAULT_DATA.toString()));

        // Update Logo
        logo.setLabel(UPDATED_LABEL);
        logo.setData(UPDATED_DATA);

        restLogoMockMvc.perform(post("/app/rest/logos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(logo)))
                .andExpect(status().isOk());

        // Read updated Logo
        restLogoMockMvc.perform(get("/app/rest/logos/{orgId}", DEFAULT_ORG_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.label").value(UPDATED_LABEL.toString()))
                .andExpect(jsonPath("$.data").value(UPDATED_DATA.toString()));

        // Delete Logo
        restLogoMockMvc.perform(delete("/app/rest/logos/{orgId}", DEFAULT_ORG_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Read nonexisting Logo
        restLogoMockMvc.perform(get("/app/rest/logos/{orgId}", DEFAULT_ORG_ID)
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());

    }
}
