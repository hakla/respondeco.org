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
public class OrganizationRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private UserRepository userRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private PostingFeedRepository postingFeedRepository;

    private User orgAdmin;
    private Organization organization;
    private Organization organization2;
    private PostingFeed postingFeed;

    @Before
    public void setup() {

        orgAdmin = new User();
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setGender(Gender.UNSPECIFIED);
        userRepository.save(orgAdmin);

        postingFeed = new PostingFeed();
        postingFeedRepository.save(postingFeed);

        organization = new Organization();
        organization.setName("testorg");
        organization.setOwner(orgAdmin);
        organization.setPostingFeed(postingFeed);
        organizationRepository.save(organization);
    }

    @Test
    public void testFindByActiveIsTrue_shouldReturnOnlyActive() throws Exception {
        organization2 = new Organization();
        organization2.setName("testorg");
        organization2.setOwner(orgAdmin);
        organization2.setPostingFeed(postingFeed);
        organizationRepository.save(organization2);

        List<Organization> organizations = organizationRepository.findByActiveIsTrue();
        assertTrue(organizations.contains(organization));
        assertTrue(organizations.contains(organization2));

        organization.setActive(false);
        organizationRepository.save(organization);

        organizations = organizationRepository.findByActiveIsTrue();
        assertFalse(organizations.contains(organization));
        assertTrue(organizations.contains(organization2));
    }

    @Test
    public void testFindByIdAndActiveIsTrue_shouldReturnActiveOrganization() throws Exception {
        organizationRepository.save(organization);

        Organization savedOrganization = organizationRepository.findByIdAndActiveIsTrue(organization.getId());
        assertTrue(savedOrganization.equals(organization));

    }

    @Test
    public void testFindByIdAndActiveIsTrue_shouldNotReturnInactiveOrganization() throws Exception {
        organization.setActive(false);
        organizationRepository.save(organization);

        Organization savedOrganization = organizationRepository.findByIdAndActiveIsTrue(organization.getId());
        assertNull(savedOrganization);
    }

    @Test
    public void testFindByName_shouldReturnOrganizationByName() throws Exception {
        Organization savedOrganization = organizationRepository.findByName(organization.getName());

        assertTrue(savedOrganization.getName().equals(organization.getName()));
    }

    @Test
    public void testFindByOwner_shouldReturnOrganizationByOwner() throws Exception {
        Organization savedOrganization = organizationRepository.findByOwner(organization.getOwner());

        assertTrue(savedOrganization.getOwner().equals(orgAdmin));
    }


}
