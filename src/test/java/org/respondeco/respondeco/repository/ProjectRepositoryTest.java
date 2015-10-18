package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.respondeco.respondeco.RepositoryLayerTest;
import org.respondeco.respondeco.domain.*;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by Clemens Puehringer on 21/11/14.
 */

public class ProjectRepositoryTest extends RepositoryLayerTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private PropertyTagRepository propertyTagRepository;

    @Inject
    private PostingFeedRepository postingFeedRepository;

    private User orgAdmin;
    private Organization organization;
    private Project project;
    private PostingFeed postingFeed;
    private PostingFeed postingFeed2;
    private PostingFeed postingFeed3;
    private PostingFeed postingFeed4;
    private PostingFeed postingFeed5;
    private PostingFeed postingFeed6;

    @Before
    public void setup() {

        orgAdmin = new User();
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setGender(Gender.UNSPECIFIED);
        userRepository.save(orgAdmin);

        postingFeed = new PostingFeed();
        postingFeedRepository.save(postingFeed);

        postingFeed2 = new PostingFeed();
        postingFeedRepository.save(postingFeed2);

        postingFeed3 = new PostingFeed();
        postingFeedRepository.save(postingFeed3);

        postingFeed4 = new PostingFeed();
        postingFeedRepository.save(postingFeed4);

        postingFeed5 = new PostingFeed();
        postingFeedRepository.save(postingFeed5);

        postingFeed6 = new PostingFeed();
        postingFeedRepository.save(postingFeed6);

        organization = new Organization();
        organization.setName("testorg");
        organization.setOwner(orgAdmin);
        organization.setPostingFeed(postingFeed3);
        organizationRepository.save(organization);

        project = new Project();
        project.setName("testproject");
        project.setPurpose("testpurpose");
        project.setConcrete(false);
        project.setOrganization(organization);
        project.setManager(orgAdmin);
        project.setPostingFeed(postingFeed);
    }

    @Test
    @Transactional
    public void testFindByIdAndActiveIsTrue_shouldReturnActiveProject() throws Exception {
        projectRepository.save(project);

        Project savedProject = projectRepository.findByIdAndActiveIsTrue(project.getId());
        assertTrue(savedProject.equals(project));

    }

    @Test
    public void testFindByIdAndActiveIsTrue_shouldNotReturnInactiveProject() throws Exception {
        project.setActive(false);
        projectRepository.save(project);

        Project savedProject = projectRepository.findByIdAndActiveIsTrue(project.getId());
        assertNull(savedProject);

    }

    /**
     * Test save Project Follow and retrieve the data for assertion
     * @throws Exception
     */
    @Test
    @Transactional
    public void testfindByUserAndProject_General() throws Exception{

        project.setFollowingUsers(Arrays.asList(orgAdmin));
        projectRepository.save(project);
        projectRepository.flush();

        Project testObject = projectRepository.findByUserIdAndProjectId(orgAdmin.getId(), project.getId());
        assertNotNull(testObject);
        assertEquals(testObject.getFollowingUsers().get(0), orgAdmin);
    }

    /**
     * No followers found
     * @throws Exception
     */
    @Test
    @Transactional
    public void testfindByUserAndProject_NoFollowers() throws Exception{

        Project testObject = projectRepository.findByUserIdAndProjectId(orgAdmin.getId(), project.getId());
        assertNull(testObject);
    }

}
