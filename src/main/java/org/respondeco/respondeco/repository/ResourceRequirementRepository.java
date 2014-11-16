package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Roman Kern on 15.11.14.
 */
public interface ResourceRequirementRepository extends JpaRepository<ResourceRequirement, Long> {
}
