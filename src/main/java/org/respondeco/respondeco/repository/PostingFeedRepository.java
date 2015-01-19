package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Posting;
import org.respondeco.respondeco.domain.PostingFeed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the PostingFeed entity.
 */
public interface PostingFeedRepository extends JpaRepository<PostingFeed, Long> {

    /**
     * Query used for postingfeed; returns active postings for organization from the repository
     * ordered by creation date (newest posting first)
     * @param id id of organization for which postings are searched
     * @param pageable pagerequest given from service
     * @return page of postings found for given organization
     */
    @Query("SELECT p " +
        "FROM Organization o INNER JOIN o.postingFeed pf INNER JOIN pf.postings p " +
        "WHERE o.id = :organizationid AND p.active = true " +
        "ORDER BY p.createdDate DESC ")
    public Page<Posting> getPostingsForOrganization(@Param("organizationid") Long id, Pageable pageable);

    /**
     * Query used for newsfeed; returns active postings for organization and projects from the repository
     * ordered by creation date (newest posting first)
     * This query only works if the organizationIds and the projectIds both contain at least one entry
     * @param organizationIds list of organization ids for which postings are searched
     * @param projectIds list of project ids for which postings are searched
     * @param pageable pagerequest given from service
     * @return page of postings found for given organizations and projects
     */
    @Query("SELECT DISTINCT p " +
        "FROM Organization o, Project po, PostingFeed pf, Posting p " +
        "WHERE o.id IN (:organizations) AND po.id IN (:projects) " +
        "AND (o.postingFeed = pf OR po.postingFeed = pf) " +
        "AND p.active = true AND p.postingfeed = pf " +
        "ORDER BY p.createdDate DESC ")
    public Page<Posting> getPostingsForOrganizationsAndProjects(
        @Param("organizations") List<Long> organizationIds,
        @Param("projects") List<Long> projectIds,
        Pageable pageable);

    /**
     * Query used for newsfeed; returns active postings for list of organizations from the repository
     * ordered by creation date (newest posting first)
     * This query only works if the organizationIds contain at least one entry
     * @param organizationIds list of organization ids for which postings are searched
     * @param pageable pagerequest given from service
     * @return page of postings found for given organization
     */
    @Query("SELECT p " +
        "FROM Organization o INNER JOIN o.postingFeed pf INNER JOIN pf.postings p " +
        "WHERE o.id IN (:organizations) AND p.active = true " +
        "ORDER BY p.createdDate DESC ")
    public Page<Posting> getPostingsForOrganizations(@Param("organizations") List<Long> organizationIds, Pageable pageable);

    /**
     * Query used for newsfeed; returns active postings for list of projects from the repository
     * ordered by creation date (newest posting first)
     * This query only works if the projectIds contain at least one entry
     * @param projectIds list of project ids for which postings are searched
     * @param pageable pagerequest given from service
     * @return page of postings found for given organization
     */
    @Query("SELECT p " +
        "FROM Project po INNER JOIN po.postingFeed pf INNER JOIN pf.postings p " +
        "WHERE po.id IN (:projects) AND p.active = true " +
        "ORDER BY p.createdDate DESC ")
    public Page<Posting> getPostingsForProjects(@Param("projects") List<Long> projectIds, Pageable pageable);

    /**
     * Query used for postingfeed; returns active postings for project from the repository
     * ordered by creation date (newest posting first)
     * @param id id of project for which postings are searched
     * @param pageable pagerequest given from service
     * @return page of postings found for given project
     */
    @Query("SELECT p " +
            "FROM Project po INNER JOIN po.postingFeed pf INNER JOIN pf.postings p " +
            "WHERE po.id = :projectid AND p.active = true " +
            "ORDER BY p.createdDate DESC ")
    public Page<Posting> getPostingsForProject(@Param("projectid") Long id, Pageable pageable);
}
