package org.respondeco.respondeco.repository;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.User;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestExecutionListeners;
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
public class ProjectRepositoryTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private OrganizationRepository organizationRepository;

    @Inject
    private ProjectRepository projectRepository;

    private User orgAdmin;
    private Organization organization;
    private Project project;

    @Before
    public void setup() {
        projectRepository.deleteAll();
        organizationRepository.deleteAll();
        userRepository.deleteAll();

        orgAdmin = new User();
        orgAdmin.setLogin("orgAdmin");
        orgAdmin.setGender(Gender.UNSPECIFIED);
        userRepository.save(orgAdmin);

        organization = new Organization();
        organization.setName("testorg");
        organization.setOwner(orgAdmin.getId());
        organizationRepository.save(organization);

        project = new Project();
        project.setName("testproject");
        project.setPurpose("testpurpose");
        project.setConcrete(false);
        project.setOrganizationId(organization.getId());
        project.setManagerId(orgAdmin.getId());
    }

    @Test
    public void testSave_shouldSaveCorrectly() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(3);
        project.setName("testproject");
        project.setPurpose("testpurpose");
        project.setConcrete(true);
        project.setStartDate(startDate);
        project.setEndDate(endDate);

        projectRepository.save(project);
        Project savedProject = projectRepository.findOne(project.getId());

        assertEquals(savedProject.getName(), "testproject");
        assertEquals(savedProject.getPurpose(), "testpurpose");
        assertEquals(savedProject.isConcrete(), true);
        assertEquals(savedProject.getStartDate(), startDate);
        assertEquals(savedProject.getEndDate(), endDate);
    }

    @Test
    public void testFindByActiveIsTrue_shouldReturnOnlyActive() throws Exception {
        Project project2 = new Project();
        project2.setName("project2");
        project2.setPurpose("testpurpose2");
        project2.setConcrete(false);
        project2.setOrganizationId(organization.getId());
        project2.setManagerId(orgAdmin.getId());

        projectRepository.save(project);
        projectRepository.save(project2);

        List<Project> projects = projectRepository.findByActiveIsTrue();
        assertTrue(projects.size() == 2);
        assertTrue(projects.contains(project));
        assertTrue(projects.contains(project2));

        project.setActive(false);
        projectRepository.save(project);

        projects = projectRepository.findByActiveIsTrue();
        assertTrue(projects.size() == 1);
        assertFalse(projects.contains(project));
        assertTrue(projects.contains(project2));
    }

    @Test
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
}
