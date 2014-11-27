package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.PropertyTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the Project entity.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {

    public List<Project> findByActiveIsTrue(Pageable pageable);

    public Project findByIdAndActiveIsTrue(Long id);

    @Query("SELECT DISTINCT p " +
            "FROM Project p LEFT OUTER JOIN p.propertyTags pt " +
            "WHERE (pt.name in :tags OR p.name LIKE %:name%) " +
            "AND p.active = 1")
    public List<Project> findByNameAndTags(
            @Param("name") String name,
            @Param("tags") Collection<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT p " +
            "FROM Project p LEFT OUTER JOIN p.propertyTags pt " +
            "INNER JOIN p.organization o " +
            "WHERE (o.id = :orgId AND (pt.name in :tags OR p.name LIKE %:name%)) " +
            "AND p.active = 1")
    public List<Project> findByOrganizationAndNameAndTags(
            @Param("orgId") Long orgId,
            @Param("name")  String name,
            @Param("tags")  Collection<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT p " +
            "FROM Project p LEFT OUTER JOIN p.propertyTags pt " +
            "WHERE pt.name in :tags " +
            "AND p.active = 1")
    public List<Project> findByTags(
            @Param("tags") Collection<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT p " +
            "FROM Project p LEFT OUTER JOIN p.propertyTags pt " +
            "INNER JOIN p.organization o " +
            "WHERE (o.id = :orgId AND pt.name in :tags) " +
            "AND p.active = 1")
    public List<Project> findByOrganizationAndTags(
            @Param("orgId") Long orgId,
            @Param("tags")  Collection<String> tags, Pageable pageable);

}
