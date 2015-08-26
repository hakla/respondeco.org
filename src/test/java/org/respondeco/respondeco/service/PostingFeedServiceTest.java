package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Christoph Schiessl on 16/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class PostingFeedServiceTest {

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private PostingRepository postingRepositoryMock;

    @Mock
    private PostingFeedRepository postingFeedRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private UserService userService;

    private PostingFeedService postingFeedService;

    private User projectManager;
    private User orgOwner;
    private Project basicProject;
    private Organization projectOrganization;
    private PostingFeed postingFeedProject;
    private PostingFeed postingFeedOrganization;
    private Posting posting1;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        postingFeedService = new PostingFeedService(
                postingFeedRepositoryMock,
                projectRepositoryMock,
                organizationRepositoryMock,
                userService,
                postingRepositoryMock);

        projectManager = new User();
        projectManager.setId(1L);
        projectManager.setLogin("testManager");

        orgOwner = new User();
        orgOwner.setId(2L);
        orgOwner.setLogin("testOwner");

        postingFeedOrganization = new PostingFeed();
        postingFeedOrganization.setId(2L);

        projectOrganization = new Organization();
        projectOrganization.setId(1L);
        projectOrganization.setName("projectOrg");
        projectOrganization.setOwner(orgOwner);
        projectOrganization.setPostingFeed(postingFeedOrganization);

        postingFeedProject = new PostingFeed();
        postingFeedProject.setId(1L);

        basicProject = new Project();
        basicProject.setId(1L);
        basicProject.setName("testProject");
        basicProject.setPurpose("testPurpose");
        basicProject.setConcrete(false);
        basicProject.setManager(projectManager);
        basicProject.setOrganization(projectOrganization);
        basicProject.setPostingFeed(postingFeedProject);

        posting1 = new Posting();
        posting1.setId(1L);
        posting1.setAuthor(projectManager);
        posting1.setInformation("testPosting");


    }

    @Test
     public void testAddPostingForProject() throws NoSuchEntityException, PostingFeedException {
        when(userService.getUserWithAuthorities()).thenReturn(projectManager);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

        postingFeedService.addPostingForProjects(basicProject.getId(),"posting1");

        verify(postingRepositoryMock, times(1)).save(isA(Posting.class));
    }

    @Test(expected = NoSuchEntityException.class)
    public void testAddPostingForProject_NoSuchProject() throws NoSuchEntityException, PostingFeedException {
        when(userService.getUserWithAuthorities()).thenReturn(projectManager);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(null);

        postingFeedService.addPostingForProjects(basicProject.getId(),"posting1");

    }

    @Test(expected = PostingFeedException.class)
    public void testAddPostingForProject_NotManagerOfProject() throws NoSuchEntityException, PostingFeedException {
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

        postingFeedService.addPostingForProjects(basicProject.getId(),"posting1");

    }

    @Test(expected = PostingFeedException.class)
    public void testAddPostingForProject_PostingIsEmpty() throws NoSuchEntityException, PostingFeedException {
        when(userService.getUserWithAuthorities()).thenReturn(projectManager);
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

        postingFeedService.addPostingForProjects(basicProject.getId(),"");

    }

    @Test(expected = PostingFeedException.class)
    public void testAddPostingForProject_NoPostingFeed() throws NoSuchEntityException, PostingFeedException {
        when(userService.getUserWithAuthorities()).thenReturn(projectManager);

        basicProject.setPostingFeed(null);

        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId())).thenReturn(basicProject);

        postingFeedService.addPostingForProjects(basicProject.getId(),"posting1");

    }

    @Test
    public void testAddPostingForOrganization() throws NoSuchEntityException, PostingFeedException {
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(organizationRepositoryMock.findOne(projectOrganization.getId()))
                .thenReturn(projectOrganization);

        postingFeedService.createPostingForOrganization(projectOrganization.getId(), "posting2");

        verify(postingRepositoryMock, times(1)).save(isA(Posting.class));
    }

    @Test(expected = NoSuchEntityException.class)
    public void testAddPostingForOrganization_NoSuchOrganization() throws NoSuchEntityException,
            PostingFeedException {
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(organizationRepositoryMock.findOne(projectOrganization.getId())).thenReturn(null);

        postingFeedService.createPostingForOrganization(projectOrganization.getId(), "posting1");

    }

    @Test(expected = PostingFeedException.class)
    public void testAddPostingForOrganization_NotOwnerOfOrganization() throws NoSuchEntityException,
            PostingFeedException {
        when(userService.getUserWithAuthorities()).thenReturn(projectManager);
        when(organizationRepositoryMock.findOne(projectOrganization.getId()))
                .thenReturn(projectOrganization);

        postingFeedService.createPostingForOrganization(projectOrganization.getId(), "posting2");

    }

    @Test(expected = PostingFeedException.class)
    public void testAddPostingForOrganization_PostingIsEmpty() throws NoSuchEntityException, PostingFeedException {
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(organizationRepositoryMock.findOne(projectOrganization.getId()))
                .thenReturn(projectOrganization);

        postingFeedService.createPostingForOrganization(projectOrganization.getId(), "");

    }

    @Test(expected = PostingFeedException.class)
    public void testAddPostingForOrganization_NoPostingFeed() throws NoSuchEntityException, PostingFeedException {
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);

        projectOrganization.setPostingFeed(null);

        when(organizationRepositoryMock.findOne(projectOrganization.getId()))
                .thenReturn(projectOrganization);

        postingFeedService.createPostingForOrganization(projectOrganization.getId(), "posting2");

    }

    @Test
    public void testGetPostingsForOrganization() throws NoSuchEntityException {
        when(organizationRepositoryMock.findOne(projectOrganization.getId()))
                .thenReturn(projectOrganization);
        RestParameters restParameters = new RestParameters(null, null, null, null);
        postingFeedService.getPostingsForOrganization(projectOrganization.getId(), restParameters.buildPageRequest());

        verify(postingFeedRepositoryMock, times(1)).getPostingsForOrganization(projectOrganization.getId(), restParameters.buildPageRequest());
    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetPostingsForOrganization_NoSuchOrganization() throws NoSuchEntityException {
        when(organizationRepositoryMock.findOne(projectOrganization.getId()))
                .thenReturn(null);

        postingFeedService.getPostingsForOrganization(projectOrganization.getId(), null);
    }

    @Test
    public void testGetPostingsForProject() throws NoSuchEntityException {
        when(projectRepositoryMock.findByIdAndActiveIsTrue(projectOrganization.getId()))
                .thenReturn(basicProject);
        RestParameters restParameters = new RestParameters(null, null, null, null);
        postingFeedService.getPostingsForProject(basicProject.getId(), restParameters.buildPageRequest());

        verify(postingFeedRepositoryMock, times(1)).getPostingsForProject(basicProject.getId(), restParameters.buildPageRequest());
    }

    @Test(expected = NoSuchEntityException.class)
    public void testGetPostingsForProject_NoSuchOrganization() throws NoSuchEntityException {
        when(projectRepositoryMock.findByIdAndActiveIsTrue(basicProject.getId()))
                .thenReturn(null);

        postingFeedService.getPostingsForProject(basicProject.getId(), null);
    }

    @Test
    public void testDeletePosting() throws PostingException {
        posting1.setPostingfeed(postingFeedProject);
        postingRepositoryMock.save(posting1);
        when(userService.getUserWithAuthorities()).thenReturn(projectManager);
        when(postingRepositoryMock.findByIdAndActiveIsTrue(posting1.getId())).thenReturn(posting1);

        postingFeedService.deletePosting(posting1.getId());

        verify(postingRepositoryMock, times(1)).findByIdAndActiveIsTrue(posting1.getId());
    }

    @Test(expected = PostingException.class)
    public void testDeletePosting_NoSuchPosting() throws PostingException {
        posting1.setPostingfeed(postingFeedProject);
        postingRepositoryMock.save(posting1);
        when(userService.getUserWithAuthorities()).thenReturn(projectManager);
        when(postingRepositoryMock.findByIdAndActiveIsTrue(posting1.getId())).thenReturn(null);

        postingFeedService.deletePosting(posting1.getId());
    }

    @Test(expected = PostingException.class)
    public void testDeletePosting_NotAuthorOfOrganization() throws PostingException {
        posting1.setPostingfeed(postingFeedProject);
        postingRepositoryMock.save(posting1);
        when(userService.getUserWithAuthorities()).thenReturn(orgOwner);
        when(postingRepositoryMock.findByIdAndActiveIsTrue(posting1.getId())).thenReturn(posting1);

        postingFeedService.deletePosting(posting1.getId());
    }
}
