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
@Transactional
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

    public void rateProject(Long projectId, Long organizationId, Integer ratingValue, String comment)
            throws NoSuchResourceMatchException {
        User user = userService.getUserWithAuthorities();
        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(organizationId);
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
                    String.format("You are not the owner of organization %s" , organizationId));
        }
        if(resourceMatch.getProjectRating() != null) {
            throw new SupporterRatingException(".allreadyrated",
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
            throws NoSuchResourceMatchException {
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

    public Object[] getAggregatedRatingByProject(Long projectId) {
        return resourceMatchRepository.getAggregatedRatingByProject(projectId);
    }

    public AggregatedRating getAggregatedRatingByOrganization(Long organizationId) {
        return resourceMatchRepository.getAggregatedRatingByOrganization(organizationId);
    }
/**
    public Rating createProjectRating(Integer rating, String comment, Long projectId) throws ProjectRatingException, NotOwnerOfOrganizationException {
        User user = userService.getUserWithAuthorities();
        Project project = projectService.findProjectById(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        Rating projectRating1 = ratingRepository.findByUserAndProject(user,project);
        if(projectRating1!=null) {
            throw new ProjectRatingException(".multiplerating", String.format("Can't rate project %s twice", projectId));
        }
        if(user.getOrganization().getOwner().equals(user) == false) {
            throw new NotOwnerOfOrganizationException(String.format("User is not owner of organization"));
        }
        if(user.getOrganization().equals(project.getOrganization())) {
            throw new ProjectRatingException(".ownorganization", String.format("Can't rate own project %s", projectId));
        }
        List<Organization> organizations = ratingRepository.findOrganizationsByResourceRequirements(projectId);
        if(organizations.contains(user.getOrganization())) {
            throw new ProjectRatingException(".noresourceexchange", String.format("Can't rate project %s because organization didn't offer resource", projectId));
        }
        Rating projectRating = new Rating();
        projectRating.setRating(rating);
        projectRating.setComment(comment);
        //projectRating.setProject(project);
        //projectRating.setUser(user);
        ratingRepository.save(projectRating);
        return projectRating;
    }

    public Rating getProjectRating(Long projectId) {
        User user = userService.getUserWithAuthorities();
        Project project = projectService.findProjectById(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        Rating projectRating = ratingRepository.findByUserAndProject(user,project);
        return projectRating;
    }

    public void updateProjectRating(Integer rating, String comment, Long ratingId) throws NoSuchProjectRatingException {
        User user = userService.getUserWithAuthorities();
        Rating projectRating = ratingRepository.findOne(ratingId);
        if(projectRating == null) {
            throw new NoSuchProjectRatingException(String.format("There is no such project rating"));
        }
        /**
        if(projectRating.getUser().equals(user) == false) {
            throw new NoSuchProjectRatingException(String.format("This rating is not user's rating %s", user.getLogin()));
        }

        projectRating.setRating(rating);
        projectRating.setComment(comment);
        ratingRepository.save(projectRating);
    }**/
}
