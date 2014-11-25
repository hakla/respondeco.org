package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceRequirement entity.
 */
public interface ResourceRequirementRepository extends JpaRepository<ResourceRequirement, Long> {

    List<ResourceRequirement> findByDescriptionAndProjectId(String description, Long projectId);
    List<ResourceRequirement> findByProjectId(Long projectId);
}
