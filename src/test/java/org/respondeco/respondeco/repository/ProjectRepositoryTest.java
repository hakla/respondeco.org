package org.respondeco.respondeco.repository;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.testutil.TestUtil;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
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
    public void testFindByActiveIsTrue_shouldReturnOnlyActive() throws Exception {
        Project project2 = new Project();
        project2.setName("project2");
        project2.setPurpose("testpurpose2");
        project2.setConcrete(false);
        project2.setOrganization(organization);
        project2.setManager(orgAdmin);
        project2.setPostingFeed(postingFeed2);

        projectRepository.save(project);
        projectRepository.save(project2);

        List<Project> projects = projectRepository.findByActiveIsTrue(null).getContent();

        assertTrue(projects.contains(project));
        assertTrue(projects.contains(project2));

        project.setActive(false);
        projectRepository.save(project);

        projects = projectRepository.findByActiveIsTrue(null).getContent();
        assertFalse(projects.contains(project));
        assertTrue(projects.contains(project2));
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

    @Test
    @Transactional //to enable lazy initialization
    public void testFindByNameAndTags_shouldFindProjectByName() throws Exception {
        projectRepository.save(project);
        List<Project> projects = projectRepository.findByNameAndTags("testproject", null, null).getContent();
        assertEquals(1, projects.size());
        System.out.println(projects.get(0));
        assertEquals("testproject", projects.get(0).getName());
    }

    @Test
    @Transactional
    public void testFindByNameAndTags_shouldFindProjectsByNameLike() throws Exception {
        Project project2 = new Project();
        project2.setName("zzyzzyzyxx");
        project2.setPurpose("blub");
        project2.setConcrete(false);
        project2.setOrganization(organization);
        project2.setManager(orgAdmin);
        project2.setPostingFeed(postingFeedRepository.save(new PostingFeed()));

        Project project3 = new Project();
        project3.setName("xxxxxxxx");
        project3.setPurpose("blub");
        project3.setConcrete(false);
        project3.setOrganization(organization);
        project3.setManager(orgAdmin);
        project3.setPostingFeed(postingFeedRepository.save(new PostingFeed()));

        projectRepository.save(project);
        projectRepository.save(project2);
        projectRepository.save(project3);
        List<Project> projects = projectRepository.findByNameAndTags("%xx%", null, null).getContent();
        assertEquals(2, projects.size());

    }

    @Test
    @Transactional
    public void testFindByNameAndTags_shouldFindProjectsByTags() throws Exception {
        PropertyTag tag1 = new PropertyTag();
        tag1.setName("testtag");
        PropertyTag tag2 = new PropertyTag();
        tag2.setName("blub");
        PropertyTag tag3 = new PropertyTag();
        tag3.setName("xxxxxxx");

        Project project2 = new Project();
        project2.setName("project2");
        project2.setPurpose("blub");
        project2.setConcrete(false);
        project2.setOrganization(organization);
        project2.setManager(orgAdmin);
        project2.setPostingFeed(postingFeedRepository.save(new PostingFeed()));

        Project project3 = new Project();
        project3.setName("xxxxxxxx");
        project3.setPurpose("blub");
        project3.setConcrete(false);
        project3.setOrganization(organization);
        project3.setManager(orgAdmin);
        project3.setPostingFeed(postingFeedRepository.save(new PostingFeed()));

        project.setPropertyTags(Arrays.asList(tag1, tag3));
        project2.setPropertyTags(Arrays.asList(tag3));
        project3.setPropertyTags(Arrays.asList(tag1, tag2));

        propertyTagRepository.save(tag1);
        propertyTagRepository.save(tag2);
        propertyTagRepository.save(tag3);
        propertyTagRepository.flush();

        projectRepository.save(project);
        projectRepository.save(project2);
        projectRepository.save(project3);
        projectRepository.flush();

        List<Project> projects = projectRepository.findByNameAndTags(null, Arrays.asList(tag3.getName()), null).getContent();
        assertEquals(2, projects.size());
        assertTrue(projects.contains(project));
        assertTrue(projects.contains(project2));
    }

    @Test
    @Transactional
    public void testFindByNameAndTags_shouldFindProjectsByNameOrTags() throws Exception {
        PropertyTag tag1 = new PropertyTag();
        tag1.setName("testtag");
        PropertyTag tag2 = new PropertyTag();
        tag2.setName("blub");
        PropertyTag tag3 = new PropertyTag();
        tag3.setName("xxxxxxx");

        Project project2 = new Project();
        project2.setName("project2");
        project2.setPurpose("blub");
        project2.setConcrete(false);
        project2.setOrganization(organization);
        project2.setManager(orgAdmin);
        project2.setPostingFeed(postingFeedRepository.save(new PostingFeed()));

        Project project3 = new Project();
        project3.setName("xxxxxxxx");
        project3.setPurpose("blub");
        project3.setConcrete(false);
        project3.setOrganization(organization);
        project3.setManager(orgAdmin);
        project3.setPostingFeed(postingFeedRepository.save(new PostingFeed()));

        project.setPropertyTags(Arrays.asList(tag1, tag3));
        project2.setPropertyTags(Arrays.asList(tag3));
        project3.setPropertyTags(Arrays.asList(tag2));

        propertyTagRepository.save(tag1);
        propertyTagRepository.save(tag2);
        propertyTagRepository.save(tag3);
        propertyTagRepository.flush();

        projectRepository.save(project);
        projectRepository.save(project2);
        projectRepository.save(project3);
        projectRepository.flush();

        List<Project> projects = projectRepository.findByNameAndTags("%xxxxx%", Arrays.asList(tag1.getName()), null).getContent();
        assertEquals(2, projects.size());
        assertTrue(projects.contains(project));
        assertTrue(projects.contains(project3));

    }

    @Test
    @Transactional
    public void testFindByNameAndTags_shouldWorkWithOffsetAndLimit() throws Exception {
        List<String> ascendingStrings = TestUtil.getAscendingStrings(20, "xxxxxx");
        List<String> randomStrings = TestUtil.getAscendingStrings(20);
        Project p;
        for(int i=0;i<20;i++) {
            p = new Project();
            p.setName(ascendingStrings.get(i));
            p.setPurpose("purpose");
            p.setConcrete(false);
            p.setManager(orgAdmin);
            p.setOrganization(organization);
            p.setPostingFeed(postingFeedRepository.save(new PostingFeed()));
            projectRepository.save(p);
        }
        for(int i=0;i<20;i++) {
            p = new Project();
            p.setName(randomStrings.get(i));
            p.setPurpose("purpose");
            p.setConcrete(false);
            p.setManager(orgAdmin);
            p.setOrganization(organization);
            p.setPostingFeed(postingFeedRepository.save(new PostingFeed()));
            projectRepository.save(p);
        }
        projectRepository.flush();

        PageRequest request = new PageRequest(1, 5, new Sort(
                new Sort.Order(Sort.Direction.ASC, "name")
        ));
        List<Project> projects = projectRepository.findByNameAndTags("%xxxxx%", null, request).getContent();
        assertEquals(5, projects.size());
        for(int i=0;i<5;i++) {
            assertEquals(ascendingStrings.get(i+5), projects.get(i).getName());
        }
    }

    @Test
    @Transactional
    public void testFindByOrganizationAndNameAndTags_shouldOnlyReturnProjectsFromOrganization() throws Exception {
        List<String> ascendingStrings = TestUtil.getAscendingStrings(30, "project");
        List<String> randomStrings = TestUtil.getAscendingStrings(20);
        Project p;
        for(int i=0;i<30;i++) {
            p = new Project();
            p.setName("project" + i);
            p.setPurpose(ascendingStrings.get(i));
            p.setConcrete(false);
            p.setManager(orgAdmin);
            p.setOrganization(organization);
            p.setPostingFeed(postingFeedRepository.save(new PostingFeed()));
            projectRepository.save(p);
        }

        User otherUser = new User();
        otherUser.setLogin("other");
        otherUser.setGender(Gender.UNSPECIFIED);
        userRepository.save(otherUser);
        userRepository.flush();
        Organization otherOrg = new Organization();
        otherOrg.setName("otherorg");
        otherOrg.setOwner(otherUser);
        otherOrg.setPostingFeed(postingFeedRepository.save(new PostingFeed()));
        organizationRepository.save(otherOrg);
        organizationRepository.flush();
        for(int i=0;i<20;i++) {
            p = new Project();
            p.setName("project_" + i);
            p.setPurpose("purpose");
            p.setConcrete(false);
            p.setManager(otherUser);
            p.setOrganization(otherOrg);
            p.setPostingFeed(postingFeedRepository.save(new PostingFeed()));
            projectRepository.save(p);
        }
        projectRepository.flush();

        List<Project> projects = projectRepository
                .findByOrganizationAndNameAndTags(organization.getId(), "%project%", null, null).getContent();
        assertEquals(30, projects.size());
        for(int i=0;i<30;i++) {
            assertEquals(organization, projects.get(i).getOrganization());
        }
    }

}
