package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceOffer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Roman Kern on 15.11.14.
 */
public interface ResourceOfferRepository extends JpaRepository<ResourceOffer, Long> {

    List<ResourceOffer> findByDescriptionAndOrganisationId(String name, Long organisationId);
}
