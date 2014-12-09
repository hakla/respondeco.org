package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.AggregatedRating;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ResourceMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by clemens on 08/12/14.
 */

public interface ResourceMatchRepository extends JpaRepository<ResourceMatch, Long> {

    public ResourceMatch findByProjectAndOrganization(Project project, Organization organization);
/*
    @Query("SELECT COUNT(rm) AS count " +
                    "FROM Project p INNER JOIN p.resourceMatches rm " +
                    "WHERE p.id = :projectid")
*/
    @Query("SELECT rm.projectRating.id " +
            "FROM Project p INNER JOIN p.resourceMatches rm " +
            "WHERE p.id = :projectid")
    public Object[] getAggregatedRatingByProject(@Param("projectid") Long id);

    @Query("SELECT COUNT(r) AS count, AVG(r.rating) AS rating " +
            "FROM Rating r " +
            "WHERE r IN (SELECT rm.supporterRating " +
            "FROM ResourceMatch rm " +
            "WHERE rm.organization.id = :organizationid)")
    public AggregatedRating getAggregatedRatingByOrganization(@Param("organizationid") Long id);
}
