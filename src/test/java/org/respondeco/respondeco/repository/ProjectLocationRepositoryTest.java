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
import java.math.BigInteger;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benjamin Fraller on 05.01.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
public class ProjectLocationRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private ProjectLocationRepository projectLocationRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    private ProjectLocation projectLocation;
    private Project project;
    private User user;
    private Organization organization;
    private PostingFeed postingFeed;

    @Before
    public void setup() {

        user = new User();
        user.setOrganization(organization);
        user.setActivated(true);
        user.setLogin("login");
        user.setGender(Gender.MALE);
        userRepository.save(user);

        postingFeed = new PostingFeed();
        postingFeed.setId(1L);

        organization = new Organization();
        organization.setName("testorg");
        organization.setOwner(user);
        organization.setPostingFeed(postingFeed);
        organizationRepository.save(organization);

        project = new Project();
        project.setId(1L);
        project.setName("project");
        project.setManager(user);
        project.setOrganization(organization);
        project.setCreatedBy("user");
        project.setPostingFeed(postingFeed);

        projectRepository.save(project);

        projectLocation = new ProjectLocation();
        projectLocation.setId(1L);
        projectLocation.setLat(10.0);
        projectLocation.setLng(10.0);
        projectLocation.setAddress("address");
        projectLocation.setProject(project);

        projectLocationRepository.save(projectLocation);
    }

    @Test
    public void testFindNearProjects_shouldReturnNearProjects() {

        //1 km radius - 0.997km distance from 10,10 -> should be found
        List<Object[]> objList = projectLocationRepository.findNearProjects(10.0,10.00913,1);

        Object[] object = objList.get(0);
        assertEquals(object[0], new BigInteger("1"));
        assertEquals((double)object[1], 10.0, 0.1);
        assertEquals((double)object[2], 10.00913,0.1);
        assertEquals(object[3], "address");
        assertEquals(object[4], new BigInteger("1"));
    }

    @Test
    public void testFindNearProjects_shouldNotFindAnyProjects() {

        //1 km radius - 0.997km distance from 10,10 -> should be found
        List<Object[]> objList = projectLocationRepository.findNearProjects(10.0,10.00914,1);

        assertTrue(objList.isEmpty());
    }
}
