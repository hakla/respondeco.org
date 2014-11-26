package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceOfferJoinResourceRequirement;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data JPA repository for the ResourceOfferJoinResourceRequirement entity.
 */
@Transactional
public interface ResourceOfferJoinResourceRequirementRepository extends AbstractEntityRepository<ResourceOfferJoinResourceRequirement, Long> {

}
