package org.respondeco.respondeco.service;

import com.mysema.query.types.ExpressionUtils;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.domain.QProject;
import org.respondeco.respondeco.repository.PostingFeedRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.ResourceOfferRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.service.util.EntityAssert;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Clemens Puehringer on 15/11/14.
 *
 * Service class for Projects, contains methods for creating, updating, searching and deleting Projects
 */

@Service
public class ProjectService {

    private final Logger log = LoggerFactory.getLogger(ProjectService.class);

    public static final ServiceException.ErrorPrefix ERROR_PREFIX       = new ServiceException.ErrorPrefix("project");
    public static final String ERROR_NAME_CHANGE_AFTER_VALIDATION       = "name_change_after_validation";
    public static final String ERROR_NOT_ADMIN                          = "not_admin";
    public static final String ERROR_NOT_OWNER                          = "not_owner";
    public static final String ERROR_NOT_PART_OF_ORGANIZATION           = "not_part_of_organization";
    public static final String ERROR_NOT_VERIFIED                       = "not_verified";
    public static final String ERROR_NPO_CHANGE_AFTER_VALIDATION        = "npo_change_after_validation";
    public static final String ERROR_NO_AUTHORITY                       = "no_authority";
    public static final String ERROR_NO_ORGANIZATION                    = "no_organization";
    public static final String ERROR_NO_OWNER                           = "no_owner";

    private PostingFeedRepository postingFeedRepository;
    private ProjectRepository projectRepository;
    private ResourceOfferRepository resourceOfferRepository;
    private UserRepository userRepository;

    private PropertyTagService propertyTagService;
    private ResourceService resourceService;
    private UserService userService;

    @Inject
    public ProjectService(PostingFeedRepository postingFeedRepository,
                          ProjectRepository projectRepository,
                          ResourceOfferRepository resourceOfferRepository,
                          UserRepository userRepository,

                          PropertyTagService propertyTagService,
                          ResourceService resourceService,
                          UserService userService) {
        this.postingFeedRepository = postingFeedRepository;
        this.projectRepository = projectRepository;
        this.resourceOfferRepository = resourceOfferRepository;
        this.userRepository = userRepository;

        this.propertyTagService = propertyTagService;
        this.resourceService = resourceService;
        this.userService = userService;
    }

    /**
     * creates a new project based on the given values
     * @return the newly created project
     * @throws OperationForbiddenException if the current user does not belong to an organization
     * @throws org.respondeco.respondeco.service.exception.ResourceNotFoundException if a resource requirement could not be created,
     * {@see ResourceService#createRequirement(String,BigDecimal,String,Project,Boolean,List<String>)}
     * @throws IllegalValueException if the given parameters are inconsistent, e.g. the project is concrete, but start
     * or end date are not given
     */
    public Project create(Project project)
        throws IllegalValueException {
        EntityAssert.Project.isNew(project, ERROR_PREFIX);
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser.getOrganization() == null) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_ORGANIZATION),
                "Current user (%1) does not belong to an organization",
                Arrays.asList("user"), Arrays.asList(currentUser.getLogin()));
        }

        project.setManager(currentUser);
        project.setPropertyTags(propertyTagService.getOrCreateTags(project.getPropertyTags()));
        project.setOrganization(currentUser.getOrganization());

        PostingFeed postingFeed = new PostingFeed();
        postingFeedRepository.save(postingFeed);
        project.setPostingFeed(postingFeed);

        if(project.getResourceRequirements() != null) {
            for(ResourceRequirement requirement : project.getResourceRequirements()) {
                requirement.setOriginalAmount(requirement.getAmount());
                requirement.setProject(project);
            }
        }

        return projectRepository.save(project);
    }

    /**
     * updates a project
     * @return the updated project
     * @throws OperationForbiddenException if the user's organization and the project's organization do not match or
     * the user is neither the manager of the project nor the owner of the project's organization
     * @throws org.respondeco.respondeco.service.exception.ResourceNotFoundException if a resource requirement could not be created or updated, {@see ResourceService}
     * @throws IllegalValueException if no id was given or the project with the given id could not be found,
     * or if the given parameters are inconsistent, e.g. the project is concrete, but start or end date are not given
     */
    public Project update(Project updatedProject) {
        EntityAssert.Project.isUpdateable(updatedProject, ERROR_PREFIX);
        User currentUser = userService.getUserWithAuthorities();
        Organization organization = currentUser.getOrganization();
        //check if user does belong to an organization
        if(organization == null) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_ORGANIZATION),
                "Current user (%2) does not belong to an organization",
                Arrays.asList("userId", "userName"),
                Arrays.asList(currentUser.getId(), currentUser.getLogin()));
        }
        Project currentProject = projectRepository.findOne(updatedProject.getId());
        if(currentProject == null) {
            //no project found
            throw new NoSuchEntityException(ERROR_PREFIX, updatedProject.getId(), Project.class);
        }
        if(!currentProject.getOrganization().equals(organization)) {
            //project's org does not match user's org
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NOT_OWNER),
                "Current user (%2) is not owner of the project's (%4) organization (%6)",
                Arrays.asList("userId", "userName", "projectId", "projectName", "organizationId", "organizationName"),
                Arrays.asList(currentUser.getId(), currentUser.getLogin(), currentProject.getId(),
                    currentProject.getName(), organization.getId(), organization.getName()));
        }
        if(!currentUser.equals(currentProject.getManager())) {
            if(!currentUser.equals(organization.getOwner())) {
                //user is neither project manager nor organization owner
                throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORITY),
                    "Current user (%2) does not have permission to alter project %4",
                    Arrays.asList("userId", "userName", "projectId", "projectName"),
                    Arrays.asList(currentUser.getId(), currentUser.getLogin(), currentProject.getId(),
                        currentProject.getName()));
            }
        }

        currentProject.setName(updatedProject.getName());
        currentProject.setPurpose(updatedProject.getPurpose());
        currentProject.setConcrete(updatedProject.getConcrete());
        currentProject.setStartDate(updatedProject.getStartDate());
        currentProject.setPropertyTags(propertyTagService.getOrCreateTags(updatedProject.getPropertyTags()));
        currentProject.setResourceRequirements(resourceService
            .getUpdatedRequirements(currentProject, updatedProject.getResourceRequirements()));

        Project resultProject = projectRepository.saveAndFlush(currentProject);
        resultProject.getResourceRequirements().removeIf(
          requirement -> !requirement.getActive()
        );
        return resultProject;
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
    public Project setManager(Long id, Long newManagerId) throws IllegalValueException {
        User newManager = userRepository.findOne(newManagerId);
        if(newManager == null) {
            throw new NoSuchEntityException(UserService.ERROR_PREFIX, newManagerId, User.class);
        }
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, id, Project.class);
        }
        User currentUser = userService.getUserWithAuthorities();
        if(currentUser == null) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORITY),
                "current user is not logged in and has no authority to change the project manager of project (%2)",
                Arrays.asList("projectId", "projectName"),
                Arrays.asList(project.getId(), project.getName()));
        }
        //check if user has authority to delete
        if(!project.getManager().equals(currentUser)) {
            if(!project.getOrganization().getOwner().equals(currentUser)) {
                throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORITY),
                    "current user (%2) has no authority to change the project manager of project (%4)",
                    Arrays.asList("userId", "userLogin", "projectId", "projectName"),
                    Arrays.asList(currentUser.getId(), currentUser.getLogin(), project.getId(), project.getName()));
            }
        }
        //check if the new manager is part of the project's organization
        if(!project.getOrganization().equals(newManager.getOrganization())) {
            throw new IllegalValueException(ERROR_PREFIX.join(ERROR_NOT_PART_OF_ORGANIZATION),
                "new manager (%2) does not belong to organization %4",
                Arrays.asList("newManagerId", "newManagerName", "organizationId", "organizationName"),
                Arrays.asList(newManager.getId(), newManager.getLogin(), project.getOrganization().getId(),
                    project.getOrganization().getName()));
        }
        project.setManager(newManager);
        return projectRepository.save(project);
    }

    /**
     * delete a project
     * @param id the id of the project to delete
     * @throws OperationForbiddenException if the current user is neither the manager of the project nor the owner
     * of the project's organization
     * @throws NoSuchEntityException if the project with the given id could not be found
     */
    public void delete(Long id) throws OperationForbiddenException, NoSuchEntityException {
        Project project = projectRepository.findOne(id);
        if(project == null) {
            throw new NoSuchEntityException(ERROR_PREFIX, id, Project.class);
        }
        User currentUser = userService.getUserWithAuthorities();
        User manager = project.getManager();
        log.debug("current user: {}, manager: {}", currentUser, manager);
        if(currentUser == null) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORITY),
                "current user is not logged in and has no authority to change the project manager of project (%2)",
                Arrays.asList("projectId", "projectName"),
                Arrays.asList(project.getId(), project.getName()));
        }
        //check if user has authority to delete
        if(!project.getManager().equals(currentUser)) {
            if(!project.getOrganization().getOwner().equals(currentUser)) {
                throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORITY),
                    "current user (%2) has no authority to change the project manager of project (%4)",
                    Arrays.asList("userId", "userLogin", "projectId", "projectName"),
                    Arrays.asList(currentUser.getId(), currentUser.getLogin(), project.getId(), project.getName()));
            }
        }
        projectRepository.delete(id);
    }

    /**
     * get a project by its id
     * @param id the id of the project
     * @return the project with the given id
     */
    public Project findProjectById(Long id) {
        Project project = projectRepository.findOne(id);
        if(project == null) {
            return null;
        }
        if(project.getResourceRequirements() != null) {
            project.getResourceRequirements().removeIf(new java.util.function.Predicate<ResourceRequirement>() {
                @Override
                public boolean test(ResourceRequirement resourceRequirement) {
                    return resourceRequirement.getActive() == false;
                }
            });
        }
        return project;
    }

    /**
     * Find projects by name and tags
     * @param searchText the text to search for in project names and tags
     * @param restParameters other parameters for paging and sorting
     * @return a page of Projects which match the given name and are associated with the given tags, paged and sorted
     * with the given RestParameters
     */
    public Page<Project> findProjects(String searchText, RestParameters restParameters) {
        PageRequest pageRequest = null;
        Page<Project> page;

        if(restParameters != null) {
            pageRequest = restParameters.buildPageRequest();
        }

        if(searchText == null || searchText.isEmpty()) {
            page = projectRepository.findByActiveIsTrue(pageRequest);
        } else {
            String[] searchValues = searchText.split(" ");
            QProject project = QProject.project;
            List<Predicate> projectNameOrTagLike = new ArrayList<>();
            for(String sval : searchValues) {
                sval = sval.trim();
                if(sval.length() == 0) {
                    continue;
                }
                projectNameOrTagLike.add(project.name.containsIgnoreCase(sval));
                projectNameOrTagLike.add(project.propertyTags.any().name.containsIgnoreCase(sval));
            }
            BooleanExpression isActive = project.active.isTrue();

            Predicate nameOrTags = ExpressionUtils.anyOf(projectNameOrTagLike);
            Predicate query = ExpressionUtils.allOf(nameOrTags, isActive);

            page = projectRepository.findAll(query, pageRequest);
        }

        page.forEach(p -> {
            // order tags ascending by length
            p.getPropertyTags().sort(new Comparator<PropertyTag>() {
                @Override
                public int compare(PropertyTag o1, PropertyTag o2) {
                    int l1 = o1.getName().length();
                    int l2 = o2.getName().length();
                    int order = 0;

                    if (l1 > l2) {
                        order = -1;
                    } else if (l1 < l2) {
                        order = 1;
                    }

                    return order;
                }
            });
        });

        return page;
    }

    /**
     * Find projects by organization, name and tags
     * @param orgId projects will be searched for based on their organization
     * @param searchText the text to search for in name and tags of the project
     * @param restParameters other parameters for paging and sorting
     * @return a page of Projects which match the given name and are associated with the given tags, paged and sorted
     * with the given RestParameters
     */
    public Page<Project> findProjectsFromOrganization(Long orgId, String searchText, RestParameters restParameters) {
        PageRequest pageRequest = null;
        Page page;

        if(restParameters != null) {
            pageRequest = restParameters.buildPageRequest();
        }

        QProject project = QProject.project;
        BooleanExpression projectOrgEquals = project.organization.id.eq(orgId);
        BooleanExpression isActive = project.active.isTrue();
        if(searchText == null || searchText.isEmpty()) {
            Predicate query = ExpressionUtils.allOf(projectOrgEquals, isActive);
            page = projectRepository.findAll(query, pageRequest);
        } else {
            BooleanExpression projectNameLike = project.name.containsIgnoreCase(searchText);
            BooleanExpression projectTagLike = project.propertyTags.any().name.containsIgnoreCase(searchText);

            Predicate nameOrTags = ExpressionUtils.anyOf(projectNameLike, projectTagLike);
            Predicate query = ExpressionUtils.allOf(projectOrgEquals, nameOrTags, isActive);

            page = projectRepository.findAll(query, pageRequest);
        }

        return page;
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
                if (requirement.getIsEssential() != null && requirement.getAmount().compareTo(new BigDecimal(0)) > 0) {
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

                        resourceOfferRepository.save(resourceOffer);
                    });
                }
            }
        });
    }

    /**
     * Get the current Following State of the Project, instead of loading all Users manually.
     * Returns TRUE: if user already follow the Project
     * @param projectId of which we need the following state
     * @return Boolean
     */
    public Boolean followingState(Long projectId){

        User currentUser = userService.getUserWithAuthorities();

        return projectRepository.findByUserIdAndProjectId(currentUser.getId(), projectId) != null;
    }

    /**
     * Allow user to mark the given project as followed. If this become true, all newsfeed from project
     * will be displayed in users dashboard
     * @param projectId (project) that user would like to follow (add to subscripotion).
     * @return true, if the follow-connection was newly created, false if the connetion already existed
     */
    public Boolean follow(Long projectId) throws IllegalValueException{
        User currentUser = userService.getUserWithAuthorities();

        // check if project already exists for the current user and given project id.
        // if true, we will allow an duplicate entry that will cause primary key constraint.
        // Better to throw an exception
        if(projectRepository.findByUserIdAndProjectId(currentUser.getId(), projectId) != null){
            return false;
        }

        Project selected = projectRepository.findOne(projectId);

        // check if project exists and is active. "Removed" projects will cause some confusion for users, so throw
        // an exception if project is deactivated
        if(selected == null){
            throw new NoSuchEntityException(ERROR_PREFIX, projectId, Project.class);
        }

        // add new follower over Projects Table and save it
        List<Project> followers = currentUser.getFollowProjects();
        followers.add(selected);
        userRepository.save(currentUser);
        return true;
    }

    /**
     * Remove user from follower List and stop propagate the news from sepcific project
     * @param projectId (project) to un-follow or remove newsfeed subscription
     */
    public void unfollow(Long projectId) throws IllegalValueException{
        User currentUser = userService.getUserWithAuthorities();

        Project selected = projectRepository.findByUserIdAndProjectId(currentUser.getId(), projectId);

        // check if project exists and is active. "Removed" projects will cause some confusion for users, so throw
        // an exception if project is deactivated
        if(selected == null){
            throw new NoSuchEntityException(ERROR_PREFIX, projectId, Project.class);
        }

        // add new follower
        List<Project> followers = currentUser.getFollowProjects();
        followers.remove(selected);
        userRepository.save(currentUser);
    }
}
