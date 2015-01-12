package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.testutil.TestUtil;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Clemens Puehringer on 21/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
public class ProjectRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

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
