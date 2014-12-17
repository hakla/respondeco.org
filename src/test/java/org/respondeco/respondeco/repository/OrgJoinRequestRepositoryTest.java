package org.respondeco.respondeco.repository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
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
public class OrgJoinRequestRepositoryTest extends AbstractTransactionalJUnit4SpringContextTests {

    @Inject
    private UserRepository userRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private OrgJoinRequestRepository orgJoinRequestRepository;

    private User orgAdmin;
    private User defaultUser;
    private Organization organization;
    private OrgJoinRequest orgJoinRequest;

    @Before
    public void setup() {

        orgAdmin = new User();
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setGender(Gender.UNSPECIFIED);
        userRepository.save(orgAdmin);

        defaultUser = new User();
        defaultUser.setLogin("defaultUser");
        defaultUser.setGender(Gender.UNSPECIFIED);
        userRepository.save(defaultUser);

        organization = new Organization();
        organization.setName("testorg");
        organization.setOwner(orgAdmin);
        organizationRepository.save(organization);

        orgAdmin.setOrganization(organization);
        userRepository.save(orgAdmin);

        orgJoinRequest = new OrgJoinRequest();
        orgJoinRequest.setUser(defaultUser);
        orgJoinRequest.setOrganization(organization);
        orgJoinRequestRepository.save(orgJoinRequest);
    }

    @Test
    public void testFindByOrganizationAndActiveIsTrue() throws Exception {

        List<OrgJoinRequest> orgJoinRequests = orgJoinRequestRepository.findByOrganizationAndActiveIsTrue(organization);
        assertTrue(orgJoinRequests.contains(orgJoinRequest));

        orgJoinRequest.setActive(false);
        orgJoinRequestRepository.save(orgJoinRequest);

        orgJoinRequests = orgJoinRequestRepository.findByOrganizationAndActiveIsTrue(organization);
        assertFalse(orgJoinRequests.contains(orgJoinRequest));
    }

    @Test
    public void testFindByUserAndActiveIsTrue() throws Exception {

        List<OrgJoinRequest> orgJoinRequests = orgJoinRequestRepository.findByUserAndActiveIsTrue(defaultUser);
        assertTrue(orgJoinRequests.contains(orgJoinRequest));

    }

    @Test
    public void testFindByIdAndActiveIsTrue_shouldNotReturnInactiveOrgJoinRequest() throws Exception {

        OrgJoinRequest savedOrgJoinRequest = orgJoinRequestRepository.findByIdAndActiveIsTrue(orgJoinRequest.getId());
        assertNotNull(savedOrgJoinRequest);

        orgJoinRequest.setActive(false);
        orgJoinRequestRepository.save(orgJoinRequest);

        savedOrgJoinRequest = orgJoinRequestRepository.findByIdAndActiveIsTrue(orgJoinRequest.getId());
        assertNull(savedOrgJoinRequest);

    }

    @Test
    public void testFindByUserAndOrganizationAndActiveIsTrue() throws Exception {

        OrgJoinRequest savedOrgJoinRequest = orgJoinRequestRepository.findByUserAndOrganizationAndActiveIsTrue(defaultUser,organization);
        assertNotNull(savedOrgJoinRequest);

        orgJoinRequest.setActive(false);
        orgJoinRequestRepository.save(orgJoinRequest);

        savedOrgJoinRequest = orgJoinRequestRepository.findByUserAndOrganizationAndActiveIsTrue(defaultUser,organization);
        assertNull(savedOrgJoinRequest);

    }

}
