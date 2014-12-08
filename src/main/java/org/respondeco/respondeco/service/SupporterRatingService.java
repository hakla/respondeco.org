package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.service.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Service
@Transactional
public class SupporterRatingService {


    /**
     * RESTRUCTURE INTO A SHARED RatingService


    private SupporterRatingRepository supporterRatingRepository;

    private UserService userService;

    private ProjectService projectService;

    private OrganizationService organizationService;

    @Inject
    public SupporterRatingService(SupporterRatingRepository supporterRatingRepository, UserService userService, ProjectService projectService, OrganizationService organizationService) {
        this.supporterRatingRepository = supporterRatingRepository;
        this.userService = userService;
        this.projectService = projectService;
        this.organizationService = organizationService;
    }

    public AggregatedRating getAggregatedRating(Long organizationId) {
        return supporterRatingRepository.getAggregatedRatingForOrganization(organizationId);
    }

    public SupporterRating createSupporterRating(Integer rating, String comment, Long projectId, Long organizationId) throws SupporterRatingException {
        User user = userService.getUserWithAuthorities();
        Project project = projectService.findProjectById(projectId);
        Organization organization = organizationService.getOrganization(organizationId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization does not exist"));
        }
        SupporterRating supporterRating1 = supporterRatingRepository.findByProjectAndOrganization(project,organization);
        if(supporterRating1!=null) {
            throw new SupporterRatingException(".multiplerating", String.format("Can't rate organization %s twice", organizationId));
        }
        if(user.getOrganization().equals(organization) || project.getOrganization().equals(organization)) {
            throw new SupporterRatingException(".ownorganization", String.format("Can't rate own organization %s", organizationId));
        }
        if(project.getManager().equals(user) == false) {
            throw new SupporterRatingException(".notmanager", String.format("Can't rate when not manager of project %s", projectId));
        }
        List<Organization> organizations = supporterRatingRepository.findOrganizationsByResourceRequirements(projectId);
        if(organizations.contains(organization)) {
            throw new SupporterRatingException(".noresourceexchange", String.format("Can't rate organization %s because organization didn't offer resource", organizationId));
        }
        SupporterRating supporterRating = new SupporterRating();
        supporterRating.setRating(rating);
        supporterRating.setComment(comment);
        supporterRating.setProject(project);
        supporterRating.setUser(user);
        supporterRating.setOrganization(organization);
        supporterRatingRepository.save(supporterRating);
        return supporterRating;
    }

    public SupporterRating getSupporterRating(Long organizationId, Long projectId) {
        User user = userService.getUserWithAuthorities();
        Organization organization = organizationService.getOrganization(organizationId);
        Project project = projectService.findProjectById(projectId);
        if(organization == null) {
            throw new NoSuchOrganizationException(String.format("Organization %s does not exist", organizationId));
        }
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        if(project.getManager().equals(user) == false) {
            throw new SupporterRatingException(".notmanager", String.format("Can't get rate when not manager of project %s", projectId));
        }
        SupporterRating supporterRating = supporterRatingRepository.findByProjectAndOrganization(project, organization);
        return supporterRating;
    }

    public void updateSupporterRating(Integer rating, String comment, Long ratingId) throws NoSuchSupporterRatingException {
        User user = userService.getUserWithAuthorities();
        SupporterRating supporterRating = supporterRatingRepository.findOne(ratingId);
        if(supporterRating == null) {
            throw new NoSuchSupporterRatingException(String.format("There is no such supporter rating"));
        }
        if(supporterRating.getUser().equals(user) == false) {
            throw new NoSuchSupporterRatingException(String.format("This rating is not user's rating %s", user.getLogin()));
        }
        supporterRating.setRating(rating);
        supporterRating.setComment(comment);
        supporterRatingRepository.save(supporterRating);
    }
    */
}
