package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.RatingRepository;
import org.respondeco.respondeco.repository.ResourceMatchRepository;
import org.respondeco.respondeco.service.exception.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Service
public class RatingService {

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

    public void rateProject(Long projectId, Long matchId, Integer ratingValue, String comment)
            throws NoSuchResourceMatchException,
            NoSuchOrganizationException,
            NoSuchProjectException,
            ProjectRatingException {
        User user = userService.getUserWithAuthorities();
        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        Organization organization = user.getOrganization();
        if(project == null) {
            throw new NoSuchProjectException(String.format("Project doesn't exist"));
        }
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization doesn't exist"));
        }
        ResourceMatch resourceMatch = resourceMatchRepository.findOne(matchId);
        if(resourceMatch == null) {
            throw new NoSuchResourceMatchException(String.format("ResourceMatch doesn't exist"));
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
            throw new ProjectRatingException(".allreadyrated",
                    String.format("You have already rated for this match %s" , resourceMatch.getId()));
        }
        Rating rating = new Rating();
        rating.setRating(ratingValue);
        rating.setComment(comment);
        rating.setResourceMatch(resourceMatch);
        resourceMatch.setProjectRating(rating);
        ratingRepository.save(rating);
    }

    public void rateOrganization(Long orgId, Long matchId, Integer ratingValue, String comment)
            throws NoSuchResourceMatchException,
            NoSuchProjectException,
            NoSuchOrganizationException,
            SupporterRatingException {
        User user = userService.getUserWithAuthorities();
        ResourceMatch resourceMatch = resourceMatchRepository.findOne(matchId);
        if(resourceMatch == null) {
            throw new NoSuchResourceMatchException(String.format("ResourceMatch doesn't exist"));
        }
        Project project = resourceMatch.getProject();
        Organization organization = resourceMatch.getOrganization();
        if(project == null) {
            throw new NoSuchProjectException(String.format("Project doesn't exist"));
        }
        if(organization == null || organization.getId().equals(orgId) == false) {
            throw new NoSuchOrganizationException(String.format("Organization doesn't exist"));
        }
        if(project.getManager().equals(user) == false) {
            throw new SupporterRatingException(".notmanagerofproject", String
                    .format("You are not the manager of project %s", project.getId()));
        }
        if(organization.getOwner().equals(user) == true) {
            throw new SupporterRatingException(".notallowedtorate",
                    String.format("You are not allowed to rate this organization %s", organization.getId()));
        }
        if(resourceMatch.getAccepted() == false) {
            throw new SupporterRatingException(".notaccepted",
                    String.format("Rating this match %s is not accepted" , resourceMatch.getId()));
        }
        if(resourceMatch.getSupporterRating() != null) {
            throw new SupporterRatingException(".allreadyrated",
                    String.format("You have already rated for this match %s", matchId));
        }
        Rating rating = new Rating();
        rating.setRating(ratingValue);
        rating.setComment(comment);
        rating.setResourceMatch(resourceMatch);
        resourceMatch.setSupporterRating(rating);
        ratingRepository.save(rating);
    }

    /**
     * checks for a given project, if the current user is allowed to rate it
     * @param projectId the project for which to check
     * @return the rating permission for this project
     */
    public RatingPermission checkPermissionForProject(Long projectId) throws NoSuchProjectException {
        Project project = projectService.findProjectById(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        User currentUser = userService.getUserWithAuthorities();
        RatingPermission permission = new RatingPermission();
        permission.setAllowed(false);
        //check if user organization and project organization are the same
        if(currentUser.getOrganization().equals(project.getOrganization()) == false) {
            //check if user is owner of his organization
            if(currentUser.getOrganization().getOwner().equals(currentUser) == true) {
                List<ResourceMatch> matches = resourceMatchRepository
                    .findByProjectAndOrganization(project, currentUser.getOrganization());
                for(ResourceMatch match : matches) {
                    //user can rate if there exists a match which has been accepted and is still without a project rating
                    if(match.getAccepted() && match.getProjectRating() == null) {
                        permission.setResourceMatch(match);
                        permission.setAllowed(true);
                        break;
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
     * @throws NoSuchResourceMatchException if a match in the input list can not be found
     */
    public List<RatingPermission> checkPermissionsForMatches(List<Long> matchIds) throws NoSuchResourceMatchException {
        User currentUser = userService.getUserWithAuthorities();
        List<RatingPermission> ratingPermissions = new ArrayList<>();
        ResourceMatch match;
        RatingPermission permission;
        for(Long id : matchIds) {
            match = resourceMatchRepository.findOne(id);
            if(match == null) {
                throw new NoSuchResourceMatchException(id);
            }
            permission = new RatingPermission();
            permission.setResourceMatch(match);
            permission.setAllowed(false);
            //if the user is project manager of the project which is connected to the match
            if(match.getProject().getManager().equals(currentUser)) {
                //if the match was accepted and has not been rated yet
                if(match.getAccepted() && match.getSupporterRating() == null) {
                    permission.setAllowed(true);
                }
            }
            ratingPermissions.add(permission);
        }
        return ratingPermissions;
    }

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
