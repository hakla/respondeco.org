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

    @Query("SELECT count(pr), AVG(pr.rating) " +
            "FROM Project p INNER JOIN p.resourceMatches rm INNER JOIN rm.projectRating pr " +
            "WHERE rm.accepted = true AND p.id = :projectid")
    public Object[][] getAggregatedRatingByProject(@Param("projectid") Long id);

    @Query("SELECT count(sr), AVG(sr.rating) " +
            "FROM Organization o INNER JOIN o.resourceMatches rm INNER JOIN rm.supporterRating sr " +
            "WHERE rm.accepted = true AND o.id = :organizationid")
    public Object[][] getAggregatedRatingByOrganization(@Param("organizationid") Long id);
}
