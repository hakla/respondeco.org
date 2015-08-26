package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceRequirement entity.
 */
@Transactional
public interface ResourceRequirementRepository extends AbstractEntityRepository<ResourceRequirement, Long> {

    List<ResourceRequirement> findByNameAndProjectAndActiveIsTrue(String name, Project project);
    List<ResourceRequirement> findByProjectIdAndActiveIsTrue(Long projectId);
    ResourceRequirement findByIdAndActiveIsTrue(Long id);
    List<ResourceRequirement> findByActiveIsTrue();
}
