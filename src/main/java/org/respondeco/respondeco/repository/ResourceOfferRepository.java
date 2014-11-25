package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceOffer entity.
 */
public interface ResourceOfferRepository extends JpaRepository<ResourceOffer, Long> {

    List<ResourceOffer> findByDescriptionAndOrganisationId(String description, Long organisationId);
    List<ResourceOffer> findByOrganisationId(Long organisationId);
}
