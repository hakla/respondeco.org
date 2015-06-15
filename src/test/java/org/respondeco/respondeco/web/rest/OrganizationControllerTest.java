package org.respondeco.respondeco.web.rest;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.ImageRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.*;
import org.respondeco.respondeco.service.exception.AlreadyInOrganizationException;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.NoSuchEntityException;
import org.respondeco.respondeco.service.exception.PostingFeedException;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.ImageDTO;
import org.respondeco.respondeco.web.rest.dto.OrganizationRequestDTO;
import org.respondeco.respondeco.web.rest.dto.RatingRequestDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the OrganizationController REST controller.
 *
 * @see OrganizationController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
public class OrganizationControllerTest {

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private ImageRepository imageRepositoryMock;

    @Mock
    private ResourceService resourceServiceMock;

    @Mock
    private OrganizationService organizationServiceMock;

    @Mock
    private OrgJoinRequestService orgJoinRequestServiceMock;

    @Mock
    private RatingService ratingServiceMock;

    @Mock
    private PostingFeedService postingFeedServiceMock;


    private MockMvc restOrganizationMockMvc;

    private Organization org1;
    private Organization org2;
    private OrganizationRequestDTO organizationRequestDTO;
    private User defaultUser;
    private Set<Authority> userAuthorities;
    private UserDTO defaultUserDTO;
    private PostingFeed postingFeed;

    private ResourceOffer resOffer1;
    private ResourceOffer resOffer2;
    private ResourceMatch match1;
    private ResourceMatch match2;
    private OrgJoinRequest joinRequest1;
    private OrgJoinRequest joinRequest2;
    private Posting posting;

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
                organizationServiceMock,
                userServiceMock,
                resourceServiceMock,
                orgJoinRequestServiceMock,
                ratingServiceMock,
                postingFeedServiceMock);

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

        postingFeed = new PostingFeed();
        postingFeed.setId(1L);

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

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);

        org1 = new Organization();
        org1.setId(100L);
        org1.setOwner(defaultUser);
        org1.setName("test1");
        org2 = new Organization();
        org2.setId(200L);
        org2.setOwner(defaultUser);
        org2.setName("test2");


        resOffer1 = new ResourceOffer();
        resOffer1.setId(100L);
        resOffer1.setOrganization(org1);
        resOffer1.setName("bluboffer");
        resOffer1.setAmount(new BigDecimal(5));
        resOffer2 = new ResourceOffer();
        resOffer2.setId(100L);
        resOffer2.setOrganization(org1);
        resOffer2.setName("bluboffer");
        resOffer2.setAmount(new BigDecimal(5));

        Project project = new Project();
        project.setManager(defaultUser);
        project.setOrganization(org1);
        match1 = new ResourceMatch();
        match1.setId(100L);
        match1.setResourceRequirement(new ResourceRequirement());
        match1.setResourceOffer(resOffer1);
        match1.setOrganization(org1);
        match1.setProject(project);
        match1.setMatchDirection(MatchDirection.ORGANIZATION_CLAIMED);
        match2 = new ResourceMatch();
        match2.setId(200L);
        match2.setResourceRequirement(new ResourceRequirement());
        match2.setResourceOffer(resOffer2);
        match2.setOrganization(org1);
        match2.setProject(project);
        match2.setMatchDirection(MatchDirection.ORGANIZATION_OFFERED);

        joinRequest1 = new OrgJoinRequest();
        joinRequest1.setId(100L);
        joinRequest1.setUser(defaultUser);
        joinRequest1.setOrganization(org1);
        joinRequest2 = new OrgJoinRequest();
        joinRequest2.setId(200L);
        joinRequest2.setUser(defaultUser);
        joinRequest2.setOrganization(org1);

        posting = new Posting();
        posting.setId(1L);
        posting.setAuthor(defaultUser);
        posting.setInformation("information");
        posting.setPostingfeed(postingFeed);

        voidInterceptor = ArgumentCaptor.forType(Object.class, -1, false);

    }

    @Test
    public void testCreateOrganization_expectOK_shouldCreateOrganization() throws Exception {
        doReturn(defaultOrganization).when(organizationServiceMock).createOrganizationInformation(
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

        verify(organizationServiceMock, times(1)).createOrganizationInformation(
            organizationRequestDTO.getName(),
            organizationRequestDTO.getDescription(),
            organizationRequestDTO.getEmail(),
            organizationRequestDTO.isNpo(),
            (ImageDTO) null);
    }

    @Test
    public void testCreateOrganization_expectBAD_REQUEST_serviceThrowsException() throws Exception {
        doThrow(AlreadyInOrganizationException.class).when(organizationServiceMock)
            .createOrganizationInformation(anyString(), anyString(), anyString(), anyBoolean(), eq((ImageDTO) null));

        // Create Organization
        restOrganizationMockMvc.perform(post("/app/rest/organizations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(organizationRequestDTO)))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllOrganizations_expectOK_shouldReturnAllOrganizations() throws Exception {
        Page<Organization> orgPage = new PageImpl<Organization>(Arrays.asList(org1, org2));
        doReturn(orgPage).when(organizationServiceMock).get(any(PageRequest.class));

        // Read All Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.totalItems").value(2))
            .andExpect(jsonPath("$.organizations[0].name").value(org1.getName()))
            .andExpect(jsonPath("$.organizations[1].name").value(org2.getName()));
    }

    @Test
    public void testReadSingleOrganization_expectOK_shouldReturnSingleOrganization() throws Exception {
        doReturn(defaultOrganization).when(organizationServiceMock).getById(defaultOrganization.getId());

        // Read Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}", organizationRequestDTO.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.name").value(defaultOrganization.getName()))
            .andExpect(jsonPath("$.description").value(defaultOrganization.getDescription()))
            .andExpect(jsonPath("$.email").value(defaultOrganization.getEmail()))
            .andExpect(jsonPath("$.isNpo").value(defaultOrganization.getIsNpo()));
    }

    @Test
    public void testReadSingleOrganization_expectNOT_FOUND_cannotFindNonexistingOrganization() throws Exception {
        doReturn(null).when(organizationServiceMock).getById(defaultOrganization.getId());

        // Read nonexisting Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}", organizationRequestDTO.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
    }

//    @Test
//    public void testUpdateOrganization_expectOK_shouldUpdateOrganization() throws Exception {
//        // Update Organization
//        organizationRequestDTO.setId(defaultOrganization.getId());
//        organizationRequestDTO.setName(UPDATED_ORGNAME);
//        organizationRequestDTO.setDescription(UPDATED_DESCRIPTION);
//        organizationRequestDTO.setEmail(UPDATED_EMAIL);
//        organizationRequestDTO.setNpo(UPDATED_NPO);
//
//
//        //do nothing but return immediately without errors
//        doAnswer(voidInterceptor).when(organizationServiceMock)
//            .update(anyString(), anyString(), anyString(), anyBoolean(), eq((ImageDTO) null));
//
//        restOrganizationMockMvc.perform(put("/app/rest/organizations")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(organizationRequestDTO)))
//            .andExpect(status().isOk());
//    }
//
//    @Test
//    public void testUpdateOrganization_expectNOT_FOUND_cannotUpdateNonexistingOrganization() throws Exception {
//        doThrow(NoSuchEntityException.class).when(organizationServiceMock)
//            .update(anyString(), anyString(), anyString(), anyBoolean(), eq((ImageDTO) null));
//
//        restOrganizationMockMvc.perform(put("/app/rest/organizations")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(organizationRequestDTO)))
//            .andExpect(status().isNotFound());
//    }

//    @Test
//    public void testUpdateOrganization_expectBAD_REQUEST_serviceThrowsIllegalArgumentException() throws Exception {
//        doThrow(IllegalArgumentException.class).when(organizationServiceMock)
//            .update(anyString(), anyString(), anyString(), anyBoolean(), eq((ImageDTO) null));
//
//        restOrganizationMockMvc.perform(put("/app/rest/organizations")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(organizationRequestDTO)))
//            .andExpect(status().isBadRequest());
//    }

    @Test
    public void testDeleteOrganization_expectOK_shouldDeleteOrganization() throws Exception {
        //do nothing but return immediately without errors
        doAnswer(voidInterceptor).when(organizationServiceMock).delete(1L);

        // Delete Organization
        restOrganizationMockMvc.perform(delete("/app/rest/organizations/1")
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());
    }

    @Test
    public void testDeleteOrganization_expectNOT_FOUND_cannotDeleteNonexistingOrganization() throws Exception {
        doThrow(NoSuchEntityException.class).when(organizationServiceMock).delete(1L);

        // Delete Organization
        restOrganizationMockMvc.perform(delete("/app/rest/organizations/1")
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetResourceRequests_expectOK_shouldReturnAllResourceRequests() throws Exception {
        doReturn(Arrays.asList(match1, match2)).when(resourceServiceMock)
            .getResourcesForOrganization(anyLong(), isA(Pageable.class));

        // Delete Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/2/resourcerequests"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].matchId").value(match1.getId().intValue()))
            .andExpect(jsonPath("$[1].matchId").value(match2.getId().intValue()));
    }

    @Test
    public void testGetResourceOffers_expectOK_shouldReturnAllResourceOffers() throws Exception {
        doReturn(Arrays.asList(resOffer1, resOffer2)).when(resourceServiceMock).getOffersForOrganization(anyLong());

        // Delete Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/2/resourceoffers"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(resOffer1.getId().intValue()))
            .andExpect(jsonPath("$[1].id").value(resOffer2.getId().intValue()));
    }

    @Test
    public void testGetOrgJoinRequests_expectOK_shouldReturnAllRequests() throws Exception {
        doReturn(Arrays.asList(joinRequest1, joinRequest2)).when(orgJoinRequestServiceMock)
            .getOrgJoinRequestByOrganization(anyLong());

        // Delete Organization
        restOrganizationMockMvc.perform(get("/app/rest/organizations/2/orgjoinrequests"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id").value(joinRequest1.getId().intValue()))
            .andExpect(jsonPath("$[1].id").value(joinRequest2.getId().intValue()));
    }

    @Test
    public void testGetAggregatedRating_expectOK_shouldCallServiceForAggregatedRating() throws Exception {
        AggregatedRating aggregatedRating = new AggregatedRating();
        aggregatedRating.setRating(3.5);
        aggregatedRating.setCount(5L);

        doReturn(aggregatedRating).when(ratingServiceMock).getAggregatedRatingByOrganization(anyLong());

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

        doAnswer(voidInterceptor).when(ratingServiceMock).rateOrganization(anyLong(), anyLong(), anyInt(), anyString());

        restOrganizationMockMvc.perform(post("/app/rest/organizations/1/ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ratingRequestDTO)))
            .andExpect(status().isOk());

        verify(ratingServiceMock, times(1)).rateOrganization(anyLong(), anyLong(), anyInt(), anyString());
    }

    @Test
    public void testRateOrganization_expectNOT_FOUND_serviceThrowsNoSuchOrganizationException() throws Exception {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO();
        ratingRequestDTO.setMatchid(100L);
        ratingRequestDTO.setRating(5);
        ratingRequestDTO.setComment("foobar");

        doThrow(NoSuchEntityException.class).when(ratingServiceMock)
            .rateOrganization(anyLong(), anyLong(), anyInt(), anyString());

        restOrganizationMockMvc.perform(post("/app/rest/organizations/1/ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ratingRequestDTO)))
            .andExpect(status().isNotFound());

        verify(ratingServiceMock, times(1)).rateOrganization(anyLong(), anyLong(), anyInt(), anyString());
    }

    @Test
    public void testRateOrganization_expectBAD_REQUEST_serviceThrowsRatingException() throws Exception {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO();
        ratingRequestDTO.setRating(5);
        ratingRequestDTO.setComment("foobar");
        ratingRequestDTO.setMatchid(100L);

        doThrow(NoSuchEntityException.class).when(ratingServiceMock)
            .rateOrganization(anyLong(), anyLong(), anyInt(), anyString());

        restOrganizationMockMvc.perform(post("/app/rest/organizations/1/ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ratingRequestDTO)))
            .andExpect(status().isNotFound());

        verify(ratingServiceMock, times(1)).rateOrganization(anyLong(), anyLong(), anyInt(), anyString());
    }

    public void testGetOrgJoinRequestsByOrgId() throws Exception {
        doReturn(Arrays.asList(orgJoinRequest))
            .when(orgJoinRequestServiceMock).getOrgJoinRequestByOrganization(defaultOrganization.getId());

        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}/orgjoinrequests", defaultOrganization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetMembers() throws Exception {
        defaultUser.setOrganization(defaultOrganization);
        inviteAbleUser.setOrganization(defaultOrganization);
        doReturn(defaultOrganization).when(organizationRepositoryMock).findOne(defaultOrganization.getId());
        doReturn(Arrays.asList(defaultUser, inviteAbleUser)).when(organizationServiceMock)
            .getUserByOrgId(defaultOrganization.getId());

        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}/members", defaultOrganization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testDeleteMember() throws Exception {
        defaultUser.setOrganization(defaultOrganization);
        inviteAbleUser.setOrganization(defaultOrganization);
        doReturn(inviteAbleUser).when(userRepositoryMock).findOne(inviteAbleUser.getId());
        doReturn(defaultOrganization).when(organizationRepositoryMock).findOne(defaultOrganization.getId());


        restOrganizationMockMvc.perform(delete("/app/rest/organizations/{id}/members/{userId}", defaultOrganization.getId(), inviteAbleUser.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());
    }

    @Test
    public void testPostingForOrganization() throws Exception {
        Posting posting = new Posting();
        posting.setId(100L);
        posting.setAuthor(defaultUser);
        posting.setPostingfeed(postingFeed);
        posting.setInformation("posting1");
        posting.setCreatedDate(DateTime.now());

        doReturn(posting).when(postingFeedServiceMock).createPostingForOrganization(any(Long.class), any(String.class));

        restOrganizationMockMvc.perform(post("/app/rest/organizations/{id}/postings", defaultOrganization.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes("posting1")))
            .andExpect(status().isOk());
    }

    @Test
    public void testPostingForOrganization_expectBAD_REQUEST_serviceThrowsPostingFeedException() throws Exception {
        doThrow(PostingFeedException.class).when(postingFeedServiceMock).createPostingForOrganization(anyLong(), anyString());

        restOrganizationMockMvc.perform(post("/app/rest/organizations/{id}/postings", defaultOrganization.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes("posting1")))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostingForOrganization_expectNOT_FOUND_serviceThrowsNoSuchOrganizationException() throws Exception {
        doThrow(NoSuchEntityException.class).when(postingFeedServiceMock).createPostingForOrganization(anyLong(), anyString());

        restOrganizationMockMvc.perform(post("/app/rest/organizations/{id}/postings", defaultOrganization.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes("posting1")))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPostingForOrganization() throws Exception {
        List<Posting> postingList = new ArrayList<>();
        postingList.add(posting);

        PageImpl<Posting> page = new PageImpl<Posting>(postingList);

        defaultOrganization.setPostingFeed(postingFeed);
        doReturn(page).when(postingFeedServiceMock).getPostingsForOrganization(anyLong(), any(Pageable.class));

        restOrganizationMockMvc.perform(get("/app/rest/organizations/1/postings"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetPostingForOrganization_NoSuchOrganization() throws Exception {
        doThrow(NoSuchEntityException.class).when(postingFeedServiceMock)
            .getPostingsForOrganization(anyLong(), isA(Pageable.class));
        restOrganizationMockMvc.perform(get("/app/rest/organizations/1/postings")
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testFollowingState_TRUE() throws Exception {
        Boolean result = true;
        doReturn(result)
            .when(organizationServiceMock).followingState(anyLong());

        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}/followingstate", anyLong()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.state").value(result))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(organizationServiceMock, times(1)).followingState(anyLong());
    }

    @Test
    public void testFollowingState_FALSE() throws Exception {
        Boolean result = false;
        doReturn(result)
            .when(organizationServiceMock).followingState(anyLong());

        restOrganizationMockMvc.perform(get("/app/rest/organizations/{id}/followingstate", anyLong()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.state").value(result))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(organizationServiceMock, times(1)).followingState(anyLong());
    }

    @Test
    public void testFollow_SUCCESS() throws Exception {

        restOrganizationMockMvc.perform(post("/app/rest/organizations/{id}/follow", anyLong()))
            .andExpect(status().isCreated());
        verify(organizationServiceMock, times(1)).follow(anyLong());
    }


    @Test
    public void testFollow_FAIL() throws Exception {

        doThrow(IllegalValueException.class).when(organizationServiceMock).follow(anyLong());

        restOrganizationMockMvc.perform(post("/app/rest/organizations/{id}/follow", anyLong()))
            .andExpect(status().isBadRequest());
        verify(organizationServiceMock, times(1)).follow(anyLong());
    }

    @Test
    public void testUnfollow_SUCCESS() throws Exception {
        restOrganizationMockMvc.perform(delete("/app/rest/organizations/{id}/unfollow", anyLong()))
            .andExpect(status().isOk());
        verify(organizationServiceMock, times(1)).unfollow(anyLong());
    }

    @Test
    public void testUnfollow_FAIL() throws Exception {
        doThrow(IllegalValueException.class).when(organizationServiceMock).unfollow(anyLong());
        restOrganizationMockMvc.perform(delete("/app/rest/organizations/{id}/unfollow", anyLong()))
            .andExpect(status().isBadRequest());
        verify(organizationServiceMock, times(1)).unfollow(anyLong());
    }

    @Test
    public void testGetDonatedResources_expectOK() throws Exception {
        List<ResourceMatch> resourceMatches = new ArrayList<>();
        resourceMatches.add(match1);
        resourceMatches.add(match2);

        doReturn(new PageImpl<>(resourceMatches)).when(resourceServiceMock).getDonatedResourcesForOrganization(
            anyLong(), any(RestParameters.class)
        );


        restOrganizationMockMvc.perform(get("/app/rest/organizations/1/donatedresources?page=0&pageSize=20")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.resourceMatches").isArray())
            .andExpect(jsonPath("$.totalItems").value(2));
    }

    @Test
    public void testGetDonatedResources_throwNoSuchOrganizationException() throws Exception {
        doThrow(NoSuchEntityException.class).when(resourceServiceMock).getDonatedResourcesForOrganization(
            anyLong(), any(RestParameters.class)
        );

        restOrganizationMockMvc.perform(get("/app/rest/organizations/1/donatedresources?page=0&pageSize=20")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
    }
}
