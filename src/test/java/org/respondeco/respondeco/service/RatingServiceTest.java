package org.respondeco.respondeco.service;


import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.testutil.ArgumentCaptor;
import org.respondeco.respondeco.web.rest.dto.ProjectResponseDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

/**
 * Test class for the UserService.
 *
 * @see org.respondeco.respondeco.service.UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepositoryMock;

    @Mock
    private ResourceMatchRepository resourceMatchRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private PropertyTagService propertyTagService;

    @Mock
    private ResourceService resourceService;

    @Mock
    private ImageRepository imageRepositoryMock;


    private RatingService ratingService;

    private User defaultUser;
    private User orgOwner;
    private Organization defaultOrganization;
    private Organization projectOrganization;
    private Project basicProject;
    private ResourceMatch defaultMatch;
    private Rating defaultRating;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        projectService = new ProjectService(projectRepositoryMock,userService,userRepositoryMock,propertyTagService,
                resourceService,imageRepositoryMock);
        ratingService = new RatingService(ratingRepositoryMock,resourceMatchRepositoryMock,projectRepositoryMock,
                organizationRepositoryMock,userService,projectService);

        orgOwner = new User();
        orgOwner.setId(1L);
        orgOwner.setLogin("orgOwner");

        defaultUser = new User();
        defaultUser.setId(2L);
        defaultUser.setLogin("testUser");

        defaultOrganization = new Organization();
        defaultOrganization.setName("testOrg");
        defaultOrganization.setId(1L);
        defaultOrganization.setOwner(orgOwner);

        orgOwner.setOrganization(defaultOrganization);
        defaultUser.setOrganization(defaultOrganization);

        projectOrganization = new Organization();
        projectOrganization.setName("projectOrg");
        projectOrganization.setId(2L);
        projectOrganization.setOwner(defaultUser);

        basicProject = new Project();
        basicProject.setId(1L);
        basicProject.setName("testProject");
        basicProject.setPurpose("testPurpose");
        basicProject.setConcrete(false);
        basicProject.setManager(defaultUser);
        basicProject.setOrganization(projectOrganization);

        defaultMatch = new ResourceMatch();
        defaultMatch.setId(1L);
        defaultMatch.setProject(basicProject);
        defaultMatch.setOrganization(defaultOrganization);
        defaultMatch.setAccepted(true);

        defaultRating = new Rating();
        defaultRating.setId(1L);
        defaultRating.setRating(2);
        defaultRating.setComment("testComment");
        defaultRating.setResourceMatch(defaultMatch);
    }

    @Test
    public void testRateProject() {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);
        when(resourceMatchRepositoryMock.findByProjectAndOrganization(basicProject, defaultOrganization))
                .thenReturn(Arrays.asList(defaultMatch));

        ratingService.rateProject(basicProject.getId(), 2, "testComment");

        verify(resourceMatchRepositoryMock, times(1)).save(isA(ResourceMatch.class));
        verify(ratingRepositoryMock, times(1)).save(isA(Rating.class));

    }

    @Test(expected = NoSuchProjectException.class)
    public void testRateProject_NoSuchProject() {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(null);
        when(resourceMatchRepositoryMock.findByProjectAndOrganization(basicProject,defaultOrganization))
                .thenReturn(Arrays.asList(defaultMatch));

        ratingService.rateProject(basicProject.getId(), 2, "testComment");

    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testRateProject_NoSuchOrganization() {

        orgOwner.setOrganization(null);

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);
        when(resourceMatchRepositoryMock.findByProjectAndOrganization(basicProject,defaultOrganization))
                .thenReturn(Arrays.asList(defaultMatch));

        ratingService.rateProject(basicProject.getId(), 2, "testComment");

    }

    @Test(expected = NoSuchResourceMatchException.class)
    public void testRateProject_NoSuchResourceMatch() {

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);
        when(resourceMatchRepositoryMock.findByProjectAndOrganization(basicProject,defaultOrganization))
                .thenReturn(new ArrayList<ResourceMatch>());

        ratingService.rateProject(basicProject.getId(), 2, "testComment");
    }

    @Test(expected = ProjectRatingException.class)
    public void testRateProject_NotAccepted() {

        defaultMatch.setAccepted(false);

        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);
        when(resourceMatchRepositoryMock.findByProjectAndOrganization(basicProject,defaultOrganization))
                .thenReturn(Arrays.asList(defaultMatch));

        ratingService.rateProject(basicProject.getId(), 2, "testComment");
    }

    @Test(expected = ProjectRatingException.class)
     public void testRateProject_NotOwnerOfOrganization() {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);
        when(resourceMatchRepositoryMock.findByProjectAndOrganization(basicProject,defaultOrganization))
                .thenReturn(Arrays.asList(defaultMatch));

        ratingService.rateProject(basicProject.getId(), 2, "testComment");
    }

    @Test(expected = ProjectRatingException.class)
    public void testRateProject_AlreadyRated() {

        defaultMatch.setProjectRating(defaultRating);
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);
        when(resourceMatchRepositoryMock.findByProjectAndOrganization(basicProject,defaultOrganization))
                .thenReturn(Arrays.asList(defaultMatch));

        ratingService.rateProject(basicProject.getId(), 2, "testComment");
    }

    @Test
    public void testRateOrganization() {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(resourceMatchRepositoryMock.findOne(defaultMatch.getId()))
                .thenReturn(defaultMatch);

        ratingService.rateOrganization(defaultOrganization.getId(),defaultMatch.getId(), 2, "testComment");

        verify(resourceMatchRepositoryMock, times(1)).save(isA(ResourceMatch.class));
        verify(ratingRepositoryMock, times(1)).save(isA(Rating.class));

    }

    @Test(expected = NoSuchProjectException.class)
    public void testRateOrganization_NoSuchProject() {
        defaultMatch.setProject(null);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(resourceMatchRepositoryMock.findOne(defaultMatch.getId()))
                .thenReturn(defaultMatch);

        ratingService.rateOrganization(defaultOrganization.getId(),defaultMatch.getId(), 2, "testComment");

    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testRateOrganization_NoSuchOrganization() {
        defaultMatch.setOrganization(null);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(resourceMatchRepositoryMock.findOne(defaultMatch.getId()))
                .thenReturn(defaultMatch);

        ratingService.rateOrganization(defaultOrganization.getId(),defaultMatch.getId(), 2, "testComment");

    }

    @Test(expected = NoSuchResourceMatchException.class)
    public void testRateOrganization_NoSuchResourceMatch() {

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(resourceMatchRepositoryMock.findOne(defaultMatch.getId()))
                .thenReturn(null);

        ratingService.rateOrganization(defaultOrganization.getId(),defaultMatch.getId(), 2, "testComment");

    }

    @Test(expected = SupporterRatingException.class)
    public void testRateOrganization_NotManagerOfProject() {
        basicProject.setManager(orgOwner);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(resourceMatchRepositoryMock.findOne(defaultMatch.getId()))
                .thenReturn(defaultMatch);

        ratingService.rateOrganization(defaultOrganization.getId(),defaultMatch.getId(), 2, "testComment");

    }

    @Test(expected = SupporterRatingException.class)
    public void testRateOrganization_OrgOwnerNotAllowedToRate() {
        defaultOrganization.setOwner(defaultUser);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(resourceMatchRepositoryMock.findOne(defaultMatch.getId()))
                .thenReturn(defaultMatch);

        ratingService.rateOrganization(defaultOrganization.getId(),defaultMatch.getId(), 2, "testComment");

    }

    @Test(expected = SupporterRatingException.class)
    public void testRateOrganization_NotAccepted() {
        defaultMatch.setAccepted(false);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(resourceMatchRepositoryMock.findOne(defaultMatch.getId()))
                .thenReturn(defaultMatch);

        ratingService.rateOrganization(defaultOrganization.getId(),defaultMatch.getId(), 2, "testComment");

    }

    @Test(expected = SupporterRatingException.class)
    public void testRateOrganization_AlreadyRated() {
        defaultMatch.setSupporterRating(defaultRating);

        when(userService.getUserWithAuthorities()).thenReturn(defaultUser);
        when(resourceMatchRepositoryMock.findOne(defaultMatch.getId()))
                .thenReturn(defaultMatch);

        ratingService.rateOrganization(defaultOrganization.getId(),defaultMatch.getId(), 2, "testComment");
    }

    @Test
    public void testGetAggregatedRatingByProject() {
        Object[][] objectArray = new Object[1][2];

        defaultMatch.setProjectRating(defaultRating);

        when(resourceMatchRepositoryMock.getAggregatedRatingByProject(basicProject.getId()))
                .thenReturn(objectArray);

        ratingService.getAggregatedRatingByProject(basicProject.getId());

        assertNotNull(objectArray[0][1]);
        verify(resourceMatchRepositoryMock, times(1)).getAggregatedRatingByProject(basicProject.getId());

    }

    @Test
    public void testGetAggregatedRatingByOrganization() {
        Object[][] objectArray = new Object[1][2];

        defaultMatch.setSupporterRating(defaultRating);

        when(resourceMatchRepositoryMock.getAggregatedRatingByOrganization(defaultOrganization.getId()))
                .thenReturn(objectArray);

        ratingService.getAggregatedRatingByOrganization(defaultOrganization.getId());

        assertNotNull(objectArray[0][1]);
        verify(resourceMatchRepositoryMock, times(1)).getAggregatedRatingByOrganization(defaultOrganization.getId());

    }
}
