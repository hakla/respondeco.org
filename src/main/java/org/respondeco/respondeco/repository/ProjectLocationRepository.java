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

    @Query(value = "SELECT id, lat, lng, address, project_id, is_active, ( 6371 * acos( cos( radians(?1) ) * cos( radians( lat ) ) " +
        " * cos( radians( lng ) - radians(?2) ) + sin( radians(?1) ) * sin(radians(lat)) ) ) AS distance " +
        "FROM T_Project_Location " +
        "WHERE is_active = true " +
        "GROUP BY id, lat, lng, address, project_id " +
        "HAVING distance < ?3 " +
        "ORDER BY distance " +
        "LIMIT 0,20",nativeQuery = true)
    public List<Object[]> findNearProjects(double userLat, double userLng, double radius);

}
