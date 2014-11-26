package org.respondeco.respondeco.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.ResourcesService;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;

import java.math.BigDecimal;

import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Roman Kern on 25.11.14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class ResourceControllerTest {


    private ResourcesService resourcesService;


    @Inject
    private ResourceOfferRepository resourceOfferRepository;
    @Inject
    private ResourceRequirementRepository resourceRequirementRepository;
    @Inject
    private ResourceTagRepository resourceTagRepository;
    @Inject
    private ResourceOfferJoinResourceRequirementRepository resourceOfferJoinResourceRequirementRepository;
    @Inject
    private ResourceOfferJoinResourceTagRepository resourceOfferJoinResourceTagRepository;
    @Inject
    private ResourceRequirementJoinResourceTagRepository resourceRequirementJoinResourceTagRepository;


    private MockMvc restMockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        resourcesService = spy(new ResourcesService(
            resourceOfferRepository,
            resourceRequirementRepository,
            resourceTagRepository,
            resourceRequirementJoinResourceTagRepository,
            resourceOfferJoinResourceRequirementRepository,
            resourceOfferJoinResourceTagRepository));
        ResourceController controller = new ResourceController(resourcesService);

        restMockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void testOffer() throws Exception{

        ResourceOfferDTO sentDTO = new ResourceOfferDTO();
        sentDTO.setName("test");
        sentDTO.setDescription("Hakkiod");
        sentDTO.setOrganisationId(1L);
        sentDTO.setAmount(new BigDecimal(10));
        sentDTO.setIsCommercial(true);
        String[] tags = new String[1];
        tags[0] = "My Super Tag";
        sentDTO.setResourceTags(tags);
        // Create Project
        restMockMvc.perform(post("/app/rest/organisations/{organisationId}/resourceOffer", 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        //    .andExpect(jsonPath("$.id").value(sentOfferDTO.getId().intValue()))
        ;
        restMockMvc.perform(post("/app/rest/organisations/{organisationId}/resourceOffer", 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isBadRequest());

        sentDTO.setName(null);
        restMockMvc.perform(post("/app/rest/organisations/{organisationId}/resourceOffer", 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isBadRequest());

        sentDTO.setName("yuppi");
        restMockMvc.perform(put("/app/rest/organisations/{organisationId}/resourceOffers/{resourceOfferId}", 1L, 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isBadRequest());

        sentDTO.setId(1L);
        restMockMvc.perform(put("/app/rest/organisations/{organisationId}/resourceOffers/{resourceOfferId}", 1L, 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isOk());

        restMockMvc.perform(get("/app/rest/resourceOffers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isOk());

        restMockMvc.perform(get("/app/rest/organisations/{organisationId}/resourceOffers", 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isOk());

        restMockMvc.perform(delete("/app/rest/organisations/{organisationId}/resourceOffers/{resourceOfferId}", 1L, 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isOk());

        restMockMvc.perform(delete("/app/rest/organisations/{organisationId}/resourceOffers/{resourceOfferId}", -1L, -1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isBadRequest());

        restMockMvc.perform(delete("/app/rest/organisations/{organisationId}/resourceOffers/{resourceOfferId}", null, null)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testRequirement() throws Exception{

        ResourceRequirementDTO sentDTO = new ResourceRequirementDTO();
        sentDTO.setName("test");
        sentDTO.setDescription("Hakkiod");
        sentDTO.setProjectId(1L);
        sentDTO.setAmount(new BigDecimal(10));
        sentDTO.setIsEssential(true);
        String[] tags = new String[1];
        tags[0] = "My Super Tag";
        sentDTO.setResourceTags(tags);
        // Create Project
        restMockMvc.perform(post("/app/rest/projects{projectId}/resourceRequirements", 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        //    .andExpect(jsonPath("$.id").value(sentDTO.getId().intValue()))
        ;

        restMockMvc.perform(post("/app/rest/projects{projectId}/resourceRequirements", 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isBadRequest());

        sentDTO.setName(null);
        restMockMvc.perform(post("/app/rest/projects{projectId}/resourceRequirements", 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isBadRequest());

        sentDTO.setName("yeapiyayeah");
        restMockMvc.perform(put("/app/rest/projects/{projectId}/resourceRequirements/{resourceRequirementId}", 1L, 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isBadRequest());

        sentDTO.setId(1L);
        restMockMvc.perform(put("/app/rest/projects/{projectId}/resourceRequirements/{resourceRequirementId}", 1L, 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isOk());

        restMockMvc.perform(get("/app/rest/resourceRequirements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isOk());

        restMockMvc.perform(get("/app/rest/projects/{projectId}/resourceRequirements", 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isOk());

        restMockMvc.perform(delete("/app/rest/projects/{projectId}/resourceRequirements/{resourceRequirementId}", 1L, 1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isOk());

        restMockMvc.perform(delete("/app/rest/projects/{projectId}/resourceRequirements/{resourceRequirementId}", -1L, -1L)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isBadRequest());

        restMockMvc.perform(delete("/app/rest/projects/{projectId}/resourceRequirements/{resourceRequirementId}", null, null)
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sentDTO)))
            .andExpect(status().isMethodNotAllowed());
    }
}
