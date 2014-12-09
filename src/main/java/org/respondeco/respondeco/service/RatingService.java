package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.repository.RatingRepository;
import org.respondeco.respondeco.repository.ResourceMatchRepository;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.dto.ProjectResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
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

    public void rateProject(Long projectId, Integer ratingValue, String comment)
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
        ResourceMatch resourceMatch = resourceMatchRepository.findByProjectAndOrganization(project,organization);
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

    public void rateOrganization(Long matchId, Integer ratingValue, String comment)
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
        if(organization == null) {
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

    public AggregatedRating getAggregatedRatingByProject(Long projectId)
            throws NoSuchProjectRatingException,
            ProjectRatingException {
        Object[][] objectArray = resourceMatchRepository.getAggregatedRatingByProject(projectId);
        if(objectArray[0] == null) {
            throw new NoSuchProjectRatingException(String.format("There exists no such rating for project %s", projectId));
        }
        if(objectArray[0][0] == null || objectArray[0][1] == null) {
            throw new ProjectRatingException(".wrongAggregatedRating", String.format("AggregatedRating is wrong"));
        }
        AggregatedRating aggregatedRating = new AggregatedRating();
        aggregatedRating.setCount((Integer) objectArray[0][0]);
        aggregatedRating.setRating((Double) objectArray[0][1]);
        return aggregatedRating;
    }

    public AggregatedRating getAggregatedRatingByOrganization(Long organizationId)
            throws NoSuchSupporterRatingException,
            SupporterRatingException {
        Object[][] objectArray = resourceMatchRepository.getAggregatedRatingByOrganization(organizationId);
        if(objectArray[0] == null) {
            throw new NoSuchSupporterRatingException(String.format("There exists no such rating for organization %s", organizationId));
        }
        if(objectArray[0][0] == null || objectArray[0][1] == null) {
            throw new SupporterRatingException(".wrongAggregatedRating", String.format("AggregatedRating is wrong"));
        }
        AggregatedRating aggregatedRating = new AggregatedRating();
        aggregatedRating.setCount((Integer) objectArray[0][0]);
        aggregatedRating.setRating((Double) objectArray[0][1]);
        return aggregatedRating;
    }
}
