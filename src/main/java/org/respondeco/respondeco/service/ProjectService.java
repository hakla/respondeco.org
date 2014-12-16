package org.respondeco.respondeco.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementRequestDTO;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.respondeco.respondeco.web.rest.util.RestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clemens Puehringer on 15/11/14.
 *
 * Service class for Projects, contains methods for creating, updating, searching and deleting Projects
 */

@Service
public class ProjectService {

    private final Logger log = LoggerFactory.getLogger(ProjectService.class);

    private ProjectRepository projectRepository;
    private UserService userService;
    private UserRepository userRepository;
    private PropertyTagService propertyTagService;
    private ResourceService resourceService;
    private ImageRepository imageRepository;
    private ResourceMatchRepository resourceMatchRepository;

    private RestUtil restUtil;

    @Inject
    public ProjectService(ProjectRepository projectRepository,
                          UserService userService, UserRepository userRepository,
                          PropertyTagService propertyTagService,
                          ResourceService resourceService,
                          ImageRepository imageRepository,
                          ResourceMatchRepository resourceMatchRepository) {
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.propertyTagService = propertyTagService;
        this.resourceService = resourceService;
        this.imageRepository = imageRepository;
        this.resourceMatchRepository = resourceMatchRepository;
        this.restUtil = new RestUtil();
    }

    /**
     * creates a new project based on the given values
     * @param name the name of the project
     * @param purpose the purpose of the project
     * @param isConcrete indicates if the project has a startDate on which it will really begin
     * @param startDate the date on which the project will start
     * @param propertyTags a list of tags for the project
     * @param resourceRequirements a list of requirements for the project
     * @param imageId the id of the image associated with the project
     * @return the newly created project
     * @throws OperationForbiddenException if the current user does not belong to an organization
     * @throws ResourceException if a resource requirement could not be created,
     * {@see ResourceService#createRequirement(String,BigDecimal,String,Project,Boolean,List<String>)}
     * @throws IllegalValueException if the given parameters are inconsistent, e.g. the project is concrete, but start
     * or end date are not given
     */
    public Project create(String name, String purpose, boolean isConcrete, LocalDate startDate,
                          List<String> propertyTags,
                          List<ResourceRequirementRequestDTO> resourceRequirements, Long imageId)
        throws OperationForbiddenException, ResourceException, IllegalValueException {
        sanityCheckDate(isConcrete, startDate);
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getOrganization() == null) {
            throw new OperationForbiddenException("Current user does not belong to an Organization");
        }

        Project newProject = new Project();
        newProject.setManager(currentUser);
        newProject.setOrganization(currentUser.getOrganization());
        newProject.setName(name);
        newProject.setPurpose(purpose);
        newProject.setConcrete(isConcrete);
        newProject.setStartDate(startDate);

        List<PropertyTag> tags = propertyTagService.getOrCreateTags(propertyTags);
        if(imageId != null) {
            newProject.setProjectLogo(imageRepository.findOne(imageId));
        }
        newProject.setPropertyTags(tags);

        projectRepository.save(newProject);

        List<ResourceRequirement> requirements = new ArrayList<>();
        if(resourceRequirements != null) {
            for(ResourceRequirementRequestDTO req : resourceRequirements) {
                requirements.add(resourceService.createRequirement(req.getName(), req.getAmount(), req.getDescription(), newProject,
                    req.getIsEssential(), req.getResourceTags()));
            }
        }
        newProject.setResourceRequirements(requirements);
        projectRepository.save(newProject);

        return newProject;
    }

    /**
     *
     * @param id the id of the project to alter
     * @param name the possibly updated name of the project
     * @param purpose the possibly updated purpose of the project
     * @param isConcrete indicates if the project has a startDate on which it will really begin
     * @param startDate the date on which the project will start
     * @param imageId the id of the image associated with the project
     * @param propertyTags a list of tags for the project
     * @param resourceRequirements a list of requirements for the project
     * @return the updated project
     * @throws OperationForbiddenException if the user's organization and the project's organization do not match or
     * the user is neither the manager of the project nor the owner of the project's organization
     * @throws ResourceException if a resource requirement could not be created or updated, {@see ResourceService}
     * @throws IllegalValueException if no id was given or the project with the given id could not be found,
     * or if the given parameters are inconsistent, e.g. the project is concrete, but start or end date are not given
     */
    public Project update(Long id, String name, String purpose, boolean isConcrete, LocalDate startDate,
                        Long imageId, List<String> propertyTags,
                        List<ResourceRequirementRequestDTO> resourceRequirements)
        throws OperationForbiddenException, ResourceException, IllegalValueException {

        sanityCheckDate(isConcrete, startDate);
        if(id == null) {
            throw new IllegalValueException("project.error.idnull", "Project id must not be null");
        }
        User currentUser = userService.getUserWithAuthorities();
        Organization organization = currentUser.getOrganization();
        //check if user does belong to an organization
        if(organization == null) {
            throw new NoSuchOrganizationException("current user does not belong to an organization");
        }
        Project project = projectRepository.findOne(id);
        if(project == null) {
            //no project found
            throw new NoSuchProjectException(id);
        }
        if(project.getOrganization().equals(organization) == false) {
            //project's org does not match user's org
            throw new OperationForbiddenException("Project " + project +
                    " is not a project from organization " + organization);
        }
        if(currentUser.equals(project.getManager()) == false) {
            if(currentUser.equals(organization.getOwner()) == false) {
                //user is neither project manager nor organization owner
                throw new OperationForbiddenException("Current user does have permission to alter project " + project);
            }
        }

        project.setName(name);
        project.setPurpose(purpose);
        project.setConcrete(isConcrete);
        project.setStartDate(startDate);
        List<PropertyTag> tags = propertyTagService.getOrCreateTags(propertyTags);
        project.setPropertyTags(tags);
        if(imageId != null) {
            project.setProjectLogo(imageRepository.findOne(imageId));
        }

        log.debug("updating requirements: {}", resourceRequirements);

        List<ResourceRequirement> requirements = new ArrayList<>();
        if(resourceRequirements != null) {
            for(ResourceRequirementRequestDTO req : resourceRequirements) {
                //if requirement is new: create, else: update
                if(req.getId() == null) {
                    requirements.add(resourceService.createRequirement(req.getName(), req.getAmount(),
                        req.getDescription(), project, req.getIsEssential(), req.getResourceTags()));
                } else {
                    requirements.add(resourceService.updateRequirement(req.getId(), req.getName(), req.getAmount(),
                        req.getDescription(), project, req.getIsEssential(), req.getResourceTags()));
                }
            }
        }

        log.debug("requirements updated");
        project.setResourceRequirements(requirements);
        projectRepository.save(project);
        return project;
    }

    /**
     * Set a new manager for the given project
     * @param id the id of the project of which to set the manager
     * @param newManagerId the user id of the new manager
     * @return the altered project with the manager set to the new manager
     * @throws IllegalValueException if the new manager does not belong to the project's organization
     * @throws OperationForbiddenException if the current user is neither the manager of the project nor the owner
     * of the project's organization
     */
    public Project setManager(Long id, Long newManagerId) throws IllegalValueException,
        OperationForbiddenException {
        User newManager = userRepository.findByIdAndActiveIsTrue(newManagerId);
        if(newManager == null) {
            throw new NoSuchUserException("no such user: " + id);
        }
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new NoSuchProjectException(id);
        }
        User currentUser = userService.getUserWithAuthorities();
        //check if user has authority to delete
        if(currentUser.equals(project.getManager()) == false) {
            if(currentUser.equals(project.getOrganization().getOwner()) == false) {
                throw new OperationForbiddenException("current user has no authority to " +
                        "change the project manager of project " + id);
            }
        }

        if(project.getOrganization().equals(newManager.getOrganization()) == false) {
            throw new IllegalValueException("project.error.notvalidmanager",
                "new manager does not belong to organization: " + project.getOrganization());
        }
        project.setManager(newManager);
        return projectRepository.save(project);
    }

    /**
     * delete a project
     * @param id the id of the project to delete
     * @return the deleted project
     * @throws OperationForbiddenException if the current user is neither the manager of the project nor the owner
     * of the project's organization
     * @throws NoSuchProjectException if the project with the given id could not be found
     */
    public Project delete(Long id) throws OperationForbiddenException, NoSuchProjectException {
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new NoSuchProjectException(id);
        }
        User currentUser = userService.getUserWithAuthorities();
        User manager = project.getManager();
        log.debug("current user: {}, manager: {}", currentUser, manager);
        //check if user has authority to delete
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

    /**
     * get a project by its id
     * @param id the id of the project
     * @return the project with the given id
     */
    public Project findProjectById(Long id) {
        return projectRepository.findByIdAndActiveIsTrue(id);
    }

    /**
     * Find projects by name and tags
     * @param name the name to search for
     * @param tagsString the tags to search for
     * @param restParams other parameters for paging and sorting
     * @return a list of Projects which match the given name and are associated with the given tags, paged and sorted
     * with the given RestParameters
     */
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
            //add wildcards to the name
            name = "%" + name + "%";
            result = projectRepository.findByNameAndTags(name, tags, pageRequest);
        }

        return result;
    }

    /**
     * Find projects by organization, name and tags
     * @param orgId projects will be searched for based on their organization
     * @param name the name to search for
     * @param tagsString the tags to search for
     * @param restParams other parameters for paging and sorting
     * @return a list of Projects which match the given name and are associated with the given tags, paged and sorted
     * with the given RestParameters
     */
    public List<Project> findProjectsFromOrganization(Long orgId, String name, String tagsString,
                                                                 RestParameters restParams) {
        List<String> tags = restUtil.splitCommaSeparated(tagsString);

        PageRequest pageRequest = null;
        if(restParams != null) {
            pageRequest = restParams.buildPageRequest();
        }

        List<Project> result;
        if((name == null || name.length() == 0) && tags.size() == 0) {
            if(orgId != null) {
                result = projectRepository.findByOrganization(orgId, pageRequest);
            } else {
                result = projectRepository.findByActiveIsTrue(pageRequest);
            }
        } else if(name == null || name.length() == 0) {
            result = projectRepository.findByOrganizationAndTags(orgId, tags, pageRequest);
        } else {
            //add wildcards to the name
            name = "%" + name + "%";
            result = projectRepository.findByOrganizationAndNameAndTags(orgId, name, tags, pageRequest);
        }

        return result;

    }

    /**
     * checks if the given values make sense
     * @param isConcrete indicator if start and end date should be checked
     * @param startDate the start date of the project, must not be null or after end date (if concrete)
     * @throws IllegalValueException if the given values make no sense, see parameter descriptions
     */
    private void sanityCheckDate(boolean isConcrete, LocalDate startDate) throws IllegalValueException {
        if(isConcrete == true) {
            if(startDate == null) {
                throw new IllegalValueException("project.error.startdate.null",
                        "start date cannot be null if project is concrete");
            }
            if(startDate.isBefore(LocalDate.now())) {
                throw new IllegalValueException("project.error.enddate.beforenow",
                        "end date cannot be before before now");
            }
        }
     }

    /**
     * Check if the authenticated user is allowed to edit a project
     *
     * @param projectId Id of the project to check
     * @return If the user is allowed to edit the project
     */
    public boolean isEditable(Long projectId) {
        boolean isAllowed = false;

        // Get the project that should be checked
        Project project = findProjectById(projectId);
        User user = userService.getUserWithAuthorities();

        if (user == null) {
            // No authenticated user
            throw new NullPointerException();
        }

        Long managerId = project.getManager().getId();
        Long userId = user.getId();

        // user is allowed to edit the project if he is the manager or he is the owner of the organization that owns the project
        if (managerId.equals(userId) || project.getOrganization().getOwner().getId().equals(userId)) {
            isAllowed = true;
        }

        return isAllowed;
    }

    /**
     * Checks repeatedly (every day at 1 am) all projects that should start today if they should start
     * A project should start if:
     *  * The current date is equal to the startDate of the project
     *  * All needed resources were donated to the project
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void checkProjectsToStart() {
        LocalDate date = LocalDate.now();

        List<Project> projects = projectRepository.findByStartDate(date);

        // iterate over all projects that should start today
        projects.forEach(project -> {
            // Get all requirements for the project
            List<ResourceRequirement> requirements = project.getResourceRequirements();
            List<ResourceRequirement> essentialRequirements = new ArrayList<ResourceRequirement>();

            boolean allNeededDonated = true;

            for (ResourceRequirement requirement : requirements) {
                // check if a essential requirement is not saturated
                if (requirement.getIsEssential() && requirement.getAmount().compareTo(new BigDecimal(0)) > 0) {
                    essentialRequirements.add(requirement);
                    allNeededDonated = false;
                }
            }

            // Set the projects successful state and save
            project.setSuccessful(allNeededDonated);
            projectRepository.save(project);

            //
            if (allNeededDonated == false) {
                for (ResourceRequirement requirement : essentialRequirements) {
                    requirement.getResourceMatches().forEach(match -> {
                        ResourceOffer resourceOffer = match.getResourceOffer();

                        if (resourceOffer != null) {
                            resourceOffer.setAmount(resourceOffer.getAmount().add(match.getAmount()));
                        }

                        resourceMatchRepository.delete(match);
                    });
                }
            }
        });
    }

}
