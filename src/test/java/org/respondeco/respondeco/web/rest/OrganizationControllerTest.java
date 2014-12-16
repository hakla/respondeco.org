package org.respondeco.respondeco.web.rest;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchProjectException;
import org.respondeco.respondeco.service.exception.ProjectRatingException;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.ImageDTO;
import org.respondeco.respondeco.web.rest.dto.OrganizationRequestDTO;
import org.respondeco.respondeco.web.rest.dto.RatingRequestDTO;
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

    private Organization org1;
    private Organization org2;
    private OrganizationRequestDTO organizationRequestDTO;
    private User defaultUser;
    private Set<Authority> userAuthorities;
    private UserDTO defaultUserDTO;

    private ArgumentCaptor<Object> voidInterceptor;

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

        org1 = new Organization();
        org1.setId(100L);
        org1.setOwner(defaultUser);
        org1.setName("test1");
        org2 = new Organization();
        org2.setId(200L);
        org2.setOwner(defaultUser);
        org2.setName("test2");

        voidInterceptor = ArgumentCaptor.forType(Object.class, -1, false);

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

        verify(organizationService, times(1)).createOrganizationInformation(
            organizationRequestDTO.getName(),
            organizationRequestDTO.getDescription(),
            organizationRequestDTO.getEmail(),
            organizationRequestDTO.isNpo(),
            (ImageDTO) null);

        doReturn(Arrays.asList(org1, org2)).when(organizationService).getOrganizations();

        // Read All Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value(org1.getName()))
                .andExpect(jsonPath("$[1].name").value(org2.getName()));

        doReturn(defaultOrganization).when(organizationService).getOrganization(defaultOrganization.getId());

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


        //do nothing but return immediately without errors
        doAnswer(voidInterceptor).when(organizationService)
            .updaterOrganizationInformation(anyString(), anyString(), anyString(), anyBoolean(), eq((ImageDTO) null));

        restOrganizationMockMvc.perform(put("/app/rest/organizations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(organizationRequestDTO)))
                .andExpect(status().isOk());

        //do nothing but return immediately without errors
        doAnswer(voidInterceptor).when(organizationService).deleteOrganizationInformation();

        // Delete Organization
        restOrganizationMockMvc.perform(delete("/app/rest/organizations")
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());


        doReturn(null).when(organizationService).getOrganization(defaultOrganization.getId());

        // Read nonexisting Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}", organizationRequestDTO.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetAggregatedRating_expectOK_shouldCallServiceForAggregatedRating() throws Exception {
        AggregatedRating aggregatedRating = new AggregatedRating();
        aggregatedRating.setRating(3.5);
        aggregatedRating.setCount(5L);

        doReturn(aggregatedRating).when(ratingService).getAggregatedRatingByOrganization(anyLong());

        restOrganizationMockMvc.perform(get("/app/rest/organizations/1/ratings"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.rating").value(3.5))
            .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    public void testRateOrganization_expectOK_shouldCallServiceToSaveRating() throws Exception {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO();
        ratingRequestDTO.setRating(5);
        ratingRequestDTO.setComment("foobar");
        ratingRequestDTO.setMatchid(100L);

        doAnswer(voidInterceptor).when(ratingService).rateOrganization(anyLong(), anyLong(), anyInt(), anyString());

        restOrganizationMockMvc.perform(post("/app/rest/organizations/1/ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ratingRequestDTO)))
            .andExpect(status().isOk());

        verify(ratingService, times(1)).rateOrganization(anyLong(), anyLong(), anyInt(), anyString());
    }

    @Test
    public void testRateOrganization_expectNOT_FOUND_serviceThrowsNoSuchOrganizationException() throws Exception {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO();
        ratingRequestDTO.setMatchid(100L);
        ratingRequestDTO.setRating(5);
        ratingRequestDTO.setComment("foobar");

        doThrow(NoSuchOrganizationException.class).when(ratingService)
            .rateOrganization(anyLong(), anyLong(), anyInt(), anyString());

        restOrganizationMockMvc.perform(post("/app/rest/organizations/1/ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ratingRequestDTO)))
            .andExpect(status().isNotFound());

        verify(ratingService, times(1)).rateOrganization(anyLong(), anyLong(), anyInt(), anyString());
    }

    @Test
    public void testRateOrganization_expectBAD_REQUEST_serviceThrowsRatingException() throws Exception {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO();
        ratingRequestDTO.setRating(5);
        ratingRequestDTO.setComment("foobar");
        ratingRequestDTO.setMatchid(100L);

        doThrow(NoSuchProjectException.class).when(ratingService)
            .rateOrganization(anyLong(), anyLong(), anyInt(), anyString());

        restOrganizationMockMvc.perform(post("/app/rest/organizations/1/ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ratingRequestDTO)))
            .andExpect(status().isBadRequest());

        verify(ratingService, times(1)).rateOrganization(anyLong(), anyLong(), anyInt(), anyString());
    }

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
        doReturn(Arrays.asList(defaultUser,inviteAbleUser)).when(organizationService)
            .getUserByOrgId(defaultOrganization.getId());

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
