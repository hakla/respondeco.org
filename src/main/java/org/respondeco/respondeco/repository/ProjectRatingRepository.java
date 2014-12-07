package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */
public interface ProjectRatingRepository extends JpaRepository<ProjectRating, Long> {

    @Query(
        "SELECT COUNT(r) AS count, AVG(r.rating) AS rating " +
        "FROM ProjectRating r INNER JOIN r.project p " +
        "WHERE p.id = :projectid")
    public AggregatedRating getAggregatedRatingForProject(@Param("projectid") Long id);

    public ProjectRating findByUserAndProject(User user, Project project);

    @Query("SELECT DISTINCT o " +
            "FROM Project p INNER JOIN p.resourceRequirements rr INNER JOIN rr.resourceOffers ro INNER JOIN ro.organization o " +
            "WHERE p.id = :projectId")
    public List<Organization> findOrganizationsByResourceRequirements(
            @Param("projectId") Long projectId
    );
}
