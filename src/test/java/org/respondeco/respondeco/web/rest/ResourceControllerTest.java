package org.respondeco.respondeco.web.rest;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.ResourceService;
import org.respondeco.respondeco.service.UserService;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.MatchAlreadyExistsException;
import org.respondeco.respondeco.service.exception.ResourceException;
import org.respondeco.respondeco.service.exception.enumException.EnumResourceException;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.respondeco.respondeco.service.ResourceTagService;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.ResourceMatchRequestDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.util.RestUtil;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementRequestDTO;
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

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Roman Kern on 25.11.14.
 */

@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class ResourceControllerTest {

    //region DEFAULT Initialiazation values
    private static final Long RESOURCE_OFFER_ID = 1L;
    private static final String RESOURCE_OFFER_NAME = "TEST OFFER NAME";
    private static final String RESOURCE_OFFER_DESCRIPTION = "HEY HERE MY SUPER OFFER DESCRIPTION. BLA BLA";
    private static final Long RESOUCE_OFFER_ORGANIZATION_ID = 1L;
    private static final BigDecimal RESOURCE_OFFER_AMOUNT = new BigDecimal(10);
    private static final Boolean RESOURCE_OFFER_IS_COMMERCIAL = true;

    private static final Long RESOURCE_REQUIREMENT_ID = 1L;
    private static final String RESOURCE_REQUIREMENT_NAME = "TEST REQUIREMENT NAME";
    private static final String RESOURCE_REQUIREMENT_DESCRIPTION = "HEY HERE MY SUPER REQUIREMENT DESCRIPTION. BLA BLA";
    private static final Long RESOUCE_REQUIREMENT_PROJECT_ID = 1L;
    private static final BigDecimal RESOURCE_REQUIREMENT_AMOUNT = new BigDecimal(1032);
    private static final Boolean RESOURCE_REQUIREMENT_IS_ESSENTIAL = true;

    private static final String RESOURCE_TAG_NAME1 = "MY SUPER TAG 1";
    private static final String RESOURCE_TAG_NAME2 = "HEY TAG 2";
    //endregion

    @Mock
    private ResourceService resourceService;
    @Mock
    private ResourceOfferRepository resourceOfferRepository;
    @Mock
    private ResourceRequirementRepository resourceRequirementRepository;
    @Mock
    private ResourceTagRepository resourceTagRepository;
    @Mock
    private ResourceTagService resourceTagService;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserService userService;
    @Mock
    private ResourceMatchRepository resourceMatchRepository;

    private MockMvc restMockMvc;


    private static Class<Long> longCl = Long.class;
    private static Class<String> stringCl = String.class;
    private static Class<BigDecimal> bigDecCl = BigDecimal.class;
    private static Class<Boolean> boolCl = Boolean.class;
    private static Class<LocalDate> locDateCl = LocalDate.class;
    private static Class<String[]> strArrCl = String[].class;


    private static Class<ResourceRequirement> reqCl = ResourceRequirement.class;
    private static Class<ResourceOffer> offerCl = ResourceOffer.class;
    private static Class<ResourceTag> tagCl = ResourceTag.class;

    private ResourceOffer resourceOffer;
    private List<ResourceOfferDTO> resourceOffers;
    private ResourceRequirement resourceRequirement;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        resourceService = spy(new ResourceService(
            resourceOfferRepository,
            resourceRequirementRepository,
            resourceTagService,
            organizationRepository,
            projectRepository,
            imageRepository,
            userService,
            resourceMatchRepository));
        ResourceController controller = new ResourceController(resourceService);

        restMockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        List<ResourceTag> resourceTags = new ArrayList<>();
        resourceTags.add(new ResourceTag(1L, RESOURCE_TAG_NAME1));
        resourceTags.add(new ResourceTag(2L, RESOURCE_TAG_NAME2));

        resourceOffer = new ResourceOffer();
        resourceOffer.setId(RESOURCE_OFFER_ID);
        resourceOffer.setName(RESOURCE_OFFER_NAME);
        resourceOffer.setDescription(RESOURCE_OFFER_DESCRIPTION);
        Organization org = new Organization();
        org.setId(RESOUCE_OFFER_ORGANIZATION_ID);
        resourceOffer.setOrganization(org);
        resourceOffer.setAmount(RESOURCE_OFFER_AMOUNT);
        resourceOffer.setIsCommercial(RESOURCE_OFFER_IS_COMMERCIAL);
        resourceOffer.setResourceTags(resourceTags);
        resourceOffers = new ArrayList<>(1);
        resourceOffers.add(new ResourceOfferDTO(resourceOffer));

        resourceRequirement = new ResourceRequirement();
        resourceRequirement.setId(RESOURCE_REQUIREMENT_ID);
        resourceRequirement.setName(RESOURCE_REQUIREMENT_NAME);
        resourceRequirement.setDescription(RESOURCE_REQUIREMENT_DESCRIPTION);
        resourceRequirement.setAmount(RESOURCE_REQUIREMENT_AMOUNT);
        resourceRequirement.setIsEssential(RESOURCE_REQUIREMENT_IS_ESSENTIAL);
        resourceRequirement.setResourceTags(resourceTags);
        Project project = new Project();
        project.setId(RESOUCE_REQUIREMENT_PROJECT_ID);
        resourceRequirement.setProject(project);
    }

    private ResourceOfferDTO bindOfferDTOMockData(Integer operation) throws Exception{
        reset(resourceService);
        ResourceOfferDTO dto = new ResourceOfferDTO(resourceOffer);
        if(operation == 0) {
            doReturn(resourceOffer).when(resourceService).createOffer(dto.getName(), dto.getAmount(),
                dto.getDescription(), dto.getOrganizationId(), dto.getIsCommercial(),
                dto.getIsRecurrent(), dto.getStartDate(), dto.getEndDate(), dto.getResourceTags(), dto.getLogoId());
        }else if (operation == 1){
            doReturn(resourceOffer).when(resourceService).updateOffer(dto.getId(), dto.getOrganizationId(),
                dto.getName(), dto.getAmount(), dto.getDescription(), dto.getIsCommercial(),
                dto.getIsRecurrent(), dto.getStartDate(), dto.getEndDate(), dto.getResourceTags(), dto.getLogoId());
        }else if (operation == 2){
            doNothing().when(resourceService).deleteOffer(dto.getId());
        }else if(operation == 3){
            doReturn(resourceOffers).when(resourceService).getAllOffers(dto.getOrganizationId());
            doReturn(resourceOffer).when(resourceService).getOfferById(dto.getId());
        }
        return dto;
    }
    private ResourceRequirementRequestDTO bindRequirementDTOMockData(Integer operation) throws Exception{
        reset(resourceService);
        ResourceRequirementRequestDTO dto = new ResourceRequirementRequestDTO(resourceRequirement);
        if(operation == 0) {
            doReturn(resourceRequirement).when(resourceService).createRequirement(dto.getName(), dto.getAmount(),
                dto.getDescription(), dto.getProjectId(), dto.getIsEssential(), dto.getResourceTags());
        } else if (operation == 1) {
            doReturn(resourceRequirement).when(resourceService).updateRequirement(dto.getId(),
                dto.getName(), dto.getAmount(), dto.getDescription(), dto.getProjectId(), dto.getIsEssential(), dto.getResourceTags());
        }else if (operation == 2){
            doNothing().when(resourceService).deleteRequirement(dto.getId());
        }else if(operation == 3){
            doReturn(Arrays.asList(resourceRequirement)).when(resourceService).getAllRequirements();
        }
        return dto;
    }
    private void verifyOffer(Integer operation, ResourceOfferDTO dto) throws Exception{
        if(operation == 0){
            verify(resourceService, times(1)).createOffer(dto.getName(), dto.getAmount(),
                dto.getDescription(), dto.getOrganizationId(), dto.getIsCommercial(),
                dto.getIsRecurrent(), dto.getStartDate(), dto.getEndDate(), dto.getResourceTags(), dto.getLogoId());
        }else if (operation == 1){
            verify(resourceService, times(1)).updateOffer(dto.getId(), dto.getOrganizationId(),
                dto.getName(), dto.getAmount(), dto.getDescription(), dto.getIsCommercial(),
                dto.getIsRecurrent(), dto.getStartDate(), dto.getEndDate(), dto.getResourceTags(), dto.getLogoId());
        }else if(operation == 2){
            verify(resourceService, times(1)).deleteOffer(dto.getId());
        }else if (operation == 3){
            verify(resourceService, times(1)).getAllOffers(any(), any(), any());
            verify(resourceService, times(1)).getOfferById(dto.getId());
        }
    }
    private void verifyRequirement(Integer operation, ResourceRequirementRequestDTO dto) throws Exception{
        if(operation == 0){
            verify(resourceService, times(1)).createRequirement(dto.getName(), dto.getAmount(),
                dto.getDescription(), dto.getProjectId(), dto.getIsEssential(), dto.getResourceTags());
        }else if (operation == 1){
            verify(resourceService, times(1)).updateRequirement(dto.getId(),
                dto.getName(), dto.getAmount(), dto.getDescription(), dto.getProjectId(), dto.getIsEssential(), dto.getResourceTags());
        }else if(operation == 2){
            verify(resourceService, times(1)).deleteRequirement(dto.getId());
        }else if (operation == 3){
            verify(resourceService, times(1)).getAllRequirements();
        }
    }

    @Test
    public void testCRUDOffer() throws Exception{
        //region CREATE
        ResourceOfferDTO dto = this.bindOfferDTOMockData(0);
        dto.setId(null);

        // Create Project
        restMockMvc.perform(post("/app/rest/resourceOffers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(resourceOffer.getId().intValue()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        this.verifyOffer(0, dto);

        // Offer already exists
        // clean mock
        dto = this.bindOfferDTOMockData(0);
        dto.setId(null);
        doThrow(new ResourceException("", EnumResourceException.ALREADY_EXISTS)).when(resourceService).createOffer(
            dto.getName(), dto.getAmount(), dto.getDescription(), dto.getOrganizationId(), dto.getIsCommercial(),
            dto.getIsRecurrent(), dto.getStartDate(), dto.getEndDate(), dto.getResourceTags(), dto.getLogoId());
        restMockMvc.perform(post("/app/rest/resourceOffers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyOffer(0, dto);

        // unexpected exception
        // clean mock
        dto = this.bindOfferDTOMockData(0);
        dto.setId(null);
        doThrow(new ResourceException("", EnumResourceException.ALREADY_EXISTS)).when(resourceService).createOffer(dto.getName(), dto.getAmount(),
            dto.getDescription(), dto.getOrganizationId(), dto.getIsCommercial(),
            dto.getIsRecurrent(), dto.getStartDate(), dto.getEndDate(), dto.getResourceTags(), dto.getLogoId());
        restMockMvc.perform(post("/app/rest/resourceOffers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyOffer(0, dto);

        //endregion

        //region UPDATE
        // clean mock
        resourceOffer.setName("yuppi");
        dto = this.bindOfferDTOMockData(1);
        restMockMvc.perform(put("/app/rest/resourceOffers/{id}", resourceOffer.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());
        this.verifyOffer(1, dto);


        //Throw Resource Excpetion
        resourceOffer.setName(RESOURCE_OFFER_NAME);
        dto = this.bindOfferDTOMockData(1);
        doThrow(new ResourceException("", EnumResourceException.ALREADY_EXISTS)).when(resourceService).updateOffer(
            dto.getId(), dto.getOrganizationId(), dto.getName(), dto.getAmount(),dto.getDescription(),
            dto.getIsCommercial(), dto.getIsRecurrent(), dto.getStartDate(), dto.getEndDate(), dto.getResourceTags(), dto.getLogoId());

        restMockMvc.perform(put("/app/rest/resourceOffers/{id}", resourceOffer.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyOffer(1, dto);

        //Throw Exception
        dto = this.bindOfferDTOMockData(1);
        doThrow(new ResourceException("", EnumResourceException.NOT_FOUND)).when(resourceService).updateOffer(dto.getId(), dto.getOrganizationId(),
            dto.getName(), dto.getAmount(),dto.getDescription(), dto.getIsCommercial(),
            dto.getIsRecurrent(), dto.getStartDate(), dto.getEndDate(), dto.getResourceTags(), dto.getLogoId());

        restMockMvc.perform(put("/app/rest/resourceOffers/{id}", resourceOffer.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyOffer(1, dto);


        //endregion

        //region DELETE
        dto = this.bindOfferDTOMockData(2);
        restMockMvc.perform(delete("/app/rest/resourceOffers/{id}", resourceOffer.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());
        this.verifyOffer(2, dto);

        //throw resource exception
        dto = this.bindOfferDTOMockData(2);
        doThrow(new ResourceException("", EnumResourceException.ALREADY_EXISTS)).when(resourceService).
            deleteOffer(dto.getId());
        restMockMvc.perform(delete("/app/rest/resourceOffers/{id}", resourceOffer.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyOffer(2, dto);

        //throw resource exception
        dto = this.bindOfferDTOMockData(2);
        doThrow(new ResourceException("", EnumResourceException.NOT_FOUND)).when(resourceService).
            deleteOffer(dto.getId());
        restMockMvc.perform(delete("/app/rest/resourceOffers/{id}", resourceOffer.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyOffer(2, dto);

        //endregion

        //region READ DATA
        dto = this.bindOfferDTOMockData(3);
        restMockMvc.perform(get("/app/rest/resourceOffers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());

        restMockMvc.perform(get("/app/rest/resourceOffers/{id}", dto.getOrganizationId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(jsonPath("$.id").value(resourceOffer.getId().intValue()))
            .andExpect(status().isOk());
        this.verifyOffer(3, dto);
        //endregion
    }

    @Test
    public void testCRUDRequirements() throws Exception{
        //region CREATE
        ResourceRequirementRequestDTO dto = this.bindRequirementDTOMockData(0);
        dto.setId(null);

        // Create Project
        restMockMvc.perform(post("/app/rest/resourceRequirements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(resourceRequirement.getId().intValue()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        this.verifyRequirement(0, dto);

        // Offer already exists
        // clean mock
        dto = this.bindRequirementDTOMockData(0);
        dto.setId(null);
        doThrow(new ResourceException("", EnumResourceException.ALREADY_EXISTS)).when(resourceService).createRequirement(
            dto.getName(), dto.getAmount(), dto.getDescription(), dto.getProjectId(), dto.getIsEssential(),
            dto.getResourceTags());
        restMockMvc.perform(post("/app/rest/resourceRequirements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyRequirement(0, dto);

        // unexpected exception
        // clean mock
        dto = this.bindRequirementDTOMockData(0);
        dto.setId(null);
        doThrow(new Exception("")).when(resourceService).createRequirement(
            dto.getName(), dto.getAmount(), dto.getDescription(), dto.getProjectId(), dto.getIsEssential(),
            dto.getResourceTags());
        restMockMvc.perform(post("/app/rest/resourceRequirements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyRequirement(0, dto);

        //endregion

        //region UPDATE
        // clean mock
        resourceRequirement.setName("yuppi");
        dto = this.bindRequirementDTOMockData(1);
        restMockMvc.perform(put("/app/rest/resourceRequirements/{resourceRequirementId}", resourceRequirement.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());
        this.verifyRequirement(1, dto);


        //Throw Resource Excpetion
        resourceRequirement.setName(RESOURCE_OFFER_NAME);
        dto = this.bindRequirementDTOMockData(1);
        doThrow(new ResourceException("", EnumResourceException.ALREADY_EXISTS)).when(resourceService).updateRequirement(
            dto.getId(), dto.getName(), dto.getAmount(), dto.getDescription(), dto.getProjectId(), dto.getIsEssential(),
            dto.getResourceTags());

        restMockMvc.perform(put("/app/rest/resourceRequirements/{resourceRequirementId}", resourceRequirement.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyRequirement(1, dto);

        //Throw Exception
        dto = this.bindRequirementDTOMockData(1);
        doThrow(new Exception()).when(resourceService).updateRequirement(
            dto.getId(), dto.getName(), dto.getAmount(), dto.getDescription(), dto.getProjectId(), dto.getIsEssential(),
            dto.getResourceTags());

        restMockMvc.perform(put("/app/rest/resourceRequirements/{resourceRequirementId}", resourceRequirement.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyRequirement(1, dto);


        //endregion

        //region DELETE

        dto = this.bindRequirementDTOMockData(2);
        restMockMvc.perform(delete("/app/rest/resourceRequirements/{resourceRequirementId}", resourceRequirement.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());
        this.verifyRequirement(2, dto);

        //throw resource exception
        dto = this.bindRequirementDTOMockData(2);
        doThrow(new ResourceException("", EnumResourceException.ALREADY_EXISTS)).when(resourceService).
            deleteRequirement(dto.getId());
        restMockMvc.perform(delete("/app/rest/resourceRequirements/{resourceRequirementId}", resourceRequirement.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyRequirement(2, dto);

        //throw resource exception
        dto = this.bindRequirementDTOMockData(2);
        doThrow(new Exception()).when(resourceService).
            deleteRequirement(dto.getId());
        restMockMvc.perform(delete("/app/rest/resourceRequirements/{resourceRequirementId}", resourceRequirement.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isBadRequest());
        this.verifyRequirement(2, dto);

        //endregion

        //region READ DATA
        dto = this.bindRequirementDTOMockData(3);
        restMockMvc.perform(get("/app/rest/resourceRequirements")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dto)))
            .andExpect(status().isOk());
        this.verifyRequirement(3, dto);
        //endregion
    }


    /**
     * Testing /app/rest/resourcerequests
     * @throws Exception
     */
    @Test
    public void testClaimResourceOffer_expectOK() throws Exception{
        ResourceMatchRequestDTO requestDTO = new ResourceMatchRequestDTO();
        requestDTO.setOrganizationId(1L);
        requestDTO.setProjectId(1L);
        requestDTO.setResourceOfferId(1L);
        requestDTO.setResourceRequirementId(1L);

        ResourceMatch resourceMatch = new ResourceMatch();
        resourceMatch.setId(1L);
        resourceMatch.setMatchDirection(MatchDirection.ORGANIZATION_CLAIMED);
        resourceMatch.setResourceOffer(new ResourceOffer());
        resourceMatch.setResourceRequirement(new ResourceRequirement());
        resourceMatch.setOrganization(new Organization());
        resourceMatch.setProject(new Project());

        doReturn(resourceMatch).when(resourceService).createClaimResourceRequest(requestDTO.getResourceOfferId(), requestDTO.getResourceRequirementId());

        restMockMvc.perform(post("/app/rest/resourcerequests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestDTO)))
            .andExpect(status().isCreated());
    }

    @Test
    public void testClaimResourceOffer_expectMatchAlreadyExists() throws Exception {
        ResourceMatchRequestDTO requestDTO = new ResourceMatchRequestDTO();
        requestDTO.setOrganizationId(1L);
        requestDTO.setProjectId(1L);
        requestDTO.setResourceOfferId(1L);
        requestDTO.setResourceRequirementId(1L);

        doThrow(MatchAlreadyExistsException.class).when(resourceService).createClaimResourceRequest(requestDTO.getResourceOfferId(), requestDTO.getResourceRequirementId());

        restMockMvc.perform(post("/app/rest/resourcerequests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestDTO)))
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @Test
    public void testClaimResourceOffer_expectIllegalValueException() throws Exception {
        ResourceMatchRequestDTO requestDTO = new ResourceMatchRequestDTO();
        requestDTO.setOrganizationId(1L);
        requestDTO.setProjectId(1L);
        requestDTO.setResourceOfferId(1L);
        requestDTO.setResourceRequirementId(1L);

        doThrow(IllegalValueException.class).when(resourceService).createClaimResourceRequest(requestDTO.getResourceOfferId(), requestDTO.getResourceRequirementId());

        restMockMvc.perform(post("/app/rest/resourcerequests")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestDTO)))
            .andExpect(status().isBadRequest());
    }


    @Test
    public void testAnswerResourceRequest_expectOK() throws Exception {
        ResourceMatchRequestDTO requestDTO = new ResourceMatchRequestDTO();
        requestDTO.setOrganizationId(1L);
        requestDTO.setProjectId(1L);
        requestDTO.setResourceOfferId(1L);
        requestDTO.setResourceRequirementId(1L);
        requestDTO.setAccepted(true);

        ResourceMatch match = new ResourceMatch();
        match.setMatchDirection(MatchDirection.ORGANIZATION_CLAIMED);
        match.setId(1L);

        doReturn(match).when(resourceService).answerResourceRequest(1L,true);

        restMockMvc.perform(put("/app/rest/resourcerequests/1")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestDTO)))
            .andExpect(status().isOk());
    }

    @Test
    public void testAnswerResourceRequest_expectIllegalValue() throws Exception {
        ResourceMatchRequestDTO requestDTO = new ResourceMatchRequestDTO();
        requestDTO.setOrganizationId(1L);
        requestDTO.setProjectId(1L);
        requestDTO.setResourceOfferId(1L);
        requestDTO.setResourceRequirementId(1L);
        requestDTO.setAccepted(true);

        ResourceMatch match = new ResourceMatch();
        match.setMatchDirection(MatchDirection.ORGANIZATION_CLAIMED);
        match.setId(1L);

        doThrow(IllegalValueException.class).when(resourceService).answerResourceRequest(1L,true);

        restMockMvc.perform(put("/app/rest/resourcerequests/1")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(requestDTO)))
            .andExpect(status().isBadRequest());
    }



}
