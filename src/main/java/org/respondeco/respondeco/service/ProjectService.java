package org.respondeco.respondeco.service;

import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ProjectLogo;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by clemens on 15/11/14.
 */

@Service
@Transactional
public class ProjectService {

    private final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private ProjectRepository projectRepository;
    private UserService userService;
    private UserRepository userRepository;
    private OrganizationRepository organizationRepository;

    @Inject
    public ProjectService(ProjectRepository projectRepository,
                          UserService userService, UserRepository userRepository,
                          OrganizationRepository organizationRepository) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    public Project save(Long id, String name, String purpose, boolean isConcrete, LocalDate startDate,
                        LocalDate endDate, byte[] logo) {
        sanityCheck(isConcrete, startDate, endDate);
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getOrgId() == null) {
            throw new IllegalArgumentException("Current user does not belong to an Organization");
        }
        Organization organization = organizationRepository.findOne(currentUser.getOrgId());
        if(organization == null) {
            throw new IllegalArgumentException("Organization does not exist: " + currentUser.getOrgId());
        }
        Project newProject;
        if(id != null) {
            newProject = projectRepository.findOne(id);
            if(newProject == null) {
                throw new IllegalArgumentException("no such project: " + id);
            }
            if(currentUser.getId() != newProject.getManagerId()) {
                throw new IllegalArgumentException("current user has no authority to " +
                        "change the project manager of project " + id);
            }
        } else {
            newProject = new Project();
            newProject.setManagerId(currentUser.getId());
            newProject.setOrganizationId(organization.getId());
        }
        newProject.setName(name);
        newProject.setPurpose(purpose);
        newProject.setConcrete(isConcrete);
        newProject.setStartDate(startDate);
        newProject.setEndDate(endDate);
        if(logo != null) {
            ProjectLogo projectLogo = new ProjectLogo();
            projectLogo.setData(logo);
            newProject.setProjectLogo(projectLogo);
        }
        projectRepository.save(newProject);
        return newProject;
    }

    public Project setManager(Long id, String newManagerLogin) throws NoSuchUserException {
        User newManager = userRepository.findByLogin(newManagerLogin);
        if(newManager == null) {
            throw new NoSuchUserException("no such user: " + id);
        }
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new IllegalArgumentException("no such project: " + id);
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getId() != project.getManagerId()) {
            throw new IllegalArgumentException("current user has no authority to " +
                    "change the project manager of project " + id);
        }
        if(project.getOrganizationId() != newManager.getOrgId()) {
            throw new IllegalArgumentException("new manager does not belong to organization: " +
                    project.getOrganizationId());
        }
        project.setManagerId(newManager.getId());
        projectRepository.save(project);
        return project;
    }

    public void delete(Long id) {
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new IllegalArgumentException("no such project: " + id);
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getId() != project.getManagerId()) {
            Organization projectOrganization = organizationRepository.findOne(project.getOrganizationId());
            if(currentUser.getId() != projectOrganization.getOwner()) {
                throw new IllegalArgumentException("current user has no authority to " +
                        "delete project " + id);
            }
        }
        project.setActive(false);
        projectRepository.save(project);
    }

    private void sanityCheck(boolean isConcrete, LocalDate startDate, LocalDate endDate) {
        if(isConcrete == true) {
            if(startDate == null) {
                throw new IllegalArgumentException("start date cannot be null if project is concrete");
            }
            if(endDate == null) {
                throw new IllegalArgumentException("end date cannot be null if project is concrete");
            }
            if(endDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("end date cannot be before before now");
            }
            if(endDate.isBefore(startDate)) {
                throw new IllegalArgumentException("end date cannot be before start date");
            }
        }
     }


}
