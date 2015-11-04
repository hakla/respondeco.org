package org.respondeco.respondeco;

import org.junit.Before;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.PersistentTokenRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Created by clemens on 18/09/15.
 */
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@TransactionConfiguration(defaultRollback = true)
@Transactional
public abstract class RepositoryLayerTest extends AbstractBaseTest {

    @Inject public PersistentTokenRepository persistentTokenRepository;
    @Inject public UserRepository userRepository;
    @Inject public OrganizationRepository organizationRepository;
    @Inject public ProjectRepository projectRepository;

    @Before
    public void setupDatabase() {
        saveUsers();
        saveOrganizations();
        saveProjects();
    }

    private void saveUsers() {
        List<User> users = Arrays.asList(
            model.USER_SAVED_MINIMAL,
            model.USER1_OWNS_ORG1_MANAGES_P1,
            model.USER2_OWNS_ORG2_MANAGES_P2
        );
        for(User user : users) {
            Organization organization = user.getOrganization();
            user.setId(null);
            user.setOrganization(null);
            userRepository.save(user);
            assertNotNull(user.getId());
            user.setOrganization(organization);
        }
    }

    private void saveOrganizations() {
        List<Organization> organizations = Arrays.asList(
            model.ORGANIZATION_SAVED_MINIMAL,
            model.ORGANIZATION1_GOVERNS_P1,
            model.ORGANIZATION2_GOVERNS_P2
        );
        for(Organization organization : organizations) {
            organization.setId(null);
            organizationRepository.save(organization);
            assertNotNull(organization.getId());
        }
    }

    private void saveProjects() {
        List<Project> projects = Arrays.asList(
            model.PROJECT1,
            model.PROJECT2
        );
        for(Project project : projects) {
            project.setId(null);
            projectRepository.save(project);
            assertNotNull(project.getId());
        }
    }

}


