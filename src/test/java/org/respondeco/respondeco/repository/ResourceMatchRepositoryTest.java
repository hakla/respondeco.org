package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementRequestDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
public class ResourceMatchRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

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

    @Inject
    private ResourceOfferRepository resourceOfferRepository;

    @Inject
    private ResourceRequirementRepository resourceRequirementRepository;

    @Inject
    private PostingFeedRepository postingFeedRepository;

    private User orgAdmin;
    private Organization organization;
    private Project project;
    private Rating rating;
    private Rating rating2;
    private Rating rating3;
    private Rating rating4;
    private ResourceMatch resourceMatch;
    private ResourceMatch resourceMatch2;
    private ResourceRequirement resourceRequirement;
    private ResourceOffer resourceOffer;
    private PostingFeed postingFeed;
    private PostingFeed postingFeed2;

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

        organization = new Organization();
        organization.setName("testorg");
        organization.setOwner(orgAdmin);
        organization.setPostingFeed(postingFeed);
        organizationRepository.save(organization);

        project = new Project();
        project.setName("testproject");
        project.setPurpose("testpurpose");
        project.setConcrete(false);
        project.setManager(orgAdmin);
        project.setOrganization(organization);
        project.setPostingFeed(postingFeed2);
        projectRepository.save(project);

        resourceOffer = new ResourceOffer();
        resourceOffer.setAmount(new BigDecimal(1));
        resourceOffer.setOriginalAmount(new BigDecimal(1));
        resourceOffer.setName("testOffer");
        resourceOffer.setDescription("testDescription");
        resourceOffer.setOrganization(organization);
        resourceOfferRepository.save(resourceOffer);

        resourceRequirement = new ResourceRequirement();
        resourceRequirement.setAmount(new BigDecimal(1));
        resourceRequirement.setOriginalAmount(new BigDecimal(1));
        resourceRequirement.setName("testOffer");
        resourceRequirement.setDescription("testDescription");
        resourceRequirement.setProject(project);
        resourceRequirementRepository.save(resourceRequirement);

        resourceMatch = new ResourceMatch();
        resourceMatch.setProject(project);
        resourceMatch.setAccepted(true);
        resourceMatch.setOrganization(organization);
        resourceMatch.setResourceOffer(resourceOffer);
        resourceMatch.setResourceRequirement(resourceRequirement);

        resourceMatch2 = new ResourceMatch();
        resourceMatch2.setProject(project);
        resourceMatch2.setAccepted(true);
        resourceMatch2.setOrganization(organization);

        rating = new Rating();
        rating.setRating(2);
        rating.setComment("test");
        rating.setResourceMatch(resourceMatch);
        resourceMatch.setProjectRating(rating);
        ratingRepository.save(rating);

        rating2 = new Rating();
        rating2.setRating(3);
        rating2.setComment("test2");
        rating2.setResourceMatch(resourceMatch2);
        resourceMatch2.setProjectRating(rating2);
        ratingRepository.save(rating2);

        rating3 = new Rating();
        rating3.setRating(1);
        rating3.setComment("test3");
        rating3.setResourceMatch(resourceMatch);
        resourceMatch.setSupporterRating(rating3);
        ratingRepository.save(rating3);

        rating4 = new Rating();
        rating4.setRating(3);
        rating4.setComment("test4");
        rating4.setResourceMatch(resourceMatch2);
        resourceMatch2.setSupporterRating(rating4);
        ratingRepository.save(rating4);

    }

    @Test
    public void testGetAggregatedRatingForProject() {

        resourceMatchRepository.save(resourceMatch);
        resourceMatchRepository.save(resourceMatch2);

        Object[][] aggregatedRating = resourceMatchRepository.getAggregatedRatingByProject(project.getId());
        Long count = new Long(2);
        Double rating = new Double(2.5);

        assertTrue(aggregatedRating[0] != null);
        assertTrue(aggregatedRating[0][0] != null);
        assertTrue(aggregatedRating[0][1] != null);
        assertEquals(aggregatedRating[0][0],count);
        assertEquals(aggregatedRating[0][1],rating);
    }

    @Test
    public void testGetAggregatedRatingForOrganization() {

        resourceMatchRepository.save(resourceMatch);
        resourceMatchRepository.save(resourceMatch2);

        Object[][] aggregatedRating = resourceMatchRepository.getAggregatedRatingByOrganization(organization.getId());
        Long count = new Long(2);
        Double rating = new Double(2.0);

        assertTrue(aggregatedRating[0] != null);
        assertTrue(aggregatedRating[0][0] != null);
        assertTrue(aggregatedRating[0][1] != null);
        assertEquals(aggregatedRating[0][0],count);
        assertEquals(aggregatedRating[0][1],rating);
    }

    @Test
    public void testFindByProjectAndOrganization() {

        resourceMatchRepository.save(resourceMatch);
        resourceMatchRepository.save(resourceMatch2);
        List<ResourceMatch> savedResourceMatches = resourceMatchRepository.findByProjectAndOrganization(project,organization);

        assertTrue(savedResourceMatches.isEmpty()==false);
        assertTrue(savedResourceMatches.size()==2);
        assertTrue(savedResourceMatches.contains(resourceMatch));

    }

    @Test
    public void testFindByProjectIdAndAcceptedIsTrueAndActiveIsTrue() {

        resourceMatchRepository.save(resourceMatch);
        resourceMatch2.setAccepted(false);
        resourceMatchRepository.save(resourceMatch2);
        List<ResourceMatch> savedResourceMatches = resourceMatchRepository.findByProjectIdAndAcceptedIsTrueAndActiveIsTrue(project.getId());

        assertTrue(savedResourceMatches.isEmpty()==false);
        assertTrue(savedResourceMatches.size()==1);
        assertTrue(savedResourceMatches.contains(resourceMatch));
    }

    @Test
    public void testFindByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue() {

        resourceMatchRepository.save(resourceMatch);
        resourceMatchRepository.save(resourceMatch2);
        List<ResourceMatch> savedResourceMatches = resourceMatchRepository
                .findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
                        resourceOffer,
                        resourceRequirement,
                        organization,
                        project);

        assertTrue(savedResourceMatches.isEmpty()==false);
        assertTrue(savedResourceMatches.size()==1);
        assertTrue(savedResourceMatches.contains(resourceMatch));
    }
}
