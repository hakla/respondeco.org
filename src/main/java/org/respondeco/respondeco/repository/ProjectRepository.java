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

    public List<Project> findByActiveIsTrue();

    public Project findByIdAndActiveIsTrue(Long id);

    @Query("select p.name from Project p where p.name like %:name%")
    List<String> findProjectNamesLike(@Param("name") String name, Pageable pageable);

    @Query("SELECT DISTINCT p " +
            "FROM Project p LEFT OUTER JOIN p.propertyTags pt " +
            "WHERE pt.name in :tags OR p.name LIKE %:name%")
    public List<Project> findByNameAndTags(
            @Param("name") String name,
            @Param("tags") Collection<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT p " +
            "FROM Project p LEFT OUTER JOIN p.propertyTags pt " +
            "WHERE pt.name in :tags")
    public List<Project> findByTags(
            @Param("tags") Collection<String> tags, Pageable pageable);

}
