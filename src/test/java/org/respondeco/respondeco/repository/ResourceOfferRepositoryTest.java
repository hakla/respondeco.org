package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

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
public class ResourceOfferRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private UserRepository userRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private ResourceOfferRepository resourceOfferRepository;

    @Inject PostingFeedRepository postingFeedRepository;

    private User orgAdmin;
    private Organization organization;
    private Organization organization2;
    private ResourceOffer resourceOffer;
    private ResourceOffer resourceOffer2;
    private PostingFeed postingFeed;
    private PostingFeed postingFeed2;
    private PageRequest pageRequest;

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

        organization2 = new Organization();
        organization2.setName("testorg2");
        organization2.setOwner(orgAdmin);
        organization2.setPostingFeed(postingFeed2);
        organizationRepository.save(organization2);

        resourceOffer = new ResourceOffer();
        resourceOffer.setAmount(new BigDecimal(1));
        resourceOffer.setOriginalAmount(new BigDecimal(1));
        resourceOffer.setName("testOffer");
        resourceOffer.setDescription("testDescription");
        resourceOffer.setOrganization(organization);
        resourceOfferRepository.save(resourceOffer);

        resourceOffer2 = new ResourceOffer();
        resourceOffer2.setAmount(new BigDecimal(2));
        resourceOffer2.setOriginalAmount(new BigDecimal(1));
        resourceOffer2.setName("testOffer2");
        resourceOffer2.setDescription("testDescription2");
        resourceOffer2.setOrganization(organization);
        resourceOfferRepository.save(resourceOffer2);

        pageRequest = new PageRequest(0,20,null);

    }

    @Test
    public void testFindByName() {

        List<ResourceOffer> resourceOffers = resourceOfferRepository.findByName(resourceOffer.getName(),null);

        assertNotNull(resourceOffers);
        assertTrue(resourceOffers.contains(resourceOffer));
        assertFalse(resourceOffers.contains(resourceOffer2));
    }

    @Test
    public void testFindByActiveIsTrue() {

        Page<ResourceOffer> resourceOffersPage = resourceOfferRepository.findByActiveIsTrue(pageRequest);
        List<ResourceOffer> resourceOffers = resourceOffersPage.getContent();

        assertNotNull(resourceOffers);
        assertTrue(resourceOffers.contains(resourceOffer));
        assertTrue(resourceOffers.contains(resourceOffer2));

        resourceOffer2.setActive(false);
        resourceOffers = resourceOfferRepository.findByActiveIsTrue(pageRequest).getContent();

        assertNotNull(resourceOffers);
        assertTrue(resourceOffers.contains(resourceOffer));
        assertFalse(resourceOffers.contains(resourceOffer2));
    }

    @Test
    public void testFindByOrganizationIdAndActiveIsTrue() {

        List<ResourceOffer> resourceOffers = resourceOfferRepository.findByOrganizationIdAndActiveIsTrue(organization.getId());

        assertNotNull(resourceOffers);
        assertTrue(resourceOffers.contains(resourceOffer));
        assertTrue(resourceOffers.contains(resourceOffer2));

        resourceOffer2.setOrganization(organization2);
        resourceOfferRepository.save(resourceOffer2);
        resourceOffers = resourceOfferRepository.findByOrganizationIdAndActiveIsTrue(organization.getId());

        assertNotNull(resourceOffers);
        assertTrue(resourceOffers.contains(resourceOffer));
        assertFalse(resourceOffers.contains(resourceOffer2));
    }

}
