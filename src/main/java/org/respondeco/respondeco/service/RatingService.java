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
import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Service
public class RatingService {

    private final Logger log = LoggerFactory.getLogger(RatingService.class);

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
     * @throws ProjectRatingException project hasn't been started yet, project is not concrete, match hasn't been
     * accepted yet (no resource exchange so far), user is not owner of organization, organization doesn't match
     * the organization from the resourcematch, the project doesn't match the resourcematch, the match has already
     * been rated
     */
    public void rateProject(Long projectId, Long matchId, Integer ratingValue, String comment)
            throws NoSuchEntityException,
            ProjectRatingException {
        User user = userService.getUserWithAuthorities();
        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        Organization organization = user.getOrganization();
        if(project == null) {
            throw new NoSuchEntityException(String.format("Project doesn't exist"));
        }
        if(project.isConcrete() == false) {
            throw new ProjectRatingException(".notconcrete", "The project cannot be rated" +
                " as it does not have a start date");
        }
        if(project.getSuccessful() == false) {
            throw new ProjectRatingException(".notstarted", "The project has not started yet");
        }
        if(organization == null) {
            throw new NoSuchEntityException(String.format("Organization doesn't exist"));
        }
        ResourceMatch resourceMatch = resourceMatchRepository.findOne(matchId);
        if(resourceMatch == null) {
            throw new NoSuchEntityException(String.format("ResourceMatch doesn't exist"));
        }
        if(resourceMatch.getAccepted() == false) {
            throw new ProjectRatingException(".notaccepted",
                    String.format("Rating this match %s is not accepted" , resourceMatch.getId()));
        }
        if(organization.getOwner().equals(user) == false) {
            throw new ProjectRatingException(".notowneroforganization",
                    String.format("You are not the owner of organization %s", organization.getId()));
        }
        if(organization.equals(resourceMatch.getOrganization()) == false) {
            throw new ProjectRatingException(".notsameorganization",
                String.format("The user's organization and the match's organization do not match, " +
                    "user's organization: %s, match's organization: %s",
                    organization, resourceMatch.getOrganization()));
        }
        if(project.equals(resourceMatch.getProject()) == false) {
            throw new ProjectRatingException(".notsameproject",
                String.format("The given project and the match's project do not match, " +
                        "given project: %s, match's project: %s",
                    project, resourceMatch.getProject()));
        }
        if(resourceMatch.getProjectRating() != null) {
            throw new ProjectRatingException(".project.allreadyrated",
                    String.format("You have already rated for this match %s" , resourceMatch.getId()));
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
            throw new NoSuchEntityException(String.format("ResourceMatch doesn't exist"));
        }
        Project project = resourceMatch.getProject();
        Organization organization = resourceMatch.getOrganization();
        if(project == null) {
            throw new NoSuchEntityException(String.format("Project doesn't exist"));
        }
        if(project.isConcrete() == false) {
            throw new SupporterRatingException(".notconcrete", "The project cannot be rated" +
                " as it does not have a start date");
        }
        if(project.getSuccessful() == false) {
            throw new SupporterRatingException(".notstarted", "The project has not started yet");
        }
        if(organization == null || organization.getId().equals(orgId) == false) {
            throw new NoSuchEntityException(String.format("Organization doesn't exist"));
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
            throw new NoSuchEntityException(projectId);
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
                throw new NoSuchEntityException(id);
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
