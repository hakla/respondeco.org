package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.RatingRepository;
import org.respondeco.respondeco.repository.ResourceMatchRepository;
import org.respondeco.respondeco.service.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Service
public class RatingService {

    private final Logger log = LoggerFactory.getLogger(RatingService.class);

    public static final ServiceException.ErrorPrefix ERROR_PREFIX = new ServiceException.ErrorPrefix("rating");
    public static final String ERROR_ALREADY_RATED                  = "already_rated";
    public static final String ERROR_MATCH_NOT_ACCEPTED             = "match_not_accepted";
    public static final String ERROR_NOT_STARTED                    = "not_started";
    public static final String ERROR_NO_AUTHORITY                   = "no_authority";
    public static final String ERROR_NO_START_DATE                  = "no_start_date";
    public static final String ERROR_ORGANIZATIONS_NOT_MATCHING     = "organizations_not_matching";
    public static final String ERROR_PROJECTS_NOT_MATCHING          = "projects_not_matching";


    private RatingRepository ratingRepository;

    private ResourceMatchRepository resourceMatchRepository;

    private ProjectRepository projectRepository;

    private OrganizationRepository organizationRepository;

    private UserService userService;

    private ProjectService projectService;

    @Inject
    public RatingService(RatingRepository ratingRepository, ResourceMatchRepository resourceMatchRepository,
                         ProjectRepository projectRepository, OrganizationRepository organizationRepository,
                         UserService userService, ProjectService projectService) {
        this.ratingRepository = ratingRepository;
        this.resourceMatchRepository = resourceMatchRepository;
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.userService = userService;
        this.projectService = projectService;
    }

    /**
     * mehtod to rate a project; successfully if the project does exist, the match does exist, project is concrete and
     * has been started already, user is owner of organization which has resource exchange with the project and
     * the match has not been used for rating this/a project already
     * @param projectId the given id of the project which should be rated
     * @param matchId the given id of the resourcematch which has to exist and has to include the project and is unrated
     * @param ratingValue integer value for rating
     * @param comment string for commentary of rating
     * @throws NoSuchEntityException resource match or project doesn't exist in repository
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException organization doesn't exist (user is not a member of an organization)
     * accepted yet (no resource exchange so far), user is not owner of organization, organization doesn't match
     * the organization from the resourcematch, the project doesn't match the resourcematch, the match has already
     * been rated
     */
    public void rateProject(Long projectId, Long matchId, Integer ratingValue, String comment)
            throws NoSuchEntityException {
        User user = userService.getUserWithAuthorities();
        if(user == null) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_AUTHORITY),
                "Current user (not logged in) has no authority to rate project %1.",
                Arrays.asList("projectId"),
                Arrays.asList(projectId));
        }
        Organization organization = user.getOrganization();
        if(organization == null) {
            throw new OperationForbiddenException(
                OrganizationService.ERROR_PREFIX.join(OrganizationService.ERROR_NOT_OWNER),
                "Current User (%1, %2) is not owner of an Organization",
                Arrays.asList("userId", "userLogin"),
                Arrays.asList(user.getId(), user.getLogin()));
        }
        Project project = projectRepository.findOne(projectId);
        if(project == null) {
            throw new NoSuchEntityException(ProjectService.ERROR_PREFIX, projectId, Project.class);
        }
        ResourceMatch resourceMatch = resourceMatchRepository.findOne(matchId);
        if(resourceMatch == null) {
            throw new NoSuchEntityException(ResourceService.ERROR_MATCH_PREFIX, matchId, ResourceMatch.class);
        }
        if(!project.getConcrete()) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NO_START_DATE),
                "Project %1 (%2) can not be rated as it does not have a start date.",
                Arrays.asList("projectId", "projectName"),
                Arrays.asList(project.getId(), project.getName()));
        }
        if(!project.getSuccessful()) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_NOT_STARTED),
                "Project %1 (%2) can not be rated as it has not started yet.",
                Arrays.asList("projectId", "projectName"),
                Arrays.asList(project.getId(), project.getName()));
        }
        if(!resourceMatch.getAccepted()) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_MATCH_NOT_ACCEPTED),
                "Project %1 (%2) can not be rated, the resource match %3 has not been accepted yet.",
                Arrays.asList("projectId", "projectName", "resourceMatchId"),
                Arrays.asList(project.getId(), project.getName(), resourceMatch.getId()));
        }
        if(!organization.getOwner().equals(user)) {
            throw new OperationForbiddenException(
                OrganizationService.ERROR_PREFIX.join(OrganizationService.ERROR_NOT_OWNER),
                "Current User (%1, %2) is not owner of Organization %3 (%4).",
                Arrays.asList("userId", "userLogin", "organizationId", "organizationName"),
                Arrays.asList(user.getId(), user.getLogin(), organization.getId(), organization.getName()));
        }
        if(!organization.equals(resourceMatch.getOrganization())) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_ORGANIZATIONS_NOT_MATCHING),
                "The user's organization (%1, %2) and the match's organization (%3, %4) do not match.",
                Arrays.asList("userOrganizationId", "userOrganizationName", "matchOrganizationId",
                    "matchOrganizationName"),
                Arrays.asList(organization.getId(), organization.getName(), resourceMatch.getOrganization().getId(),
                    resourceMatch.getOrganization().getName()));
        }
        if(!project.equals(resourceMatch.getProject())) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_PROJECTS_NOT_MATCHING),
                "The given project (%1, %2) and the match's project (%3, %4) do not match.",
                Arrays.asList("givenProjectId", "givenProjectName", "matchProjectId",
                    "matchProjectName"),
                Arrays.asList(project.getId(), project.getName(), resourceMatch.getProject().getId(),
                    resourceMatch.getProject().getName()));
        }
        if(resourceMatch.getProjectRating() != null) {
            throw new OperationForbiddenException(ERROR_PREFIX.join(ERROR_ALREADY_RATED),
                "The match %1 has already been rated",
                Arrays.asList("resourceMatchId"),
                Arrays.asList(resourceMatch.getId()));
        }
        Rating rating = new Rating();
        rating.setRating(ratingValue);
        rating.setComment(comment);
        rating.setResourceMatch(resourceMatch);
        resourceMatch.setProjectRating(rating);
        resourceMatchRepository.save(resourceMatch);
        ratingRepository.save(rating);
    }

    /**
     * mehtod to rate an organization; successfully if the organization does exist, the match does exist,
     * project is concrete and has been started already, user is manager of project which has resource exchange
     * with the organization, user is not owner of the organization which has to be rated and the match has not been
     * used for rating this/a organization already
     * @param orgId the given id of the organization which should be rated
     * @param matchId the given id of the resourcematch which has to exist and has to include the project and is unrated
     * @param ratingValue integer value for rating
     * @param comment string for commentary of rating
     * @throws NoSuchEntityException resource match or project doesn't exist in repository
     * @throws org.respondeco.respondeco.service.exception.NoSuchEntityException organization doesn't exist (user is not a member of an organization)
     * @throws SupporterRatingException project hasn't been started yet, project is not concrete, match hasn't been
     * accepted yet (no resource exchange so far), user is not manager of project,
     * user is owner of organization, organization doesn't match
     * the organization from the resourcematch, the project doesn't match the resourcematch, the match has already
     * been rated
     */
    public void rateOrganization(Long orgId, Long matchId, Integer ratingValue, String comment)
            throws NoSuchEntityException,
            SupporterRatingException {
        User user = userService.getUserWithAuthorities();
        ResourceMatch resourceMatch = resourceMatchRepository.findOne(matchId);
        if(resourceMatch == null) {
            throw new NoSuchEntityException(ResourceService.ERROR_MATCH_PREFIX, matchId, ResourceMatch.class);
        }
        Project project = resourceMatch.getProject();
        Organization organization = resourceMatch.getOrganization();
        if(!project.getConcrete()) {
            throw new SupporterRatingException(".notconcrete", "The project cannot be rated" +
                " as it does not have a start date");
        }
        if(project.getSuccessful() == false) {
            throw new SupporterRatingException(".notstarted", "The project has not started yet");
        }
        if(organization == null || organization.getId().equals(orgId) == false) {
            throw new NoSuchEntityException(OrganizationService.ERROR_PREFIX, orgId, Organization.class);
        }
        if(project.getManager().equals(user) == false) {
            throw new SupporterRatingException(".notmanagerofproject", String
                    .format("You are not the manager of project %d", project.getId()));
        }
        if(organization.getOwner().equals(user) == true) {
            throw new SupporterRatingException(".cannotrateown",
                    String.format("You are not allowed to rate your own organization: %d", organization.getId()));
        }
        if(resourceMatch.getAccepted() == false) {
            throw new SupporterRatingException(".notaccepted",
                    String.format("This match has not been accepted yet: %d" , resourceMatch.getId()));
        }
        if(resourceMatch.getSupporterRating() != null) {
            throw new SupporterRatingException(".org.alreadyrated",
                    String.format("You have already rated for this match %d", matchId));
        }
        Rating rating = new Rating();
        rating.setRating(ratingValue);
        rating.setComment(comment);
        rating.setResourceMatch(resourceMatch);
        resourceMatch.setSupporterRating(rating);
        resourceMatchRepository.save(resourceMatch);
        ratingRepository.save(rating);
    }

    /**
     * checks for a given project, if the current user is allowed to rate it
     * @param projectId the project for which to check
     * @return the rating permission for this project
     */
    public RatingPermission checkPermissionForProject(Long projectId) throws NoSuchEntityException {
        Project project = projectService.findProjectById(projectId);
        if(project == null) {
            throw new NoSuchEntityException(ProjectService.ERROR_PREFIX, projectId, Project.class);
        }
        User currentUser = userService.getUserWithAuthorities();
        RatingPermission permission = new RatingPermission();
        permission.setAllowed(false);
        if(currentUser.getOrganization() != null && Boolean.TRUE.equals(project.getSuccessful())) {
            //check if user organization and project organization are the same
            if (currentUser.getOrganization().equals(project.getOrganization()) == false) {
                //check if user is owner of his organization
                if (currentUser.getOrganization().getOwner().equals(currentUser) == true) {
                    List<ResourceMatch> matches = resourceMatchRepository
                        .findByProjectAndOrganization(project, currentUser.getOrganization());
                    for (ResourceMatch match : matches) {
                        //user can rate if there exists a match which has been accepted and is still without a project rating
                        if (match.getAccepted() && match.getProjectRating() == null) {
                            permission.setResourceMatch(match);
                            permission.setAllowed(true);
                            break;
                        }
                    }
                }
            }
        }
        return permission;
    }

    /**
     * Checks for each match in the input, if the current user is allowed to rate it
     * @param matchIds matches to check
     * @return a list of permissions indicating the permission of the user to rate a match
     * @throws NoSuchEntityException if a match in the input list can not be found
     */
    public List<RatingPermission> checkPermissionsForMatches(List<Long> matchIds) throws NoSuchEntityException {
        User currentUser = userService.getUserWithAuthorities();
        List<RatingPermission> ratingPermissions = new ArrayList<>();
        ResourceMatch match;
        RatingPermission permission;
        for(Long id : matchIds) {
            match = resourceMatchRepository.findOne(id);
            if(match == null) {
                throw new NoSuchEntityException(ResourceService.ERROR_MATCH_PREFIX, id, ResourceMatch.class);
            }
            permission = new RatingPermission();
            permission.setResourceMatch(match);
            permission.setAllowed(false);
            log.debug("match project: {}", match.getProject());
            if(Boolean.TRUE.equals(match.getProject().getSuccessful())) {
                //if the user is project manager of the project which is connected to the match
                if (match.getProject().getManager().equals(currentUser)) {
                    //if the match was accepted and has not been rated yet
                    if (match.getAccepted() && match.getSupporterRating() == null) {
                        permission.setAllowed(true);
                    }
                }
            }
            ratingPermissions.add(permission);
        }
        return ratingPermissions;
    }

    /**
     * method to get an aggregated rating for project which uses the object array from the repository to
     * create a new aggregated rating object (first array value is the count of rating;
     * second array value is the average rating value)
     * @param projectId the given id of the project
     * @return the created aggregated rating object
     */
    public AggregatedRating getAggregatedRatingByProject(Long projectId) {
        Object[][] objectArray = resourceMatchRepository.getAggregatedRatingByProject(projectId);
        if(objectArray[0][1] == null) {
            objectArray[0][1] = 0.0;
        }
        AggregatedRating aggregatedRating = new AggregatedRating();
        aggregatedRating.setCount((Long) objectArray[0][0]);
        aggregatedRating.setRating((Double) objectArray[0][1]);
        return aggregatedRating;
    }

    /**
     * method to get an aggregated rating for organization which uses the object array from the repository to
     * create a new aggregated rating object (first array value is the count of rating;
     * second array value is the average rating value)
     * @param organizationId the given id of the organization
     * @return the created aggregated rating object
     */
    public AggregatedRating getAggregatedRatingByOrganization(Long organizationId) {
        Object[][] objectArray = resourceMatchRepository.getAggregatedRatingByOrganization(organizationId);
        if(objectArray[0][1] == null) {
            objectArray[0][1] = 0.0;
        }
        AggregatedRating aggregatedRating = new AggregatedRating();
        aggregatedRating.setCount((Long) objectArray[0][0]);
        aggregatedRating.setRating((Double) objectArray[0][1]);
        return aggregatedRating;
    }
}
