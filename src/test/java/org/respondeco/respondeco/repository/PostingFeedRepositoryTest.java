package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class PostingFeedRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private PostingRepository postingRepository;

    @Inject
    private PostingFeedRepository postingFeedRepository;

    private User orgAdmin;
    private User projectManager;
    private Organization organizationAdmin;
    private Project project;
    private Posting posting1;
    private Posting posting2;
    private Posting posting3;
    private Posting posting4;
    private PostingFeed postingFeedProject;
    private PostingFeed postingFeedOrganization;

    @Before
    public void setup() {

        orgAdmin = new User();
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setGender(Gender.UNSPECIFIED);
        userRepository.save(orgAdmin);

        projectManager = new User();
        projectManager.setLogin("projectManager");
        projectManager.setGender(Gender.UNSPECIFIED);
        userRepository.save(projectManager);

        posting1 = new Posting();
        posting1.setAuthor(orgAdmin);
        posting1.setInformation("posting1");


        posting2 = new Posting();
        posting2.setAuthor(orgAdmin);
        posting2.setInformation("posting2");

        posting3 = new Posting();
        posting3.setAuthor(projectManager);
        posting3.setInformation("posting3");

        posting4 = new Posting();
        posting4.setAuthor(projectManager);
        posting4.setInformation("posting4");

        List<Posting> postingsOrganization = new ArrayList<Posting>();
        postingsOrganization.add(posting1);
        postingsOrganization.add(posting2);

        List<Posting> postingsProject = new ArrayList<Posting>();
        postingsProject.add(posting3);
        postingsProject.add(posting4);

        postingFeedOrganization = new PostingFeed();

        postingFeedProject = new PostingFeed();

        organizationAdmin = new Organization();
        organizationAdmin.setName("testorg");
        organizationAdmin.setOwner(orgAdmin);

        postingFeedOrganization.setPostings(postingsOrganization);
        postingFeedRepository.save(postingFeedOrganization);
        posting1.setPostingfeed(postingFeedOrganization);
        postingRepository.save(posting1);
        posting2.setPostingfeed(postingFeedOrganization);
        postingRepository.save(posting2);
        organizationAdmin.setPostingFeed(postingFeedOrganization);
        organizationRepository.save(organizationAdmin);

        project = new Project();
        project.setName("testproject");
        project.setPurpose("testpurpose");
        project.setConcrete(false);
        project.setManager(projectManager);
        project.setOrganization(organizationAdmin);

        postingFeedProject.setPostings(postingsProject);
        postingFeedRepository.save(postingFeedProject);
        posting3.setPostingfeed(postingFeedProject);
        posting4.setPostingfeed(postingFeedProject);
        postingRepository.save(posting4);
        postingRepository.save(posting3);
        project.setPostingFeed(postingFeedProject);
        projectRepository.save(project);


    }

    @Test
    public void testGetPostingsForOrganization() {
        List<Posting> postings = postingFeedRepository.getPostingsForOrganization(organizationAdmin.getId());

        assertNotNull(postings);

        assertTrue(postings.get(0).equals(posting1));
        assertTrue(postings.get(1).equals(posting2));
    }

    @Test
    public void testGetPostingsForProjectInRightOrder() {
        List<Posting> postings = postingFeedRepository.getPostingsForProject(project.getId());

        assertNotNull(postings);

        assertTrue(postings.get(1).equals(posting3));
        assertTrue(postings.get(0).equals(posting4));
    }

}
