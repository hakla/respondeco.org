package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ProjectLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

/**
 * Spring Data JPA repository for Project Location
 */
public interface ProjectLocationRepository extends JpaRepository<ProjectLocation, Long>, QueryDslPredicateExecutor {

    public ProjectLocation findByProjectId(Long projectId);

    public List<ProjectLocation> findByActiveIsTrue();

    @Query(value = "SELECT id, lat, lng, address, project_id, ( 6371 * acos( cos( radians(48.17) ) * cos( radians( lat ) ) " +
        " * cos( radians( lng ) - radians(16.32) ) + sin( radians(48.17) ) * sin(radians(lat)) ) ) AS distance " +
        "FROM T_Project_Location " +
        "HAVING distance < 100 " +
        "ORDER BY distance " +
        "LIMIT 0,20",nativeQuery = true)
    public List<Object[]> findNearProjects();

}
