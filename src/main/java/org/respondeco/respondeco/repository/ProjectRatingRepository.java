package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.AggregatedRating;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ProjectRating;
import org.respondeco.respondeco.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */
public interface ProjectRatingRepository extends JpaRepository<ProjectRating, Long> {

    @Query(
        "SELECT COUNT(r) AS count, AVG(r) AS rating " +
        "FROM ProjectRating r INNER JOIN r.project p " +
        "WHERE p.id = :projectid")
    public AggregatedRating getAggregatedRatingForProject(@Param("projectid") Long id);

    public ProjectRating findByUserAndProject(User user, Project project);

}
