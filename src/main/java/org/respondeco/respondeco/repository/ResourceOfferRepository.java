package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceOffer;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceOffer entity.
 */
@Transactional
public interface ResourceOfferRepository extends AbstractEntityRepository<ResourceOffer, Long> {

    List<ResourceOffer> findByNameAndOrganisationId(String name, Long organisationId);
    List<ResourceOffer> findByOrganisationId(Long organisationId);
}
