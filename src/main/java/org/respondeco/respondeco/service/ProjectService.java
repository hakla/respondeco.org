package org.respondeco.respondeco.service;

import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.PropertyTagRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.web.rest.dto.ProjectResponseDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.respondeco.respondeco.web.rest.util.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
    private PropertyTagService propertyTagService;
    private ImageRepository imageRepository;

    private RestUtil restUtil;

    @Inject
    public ProjectService(ProjectRepository projectRepository,
                          UserService userService, UserRepository userRepository,
                          OrganizationRepository organizationRepository,
                          PropertyTagService propertyTagService,
                          ImageRepository imageRepository) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.propertyTagService = propertyTagService;
        this.imageRepository = imageRepository;
        this.restUtil = new RestUtil();
    }

    public Project create(String name, String purpose, boolean isConcrete, LocalDate startDate,
                          LocalDate endDate, List<String> propertyTags,
                          List<ResourceRequirementDTO> resourceRequirements, Long imageId) throws OperationForbiddenException {
        sanityCheckDate(isConcrete, startDate, endDate);
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getOrgId() == null) {
            throw new OperationForbiddenException("Current user does not belong to an Organization");
        }
        Organization organization = organizationRepository.findOne(currentUser.getOrgId());
        if(organization == null) {
            throw new NoSuchOrganizationException(currentUser.getOrgId());
        }

        Project newProject = new Project();
        newProject.setManager(currentUser);
        newProject.setOrganization(organization);
        newProject.setName(name);
        newProject.setPurpose(purpose);
        newProject.setConcrete(isConcrete);
        newProject.setStartDate(startDate);
        newProject.setEndDate(endDate);
        List<PropertyTag> tags = propertyTagService.getOrCreateTags(propertyTags);
        if(imageId != null) {
            newProject.setProjectLogo(imageRepository.findOne(imageId));
        }
        newProject.setPropertyTags(tags);

        projectRepository.save(newProject);
        return newProject;
    }

    public Project update(Long id, String name, String purpose, boolean isConcrete, LocalDate startDate,
                        LocalDate endDate, Long imageId, List<String> propertyTags,
                        List<ResourceRequirementDTO> resourceRequirements) throws OperationForbiddenException {

        sanityCheckDate(isConcrete, startDate, endDate);
        if(id == null) {
            throw new IllegalValueException("error.project.idnull", "Project id must not be null");
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getOrgId() == null) {
            throw new OperationForbiddenException("Current user does not belong to an Organization");
        }
        Organization organization = organizationRepository.findOne(currentUser.getOrgId());
        if(organization == null) {
            throw new NoSuchOrganizationException(currentUser.getOrgId());
        }
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new NoSuchProjectException(id);
        }
        if(project.getOrganization().equals(organization) == false) {
            throw new OperationForbiddenException("Project " + project +
                    " is not a project from organization " + organization);
        }
        if(currentUser.equals(project.getManager()) == false) {
            if(currentUser.equals(organization.getOwner()) == false) {
                throw new OperationForbiddenException("Current user does have permission to alter project " + project);
            }
        }

        project.setName(name);
        project.setPurpose(purpose);
        project.setConcrete(isConcrete);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        List<PropertyTag> tags = propertyTagService.getOrCreateTags(propertyTags);
        project.setPropertyTags(tags);
        project.setProjectLogo(imageRepository.findOne(imageId));
        projectRepository.save(project);
        return project;
    }

    public Project setManager(Long id, String newManagerLogin) throws NoSuchUserException,
            OperationForbiddenException, NoSuchProjectException {
        User newManager = userRepository.findByLogin(newManagerLogin);
        if(newManager == null) {
            throw new NoSuchUserException("no such user: " + id);
        }
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new NoSuchProjectException(id);
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.equals(project.getManager()) == false) {
            if(currentUser.equals(project.getOrganization().getOwner()) == false) {
                throw new OperationForbiddenException("current user has no authority to " +
                        "change the project manager of project " + id);
            }
        }
        if(project.getOrganization().getId().equals(newManager.getOrgId()) == false) {
            throw new IllegalValueException("project.error.notvalidmanager",
                    "new manager does not belong to organization: " + project.getOrganization());
        }
        project.setManager(newManager);
        return projectRepository.save(project);
    }

    public Project delete(Long id) throws OperationForbiddenException {
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new NoSuchProjectException(id);
        }
        User currentUser = userService.getUserWithAuthorities();
        User manager = project.getManager();
        log.debug("current user: {}, manager: {}", currentUser, manager);
        if(currentUser.equals(project.getManager()) == false) {
            if(currentUser.equals(project.getOrganization().getOwner()) == false) {
                throw new OperationForbiddenException("current user has no authority to " +
                        "delete project " + id);
            }
        }
        project.setActive(false);
        projectRepository.save(project);
        return project;
    }

    public Project findProjectById(Long id) {
        return projectRepository.findByIdAndActiveIsTrue(id);
    }

    public List<Project> findProjects(String name, String tagsString, RestParameters restParams) {
        List<String> tags = restUtil.splitCommaSeparated(tagsString);

        PageRequest pageRequest = null;
        if(restParams != null) {
            pageRequest = restParams.buildPageRequest();
        }

        List<Project> result;
        if((name == null || name.length() == 0) && tags.size() == 0) {
            result = projectRepository.findByActiveIsTrue(pageRequest);
        } else if(name == null || name.length() == 0) {
            result = projectRepository.findByTags(tags, pageRequest);
        } else {
            result = projectRepository.findByNameAndTags(name, tags, pageRequest);
        }

        return result;
    }

    public List<Project> findProjectsFromOrganization(Long orgId, String name, String tagsString,
                                                                 RestParameters restParams) {
        List<String> tags = restUtil.splitCommaSeparated(tagsString);

        PageRequest pageRequest = null;
        if(restParams != null) {
            pageRequest = restParams.buildPageRequest();
        }

        List<Project> result;
        if((name == null || name.length() == 0) && tags.size() == 0) {
            result = projectRepository.findByActiveIsTrue(pageRequest);
        } else if(name == null || name.length() == 0) {
            result = projectRepository.findByOrganizationAndTags(orgId, tags, pageRequest);
        } else {
            result = projectRepository.findByOrganizationAndNameAndTags(orgId, name, tags, pageRequest);
        }

        return result;

    }

    private void sanityCheckDate(boolean isConcrete, LocalDate startDate, LocalDate endDate) {
        if(isConcrete == true) {
            if(startDate == null) {
                throw new IllegalValueException("project.error.startdate.null",
                        "start date cannot be null if project is concrete");
            }
            if(endDate == null) {
                throw new IllegalValueException("project.error.enddate.null",
                        "end date cannot be null if project is concrete");
            }
            if(endDate.isBefore(LocalDate.now())) {
                throw new IllegalValueException("project.error.enddate.beforenow",
                        "end date cannot be before before now");
            }
            if(endDate.isBefore(startDate)) {
                throw new IllegalValueException("project.error.enddate.beforestart",
                        "end date cannot be before start date");
            }
        }
     }

}
