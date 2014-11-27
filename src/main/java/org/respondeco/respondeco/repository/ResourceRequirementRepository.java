package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceRequirement entity.
 */
@Transactional
public interface ResourceRequirementRepository extends JpaRepository<ResourceRequirement, Long> {

    List<ResourceRequirement> findByNameAndProjectId(String name, Long projectId);
    List<ResourceRequirement> findByProjectId(Long projectId);
}
