package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.AggregatedRating;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ProjectRating;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.ProjectRatingRepository;
import org.respondeco.respondeco.service.exception.MultipleRatingException;
import org.respondeco.respondeco.service.exception.NoSuchProjectRatingException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.service.exception.RatingOwnProjectException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Service
@Transactional
public class ProjectRatingService {

    private ProjectRatingRepository projectRatingRepository;

    private UserService userService;

    @Inject
    public ProjectRatingService(ProjectRatingRepository projectRatingRepository, UserService userService) {
        this.projectRatingRepository = projectRatingRepository;
        this.userService = userService;
    }

    public AggregatedRating getAggregatedRating(Long projectId) {
        return projectRatingRepository.getAggregatedRatingForProject(projectId);
    }

    public ProjectRating createProjectRating(Double rating, String comment, Project project) throws RatingOwnProjectException, MultipleRatingException {
        User user = userService.getUserWithAuthorities();
        ProjectRating projectRating1 = projectRatingRepository.findByUserAndProject(user,project);
        if(projectRating1!=null) {
            throw new MultipleRatingException(String.format("Can't rate project %s a second time", project.getId()));
        }
        if(user.getOrganization().equals(project.getOrganization())) {
            throw new RatingOwnProjectException(String.format("Can't rate own project"));
        }
        ProjectRating projectRating = new ProjectRating();
        projectRating.setRating(rating);
        projectRating.setComment(comment);
        projectRating.setProject(project);
        projectRating.setUser(user);
        projectRatingRepository.save(projectRating);
        return projectRating;
    }

    public void updateProjectRating(Double rating, String comment, Long ratingId) throws NoSuchProjectRatingException {
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
