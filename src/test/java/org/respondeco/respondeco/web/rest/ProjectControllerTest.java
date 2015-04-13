package org.respondeco.respondeco.web.rest;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.respondeco.respondeco.testutil.TestUtil;
import org.respondeco.respondeco.web.rest.dto.ImageDTO;
import org.respondeco.respondeco.web.rest.dto.ProjectApplyDTO;
import org.respondeco.respondeco.web.rest.dto.ProjectDTO;
import org.respondeco.respondeco.web.rest.dto.RatingRequestDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProjectIdeaResource REST controller.
 *
 * @see ProjectController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class})
public class ProjectControllerTest {

    private static final Long DEFAULT_ID = new Long(1L);

    private static final Long DEFAULT_ORGANIZATION_ID = 0L;

    private static final Long DEFAULT_MANAGER_ID = 0L;
    private static final Long UPDATED_MANAGER_ID = 1L;

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";

    private static final String DEFAULT_PURPOSE = "SAMPLE_TEXT";
    private static final String UPDATED_PURPOSE = "UPDATED_TEXT";

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private PropertyTagService propertyTagServiceMock;

    private ImageRepository imageRepositoryMock;

    @Mock
    private ResourceService resourceServiceMock;

    @Mock
    private RatingService ratingServiceMock;

    @Mock
    private ProjectLocationService projectLocationServiceMock;

    @Mock
    private PropertyTagRepository propertyTagRepositoryMock;

    @Mock
    private ResourceMatchRepository resourceMatchRepositoryMock;

    @Mock
    private PostingFeedRepository postingFeedRepositoryMock;

    @Mock
    private PostingFeedService postingFeedServiceMock;

    @Mock
    private ProjectLocationRepository projectLocationRepositoryMock;

    private ProjectService projectServiceMock;
    private MockMvc restProjectMockMvc;
    private ProjectDTO projectDTO;
    private Project project;
    private Organization defaultOrganization;
    private User orgAdmin;
    private User orgMember;
    private ProjectLocation projectLocation;
    private PostingFeed postingFeed;

    private ArgumentCaptor<Object> voidInterceptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        projectServiceMock = spy(new ProjectService(
            projectRepositoryMock,
            userServiceMock,
            userRepositoryMock,
            propertyTagServiceMock,
            resourceServiceMock,
            imageRepositoryMock,
            resourceMatchRepositoryMock,
            postingFeedRepositoryMock,
            projectLocationRepositoryMock));
        ProjectController projectController = new ProjectController(
            projectServiceMock,
            resourceServiceMock,
            ratingServiceMock,
            userServiceMock,
            postingFeedServiceMock,
            projectLocationServiceMock);

        this.restProjectMockMvc = MockMvcBuilders.standaloneSetup(projectController).build();

        projectDTO = new ProjectDTO();
        projectDTO.setName(DEFAULT_NAME);
        projectDTO.setPurpose(DEFAULT_PURPOSE);
        projectDTO.setConcrete(false);
        projectDTO.setLogo(new ImageDTO());


        defaultOrganization = new Organization();
        defaultOrganization.setId(100L);
        defaultOrganization.setName("testorg");

        HashSet<Authority> userAuthorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.USER);
        userAuthorities.add(authority);

        orgAdmin = new User();
        orgAdmin.setId(100L);
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setGender(Gender.UNSPECIFIED);
        orgAdmin.setOrganization(defaultOrganization);
        orgAdmin.setAuthorities(userAuthorities);

        orgMember = new User();
        orgMember.setId(200L);
        orgMember.setLogin("orgMember");
        orgMember.setGender(Gender.UNSPECIFIED);
        orgMember.setOrganization(defaultOrganization);
        orgMember.setAuthorities(userAuthorities);

        postingFeed = new PostingFeed();
        postingFeed.setId(1L);

        project = new Project();
        project.setId(100L);
        project.setOrganization(defaultOrganization);
        project.setManager(orgMember);
        project.setName(DEFAULT_NAME);
        project.setPurpose(DEFAULT_PURPOSE);
        project.setConcrete(false);

        projectLocation = new ProjectLocation();
        projectLocation.setId(1L);
        projectLocation.setAddress("address");
        projectLocation.setLat(10.0f);
        projectLocation.setLng(10.0f);
        projectLocation.setProject(project);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgMember);
        doReturn(new ArrayList<PropertyTag>()).when(propertyTagServiceMock).getOrCreateTags(anyObject());

        defaultOrganization.setOwner(orgAdmin);

        voidInterceptor = ArgumentCaptor.forType(Object.class, 0, false);
    }

    @Test
    public void testCRUDProject() throws Exception {

        doReturn(project).when(projectServiceMock).create(
            projectDTO.getName(),
            projectDTO.getPurpose(),
            projectDTO.getConcrete(),
            projectDTO.getStartDate(),
            projectDTO.getPropertyTags(),
            projectDTO.getResourceRequirements(),
            projectDTO.getLogo().getId());

        doReturn(projectLocation).when(projectLocationServiceMock).createProjectLocation(1L, projectLocation.getAddress(),
            projectLocation.getLat(), projectLocation.getLng());

        // Create Project
        restProjectMockMvc.perform(post("/app/rest/projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
            .andExpect(status().isCreated());

        verify(projectServiceMock, times(1)).create(
            projectDTO.getName(),
            projectDTO.getPurpose(),
            projectDTO.getConcrete(),
            projectDTO.getStartDate(),
            projectDTO.getPropertyTags(),
            projectDTO.getResourceRequirements(),
            projectDTO.getLogo().getId());

        doReturn(project).when(projectServiceMock).findProjectById(project.getId());
        // Read Project

        restProjectMockMvc.perform(get("/app/rest/projects/{id}", project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.organizationId").value(defaultOrganization.getId().intValue()))
            .andExpect(jsonPath("$.managerId").value(orgMember.getId().intValue()))
            .andExpect(jsonPath("$.name").value(projectDTO.getName()))
            .andExpect(jsonPath("$.purpose").value(projectDTO.getPurpose()));

        verify(projectServiceMock, times(1)).findProjectById(project.getId());

        // Update Project
        projectDTO.setId(project.getId());
        projectDTO.setName(UPDATED_NAME);
        projectDTO.setPurpose(UPDATED_PURPOSE);

        doReturn(project).when(projectServiceMock).update(
            projectDTO.getId(),
            projectDTO.getName(),
            projectDTO.getPurpose(),
            projectDTO.getConcrete(),
            projectDTO.getStartDate(),
            projectDTO.getLogo().getId(),
            projectDTO.getPropertyTags(),
            projectDTO.getResourceRequirements());

        restProjectMockMvc.perform(put("/app/rest/projects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(projectDTO)))
            .andExpect(status().isOk());

        verify(projectServiceMock, times(1)).update(
            projectDTO.getId(),
            projectDTO.getName(),
            projectDTO.getPurpose(),
            projectDTO.getConcrete(),
            projectDTO.getStartDate(),
            projectDTO.getLogo().getId(),
            projectDTO.getPropertyTags(),
            projectDTO.getResourceRequirements());

        doReturn(project).when(projectServiceMock).delete(project.getId());
        // Delete Project
        restProjectMockMvc.perform(delete("/app/rest/projects/{id}", project.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        verify(projectServiceMock, times(1)).delete(project.getId());

        when(projectRepositoryMock.findByIdAndActiveIsTrue(1000L)).thenReturn(null);
        // Read nonexisting Project
        restProjectMockMvc.perform(get("/app/rest/projects/{id}", 1000L)
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());

        verify(projectRepositoryMock, times(1)).findByIdAndActiveIsTrue(1000L);

    }

    @Test
    public void testGetByNameAndTags_shouldCallServiceToFindProjects() throws Exception {
        Project project2 = new Project();
        project2.setId(200L);
        project2.setName("test2");
        project2.setPurpose("testpurpose 2");
        project2.setOrganization(defaultOrganization);
        project2.setManager(orgMember);
        project2.setConcrete(false);

        doReturn(new PageImpl(Arrays.asList(project, project2))).when(projectServiceMock)
            .findProjects(isNull(String.class), isA(RestParameters.class));

        restProjectMockMvc.perform(get("/app/rest/projects"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$.totalItems", is(2)))
            .andExpect(jsonPath("$.projects[0].id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.projects[1].id").value(project2.getId().intValue()));

        verify(projectServiceMock, times(1))
            .findProjects(isNull(String.class), isA(RestParameters.class));
    }

    @Test
    public void testGetByOrganizationAndNameAndTags_shouldCallServiceToFindProjectsFromOrganization() throws Exception {
        Project project2 = new Project();
        project2.setId(200L);
        project2.setName("test2");
        project2.setPurpose("testpurpose 2");
        project2.setOrganization(defaultOrganization);
        project2.setManager(orgMember);
        project2.setConcrete(false);

        doReturn(new PageImpl(Arrays.asList(project, project2))).when(projectServiceMock)
            .findProjectsFromOrganization(isA(Long.class), isNull(String.class),
                isA(RestParameters.class));

        restProjectMockMvc.perform(get("/app/rest/organizations/{id}/projects", defaultOrganization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").exists())
            .andExpect(jsonPath("$.totalItems", is(2)))
            .andExpect(jsonPath("$.projects.[0].id").value(project.getId().intValue()))
            .andExpect(jsonPath("$.projects.[1].id").value(project2.getId().intValue()));

        verify(projectServiceMock, times(1))
            .findProjectsFromOrganization(isA(Long.class), isNull(String.class),
                isA(RestParameters.class));
    }

    @Test
    public void testProjectApplyOffer_SUCCESS() throws Exception {

        final ProjectApplyDTO expected = new ProjectApplyDTO();
        expected.setOrganizationId(1L);
        expected.setProjectId(2L);
        expected.setResourceOfferId(1L);
        expected.setResourceRequirementId(2L);

        final ResourceMatch actual = new ResourceMatch();

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            actual.setId(1L);

            actual.setAmount(new BigDecimal(10));
            ResourceOffer offer = new ResourceOffer();
            offer.setId((Long) args[0]);
            actual.setResourceOffer(offer);

            ResourceRequirement req = new ResourceRequirement();
            req.setId((Long) args[1]);
            actual.setResourceRequirement(req);

            Organization org = new Organization();
            org.setId((Long) args[2]);
            actual.setOrganization(org);

            Project prj = new Project();
            prj.setId((Long) args[3]);
            actual.setProject(prj);

            return actual;
        }).when(resourceServiceMock).createProjectApplyOffer(expected.getResourceOfferId(), expected.getResourceRequirementId(),
            expected.getOrganizationId(), expected.getProjectId());

        restProjectMockMvc.perform(post("/app/rest/projects/apply")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(expected)))
            .andExpect(status().isCreated());

        assertEquals(expected.getResourceOfferId(), actual.getResourceOffer().getId());
        assertEquals(expected.getResourceRequirementId(), actual.getResourceRequirement().getId());
        assertEquals(expected.getProjectId(), actual.getProject().getId());
        assertEquals(expected.getOrganizationId(), actual.getOrganization().getId());

        verify(resourceServiceMock, times(1))
            .createProjectApplyOffer(expected.getResourceOfferId(), expected.getResourceRequirementId(),
                expected.getOrganizationId(), expected.getProjectId());
    }

    @Test
    public void testProjectApplyOffer_FAIL_UnauthorizedUser() throws Exception {

        ProjectApplyDTO expected = new ProjectApplyDTO();

        when(resourceServiceMock
            .createProjectApplyOffer(expected.getResourceOfferId(), expected.getResourceRequirementId(),
                expected.getOrganizationId(), expected.getProjectId())).thenThrow(new ResourceNotFoundException(""));

        restProjectMockMvc.perform(post("/app//rest/projects/apply")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(expected)))
            .andExpect(status().isBadRequest());

        verify(resourceServiceMock, times(1))
            .createProjectApplyOffer(expected.getResourceOfferId(), expected.getResourceRequirementId(),
                expected.getOrganizationId(), expected.getProjectId());
    }

    @Test
    public void testProjectApplyOffer_FAIL_OnCurruptOrgOrReqOrOffer() throws Exception {

        ProjectApplyDTO expected = new ProjectApplyDTO();

        when(resourceServiceMock.createProjectApplyOffer(expected.getResourceOfferId(), expected.getResourceRequirementId(),
            expected.getOrganizationId(), expected.getProjectId())).thenThrow(new IllegalValueException("rest.test.item", "Error Accure"));

        restProjectMockMvc.perform(post("/app//rest/projects/apply")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(expected)))
            .andExpect(status().isBadRequest());

        verify(resourceServiceMock, times(1))
            .createProjectApplyOffer(expected.getResourceOfferId(), expected.getResourceRequirementId(),
                expected.getOrganizationId(), expected.getProjectId());
    }

    @Test
    public void testGetAggregatedRating_matchesFlagSet_shouldCallServiceForMatchPermissions() throws Exception {
        ResourceMatch rm1 = new ResourceMatch();
        rm1.setId(100L);
        RatingPermission perm1 = new RatingPermission();
        perm1.setAllowed(true);
        perm1.setResourceMatch(rm1);
        ResourceMatch rm2 = new ResourceMatch();
        rm2.setId(200L);
        RatingPermission perm2 = new RatingPermission();
        perm2.setAllowed(false);
        perm2.setResourceMatch(rm2);

        doReturn(Arrays.asList(perm1, perm2)).when(ratingServiceMock).checkPermissionsForMatches(anyList());

        restProjectMockMvc.perform(get("/app/rest/projects/1/ratings?permission=matches&matches=100,200"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].allowed").value(true))
            .andExpect(jsonPath("$[1].allowed").value(false));
    }

    @Test
    public void testGetAggregatedRating_matchesFlagSet_shouldCallServiceForProjectPermission() throws Exception {
        ResourceMatch rm1 = new ResourceMatch();
        rm1.setId(100L);
        RatingPermission perm1 = new RatingPermission();
        perm1.setAllowed(true);
        perm1.setResourceMatch(rm1);

        doReturn(perm1).when(ratingServiceMock).checkPermissionForProject(anyLong());

        restProjectMockMvc.perform(get("/app/rest/projects/1/ratings?permission=project"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].allowed").value(true));
    }

    @Test
    public void testGetAggregatedRating_expectOK_shouldCallServiceForAggregatedRating() throws Exception {
        AggregatedRating aggregatedRating = new AggregatedRating();
        aggregatedRating.setRating(3.5);
        aggregatedRating.setCount(5L);

        doReturn(aggregatedRating).when(ratingServiceMock).getAggregatedRatingByProject(anyLong());

        restProjectMockMvc.perform(get("/app/rest/projects/1/ratings"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.rating").value(3.5))
            .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    public void testRateProject_expectOK_shouldCallServiceToSaveRating() throws Exception {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO();
        ratingRequestDTO.setRating(5);
        ratingRequestDTO.setComment("foobar");
        ratingRequestDTO.setMatchid(100L);

        doAnswer(voidInterceptor).when(ratingServiceMock).rateProject(anyLong(), anyLong(), anyInt(), anyString());

        restProjectMockMvc.perform(post("/app/rest/projects/1/ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ratingRequestDTO)))
            .andExpect(status().isOk());

        verify(ratingServiceMock, times(1)).rateProject(anyLong(), anyLong(), anyInt(), anyString());
    }

    @Test
    public void testRateProject_expectNOT_FOUND_serviceThrowsNoSuchProjectException() throws Exception {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO();
        ratingRequestDTO.setRating(5);
        ratingRequestDTO.setComment("foobar");
        ratingRequestDTO.setMatchid(100L);

        doThrow(NoSuchEntityException.class).when(ratingServiceMock)
            .rateProject(anyLong(), anyLong(), anyInt(), anyString());

        restProjectMockMvc.perform(post("/app/rest/projects/1/ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ratingRequestDTO)))
            .andExpect(status().isNotFound());

        verify(ratingServiceMock, times(1)).rateProject(anyLong(), anyLong(), anyInt(), anyString());
    }

    @Test
    public void testRateProject_expectBAD_REQUEST_serviceThrowsRatingException() throws Exception {
        RatingRequestDTO ratingRequestDTO = new RatingRequestDTO();
        ratingRequestDTO.setRating(5);
        ratingRequestDTO.setComment("foobar");
        ratingRequestDTO.setMatchid(100L);

        doThrow(ProjectRatingException.class).when(ratingServiceMock)
            .rateProject(anyLong(), anyLong(), anyInt(), anyString());

        restProjectMockMvc.perform(post("/app/rest/projects/1/ratings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ratingRequestDTO)))
            .andExpect(status().isBadRequest());

        verify(ratingServiceMock, times(1)).rateProject(anyLong(), anyLong(), anyInt(), anyString());
    }

    @Test
    public void testGetAllLocations_expectOK_shouldReturnLocations() throws Exception {

        doReturn(Arrays.asList(projectLocation)).when(projectLocationServiceMock).getAllLocations();

        restProjectMockMvc.perform(get("/app/rest/locations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].address").value(projectLocation.getAddress()))
            .andExpect(jsonPath("$[0].latitude").value(new Double(projectLocation.getLat())))
            .andExpect(jsonPath("$[0].longitude").value(new Double(projectLocation.getLng())))
            .andExpect(jsonPath("$[0].project.id").value(project.getId().intValue()));

        verify(projectLocationServiceMock, times(1)).getAllLocations();
    }

    @Test
    public void testGetNearProjects_expectOK_shouldReturnNearProjects() throws Exception {

        doReturn(Arrays.asList(projectLocation)).when(projectLocationServiceMock).getNearProjects(10.0f, 15.0f, 100);

        restProjectMockMvc.perform(get("/app/rest/nearprojects?latitude=10.0&longitude=15.0&radius=100")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].address").value(projectLocation.getAddress()))
            .andExpect(jsonPath("$[0].latitude").value(new Double(projectLocation.getLat())))
            .andExpect(jsonPath("$[0].longitude").value(new Double(projectLocation.getLng())))
            .andExpect(jsonPath("$[0].project.id").value(project.getId().intValue()));
    }

    @Test
    public void testGetNearProjects_expectNOT_FOUND_serviceThrowsNoSuchProjectException() throws Exception {
        doThrow(NoSuchEntityException.class).when(projectLocationServiceMock).getNearProjects(10.0f, 15.0f, 100);

        restProjectMockMvc.perform(get("/app/rest/nearprojects?latitude=10.0&longitude=15.0&radius=100")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetNearProjects_expectBAD_REQUEST_serviceThrowsIllegalValueException() throws Exception {
        doThrow(IllegalValueException.class).when(projectLocationServiceMock).getNearProjects(10.0f, 15.0f, 100);

        restProjectMockMvc.perform(get("/app/rest/nearprojects?latitude=10.0&longitude=15.0&radius=100")
            .contentType(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostingForProject() throws Exception {
        Posting posting = new Posting();
        posting.setId(100L);
        posting.setAuthor(orgAdmin);
        posting.setPostingfeed(postingFeed);
        posting.setInformation("posting1");
        posting.setCreatedDate(DateTime.now());
        doReturn(posting).when(postingFeedServiceMock).addPostingForProjects(any(Long.class), any(String.class));

        restProjectMockMvc.perform(post("/app/rest/projects/{id}/postings", project.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes("posting1")))
            .andExpect(status().isOk());
    }

    @Test
    public void testPostingForProject_expectBAD_REQUEST_serviceThrowsPostingFeedException() throws Exception {
        doThrow(PostingFeedException.class).when(postingFeedServiceMock).addPostingForProjects(anyLong(), anyString());

        restProjectMockMvc.perform(post("/app/rest/projects/{id}/postings", project.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes("posting1")))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void testPostingForProject_expectNOT_FOUND_serviceThrowsNoSuchProjectException() throws Exception {
        doThrow(NoSuchEntityException.class).when(postingFeedServiceMock).addPostingForProjects(anyLong(), anyString());

        restProjectMockMvc.perform(post("/app/rest/projects/{id}/postings", project.getId())
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes("posting1")))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testGetPostingForProject() throws Exception {
        project.setPostingFeed(postingFeed);
        Posting posting = new Posting();
        posting.setId(100L);
        posting.setInformation("test posting");
        posting.setAuthor(orgAdmin);
        posting.setPostingfeed(postingFeed);
        posting.setCreatedDate(DateTime.now());
        Page<Posting> postingPage = new PageImpl<Posting>(Arrays.asList(posting));
        doReturn(postingPage).when(postingFeedServiceMock)
            .getPostingsForProject(any(Long.class), any(RestParameters.class));

        restProjectMockMvc.perform(get("/app/rest/projects/{id}/postings", project.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(postingFeedServiceMock, times(1)).getPostingsForProject(any(Long.class), any(RestParameters.class));
    }

    @Test
    public void testGetPostingForProject_NoSuchProject() throws Exception {
        doThrow(NoSuchEntityException.class).when(postingFeedServiceMock)
            .getPostingsForProject(anyLong(), isA(RestParameters.class));
        restProjectMockMvc.perform(get("/app/rest/projects/{id}/postings", project.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNotFound());
    }

    @Test
    public void testFollowingState_TRUE() throws Exception {
        Long id = 1L;
        Boolean result = true;
        doReturn(result)
            .when(projectServiceMock).followingState(id);

        restProjectMockMvc.perform(get("/app/rest/projects/{id}/followingstate", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.state").value(result))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(projectServiceMock, times(1)).followingState(id);
    }

    @Test
    public void testFollowingState_FALSE() throws Exception {
        Long id = 1L;
        Boolean result = false;
        doReturn(result)
            .when(projectServiceMock).followingState(id);

        restProjectMockMvc.perform(get("/app/rest/projects/{id}/followingstate", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.state").value(result))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        verify(projectServiceMock, times(1)).followingState(id);
    }

    @Test
    public void testFollow_SUCCESS() throws Exception {
        Long id = 1L;
        doNothing().when(projectServiceMock).follow(id);
        restProjectMockMvc.perform(post("/app/rest/projects/{id}/follow", id))
            .andExpect(status().isCreated());
        verify(projectServiceMock, times(1)).follow(id);
    }


    @Test
    public void testFollow_FAIL() throws Exception {
        Long id = 1L;
        doThrow(IllegalValueException.class).when(projectServiceMock).follow(id);

        restProjectMockMvc.perform(post("/app/rest/projects/{id}/follow", id))
            .andExpect(status().isBadRequest());
        verify(projectServiceMock, times(1)).follow(id);
    }

    @Test
    public void testUnfollow_SUCCESS() throws Exception {
        Long id = 1L;
        doNothing().when(projectServiceMock).unfollow(id);
        restProjectMockMvc.perform(delete("/app/rest/projects/{id}/unfollow", id))
            .andExpect(status().isOk());
        verify(projectServiceMock, times(1)).unfollow(id);
    }

    @Test
    public void testUnfollow_FAIL() throws Exception {
        Long id = 1L;
        doThrow(IllegalValueException.class).when(projectServiceMock).unfollow(id);
        restProjectMockMvc.perform(delete("/app/rest/projects/{id}/unfollow", id))
            .andExpect(status().isBadRequest());
        verify(projectServiceMock, times(1)).unfollow(id);
    }
}
