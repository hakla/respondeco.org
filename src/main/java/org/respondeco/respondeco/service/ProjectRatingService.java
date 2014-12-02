package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.AggregatedRating;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.repository.ProjectRatingRepository;
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

    @Inject
    public ProjectRatingService(ProjectRatingRepository projectRatingRepository) {
        this.projectRatingRepository = projectRatingRepository;
    }

    public AggregatedRating getAggregatedRating(Long projectId) {
        return projectRatingRepository.getAggregatedRatingForProject(projectId);
    }

}
