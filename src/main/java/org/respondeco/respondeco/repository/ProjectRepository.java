package org.respondeco.respondeco.repository;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Temporal;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import javax.persistence.TemporalType;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the Project entity.
 */
public interface ProjectRepository extends AbstractEntityRepository<Project, Long>, QueryDslPredicateExecutor {

    public Page<Project> findByActiveIsTrue(Pageable pageable);

    public List<Project> findByManager(User user);

    @Query("SELECT DISTINCT p " +
            "FROM Project p LEFT OUTER JOIN p.propertyTags pt " +
            "WHERE (pt.name in :tags OR UPPER(p.name) LIKE UPPER(:name)) " +
            "AND p.active = 1")
    public Page<Project> findByNameAndTags(
            @Param("name") String name,
            @Param("tags") Collection<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT p " +
            "FROM Project p LEFT OUTER JOIN p.propertyTags pt " +
            "INNER JOIN p.organization o " +
            "WHERE (o.id = :orgId AND (pt.name in :tags OR UPPER(p.name) LIKE UPPER(:name))) " +
            "AND p.active = 1")
    public Page<Project> findByOrganizationAndNameAndTags(
            @Param("orgId") Long orgId,
            @Param("name")  String name,
            @Param("tags")  Collection<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT p " +
        "FROM Project p " +
        "INNER JOIN p.organization o " +
        "WHERE o.id = :orgId " +
        "AND p.active = 1")
    public Page<Project> findByOrganization(
        @Param("orgId") Long orgId, Pageable pageable);


    @Query("SELECT DISTINCT p " +
            "FROM Project p LEFT OUTER JOIN p.propertyTags pt " +
            "WHERE pt.name in :tags " +
            "AND p.active = 1")
    public Page<Project> findByTags(
            @Param("tags") Collection<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT p " +
            "FROM Project p LEFT OUTER JOIN p.propertyTags pt " +
            "INNER JOIN p.organization o " +
            "WHERE (o.id = :orgId AND pt.name in :tags) " +
            "AND p.active = 1")
    public Page<Project> findByOrganizationAndTags(
            @Param("orgId") Long orgId,
            @Param("tags")  Collection<String> tags, Pageable pageable);

    public List<Project> findByStartDate(LocalDate date);

    @Query ("SELECT p FROM Project p INNER JOIN p.followingUsers u" +
            " WHERE u.id = :user_id AND p.id = :project_id AND u.active = 1 AND p.active = 1"
    )
    public Project findByUserIdAndProjectId(
        @Param("user_id") Long user_id,
        @Param("project_id") Long project_id
    );

    @Query("SELECT p.id FROM Project p WHERE p.organization.id = :organizationId AND p.active = 1 AND p.organization.active = 1")
    public List<Long> findByOrganizationId(@Param("organizationId") Long organizationId);

}
