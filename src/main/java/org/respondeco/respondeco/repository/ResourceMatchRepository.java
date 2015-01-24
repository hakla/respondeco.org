package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.AggregatedRating;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ResourceMatch;
import org.respondeco.respondeco.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by clemens on 08/12/14.
 */

public interface ResourceMatchRepository extends JpaRepository<ResourceMatch, Long>, QueryDslPredicateExecutor {

    /**
     * finds a list of resourcematches for the given organization and given project
     * @param project id of the project for which matches are searched
     * @param organization id of the organization for which matches are searched
     * @return a list of resourcematches for the organization and project from the repository
     */
    public List<ResourceMatch> findByProjectAndOrganization(Project project, Organization organization);

    /**
     * finds a list of resourcematches for the given project which are accepted (exchanged) and active (not deleted)
     * @param id given id of the project
     * @return a list of resourcematches for the project which are accepted and active
     */
    public List<ResourceMatch> findByProjectIdAndAcceptedIsTrueAndActiveIsTrue(Long id);

    /**
     * method to get an aggregated rating for the project when there was already an exchange of resources
     * @param id given id of the project
     * @return object array for all projectratings of all resourcematches where the project is in;
     * first array value is the count of the ratings; second array value is the average rating value
     */
    @Query("SELECT count(pr), AVG(pr.rating) " +
            "FROM Project p INNER JOIN p.resourceMatches rm INNER JOIN rm.projectRating pr " +
            "WHERE rm.accepted = true AND p.id = :projectid")
    public Object[][] getAggregatedRatingByProject(@Param("projectid") Long id);

    /**
     * method to get an aggregated rating for the organization when there was already an exchange of resources
     * @param id given id of the organization
     * @return object array for all supporterratings of all resourcematches where the organization is in;
     * first array value is the count of the ratings; second array value is the average rating value
     */
    @Query("SELECT count(sr), AVG(sr.rating) " +
            "FROM Organization o INNER JOIN o.resourceMatches rm INNER JOIN rm.supporterRating sr " +
            "WHERE rm.accepted = true AND o.id = :organizationid")
    public Object[][] getAggregatedRatingByOrganization(@Param("organizationid") Long id);

    /**
     * method to find a list of resourcematches for the given offers, requirements, organization and project
     * @param resourceOffer given resourceoffer
     * @param resourceRequirement given resourcerequirement
     * @param organization given organization
     * @param project given project
     * @return a list of found resourcematches
     */
    public List<ResourceMatch> findByResourceOfferAndResourceRequirementAndOrganizationAndProjectAndActiveIsTrue(
        ResourceOffer resourceOffer, ResourceRequirement resourceRequirement, Organization organization, Project project);

    /**
     * Find all ResourceMatches containing the donated resources of an organization
     * @param organization organization for which the donated resources should be returned
     * @param pageable contains page information (page size, nr of page)
     * @return Page of ResourceMatches
     */
    public Page<ResourceMatch> findByOrganizationAndAcceptedIsTrueAndActiveIsTrue(Organization organization, Pageable pageable);
}
