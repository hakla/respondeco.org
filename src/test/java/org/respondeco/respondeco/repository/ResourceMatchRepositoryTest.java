package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

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
public class ResourceMatchRepositoryTest {

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private RatingRepository ratingRepository;

    @Inject
    private ResourceMatchRepository resourceMatchRepository;

    private User orgAdmin;
    private Organization organization;
    private Project project;
    private Rating rating;
    private ResourceMatch resourceMatch;

    @Before
    public void setup() {
        /*
        projectRepository.deleteAll();
        projectRepository.flush();
        organizationRepository.deleteAll();
        organizationRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        ratingRepository.deleteAll();
        ratingRepository.flush();
        resourceMatchRepository.deleteAll();
        resourceMatchRepository.flush();
*/
        orgAdmin = new User();
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setGender(Gender.UNSPECIFIED);
        userRepository.save(orgAdmin);

        organization = new Organization();
        organization.setName("testorg");
        organization.setOwner(orgAdmin);
        organizationRepository.save(organization);

        project = new Project();
        project.setName("testproject");
        project.setPurpose("testpurpose");
        project.setConcrete(false);
        project.setManager(orgAdmin);
        project.setOrganization(organization);
        projectRepository.save(project);

        resourceMatch = new ResourceMatch();
        resourceMatch.setProject(project);
        resourceMatch.setOrganization(organization);
        resourceMatch.setProjectRating(rating);


        rating = new Rating();
        rating.setRating(2);
        rating.setComment("test");
        rating.setResourceMatch(resourceMatch);


    }

    @Test
    @Transactional
    public void testGetAggregatedRating() {

        resourceMatchRepository.save(resourceMatch);
        ratingRepository.save(rating);


        Object[] aggregatedRating = resourceMatchRepository.getAggregatedRatingByProject(project.getId());
        assertTrue(aggregatedRating[0].equals(1));

    }
}
