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
public class ResourceRequirementRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private UserRepository userRepository;

    @Inject
    private ProjectRepository projectRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private ResourceRequirementRepository resourceRequirementRepository;

    @Inject
    private PostingFeedRepository postingFeedRepository;

    private User orgAdmin;
    private Organization organization;
    private Project project;
    private Project project2;
    private ResourceRequirement resourceRequirement;
    private ResourceRequirement resourceRequirement2;
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
        project.setOrganization(organization);
        project.setManager(orgAdmin);
        project.setPostingFeed(postingFeed2);
        projectRepository.save(project);

        resourceRequirement = new ResourceRequirement();
        resourceRequirement.setAmount(new BigDecimal(1));
        resourceRequirement.setOriginalAmount(new BigDecimal(1));
        resourceRequirement.setName("testRequirement");
        resourceRequirement.setDescription("testDescription");
        resourceRequirement.setProject(project);
        resourceRequirementRepository.save(resourceRequirement);

        resourceRequirement2 = new ResourceRequirement();
        resourceRequirement2.setAmount(new BigDecimal(2));
        resourceRequirement2.setOriginalAmount(new BigDecimal(2));
        resourceRequirement2.setName("testRequirement2");
        resourceRequirement2.setDescription("testDescription2");
        resourceRequirement2.setProject(project);
        resourceRequirementRepository.save(resourceRequirement2);
    }

    @Test
    public void testFindByNameAndProject() {

        List<ResourceRequirement> resourceRequirements = resourceRequirementRepository.findByNameAndProject(resourceRequirement.getName(),project);

        assertNotNull(resourceRequirements);
        assertTrue(resourceRequirements.contains(resourceRequirement));
        assertFalse(resourceRequirements.contains(resourceRequirement2));
    }

    @Test
    public void testFindByProjectId() {

        List<ResourceRequirement> resourceRequirements = resourceRequirementRepository.findByProjectId(project.getId());
        assertNotNull(resourceRequirements);
        assertTrue(resourceRequirements.contains(resourceRequirement));
        assertTrue(resourceRequirements.contains(resourceRequirement2));

        resourceRequirement2.setProject(project2);
        resourceRequirements = resourceRequirementRepository.findByProjectId(project.getId());

        assertNotNull(resourceRequirements);
        assertTrue(resourceRequirements.contains(resourceRequirement));
        assertFalse(resourceRequirements.contains(resourceRequirements));
    }
}
