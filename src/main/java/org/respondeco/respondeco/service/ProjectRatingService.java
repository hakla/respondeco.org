package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.ProjectRatingRepository;
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
public class ProjectRatingService {

    private ProjectRatingRepository projectRatingRepository;

    private UserService userService;

    private ProjectService projectService;

    @Inject
    public ProjectRatingService(ProjectRatingRepository projectRatingRepository, UserService userService, ProjectService projectService) {
        this.projectRatingRepository = projectRatingRepository;
        this.userService = userService;
        this.projectService = projectService;
    }

    public AggregatedRating getAggregatedRating(Long projectId) {
        return projectRatingRepository.getAggregatedRatingForProject(projectId);
    }

    public ProjectRating createProjectRating(Integer rating, String comment, Long projectId) throws ProjectRatingException, NotOwnerOfOrganizationException {
        User user = userService.getUserWithAuthorities();
        Project project = projectService.findProjectById(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        ProjectRating projectRating1 = projectRatingRepository.findByUserAndProject(user,project);
        if(projectRating1!=null) {
            throw new ProjectRatingException(".multiplerating", String.format("Can't rate project %s twice", projectId));
        }
        if(user.getOrganization().getOwner().equals(user) == false) {
            throw new NotOwnerOfOrganizationException(String.format("User is not owner of organization"));
        }
        if(user.getOrganization().equals(project.getOrganization())) {
            throw new ProjectRatingException(".ownorganization", String.format("Can't rate own project %s", projectId));
        }
        List<Organization> organizations = projectRatingRepository.findOrganizationsByResourceRequirements(projectId);
        if(organizations.contains(user.getOrganization())) {
            throw new ProjectRatingException(".noresourceexchange", String.format("Can't rate project %s because organization didn't offer resource", projectId));
        }
        ProjectRating projectRating = new ProjectRating();
        projectRating.setRating(rating);
        projectRating.setComment(comment);
        projectRating.setProject(project);
        projectRating.setUser(user);
        projectRatingRepository.save(projectRating);
        return projectRating;
    }

    public ProjectRating getProjectRating(Long projectId) {
        User user = userService.getUserWithAuthorities();
        Project project = projectService.findProjectById(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        ProjectRating projectRating = projectRatingRepository.findByUserAndProject(user,project);
        return projectRating;
    }

    public void updateProjectRating(Integer rating, String comment, Long ratingId) throws NoSuchProjectRatingException {
        User user = userService.getUserWithAuthorities();
        ProjectRating projectRating = projectRatingRepository.findOne(ratingId);
        if(projectRating == null) {
            throw new NoSuchProjectRatingException(String.format("There is no such project rating"));
        }
        if(projectRating.getUser().equals(user) == false) {
            throw new NoSuchProjectRatingException(String.format("This rating is not user's rating %s", user.getLogin()));
        }
        projectRating.setRating(rating);
        projectRating.setComment(comment);
        projectRatingRepository.save(projectRating);
    }
}
