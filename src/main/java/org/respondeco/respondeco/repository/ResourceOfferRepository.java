package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceOffer entity.
 */
@Transactional
public interface ResourceOfferRepository extends JpaRepository<ResourceOffer, Long> {

    List<ResourceOffer> findByNameAndOrganisationId(String name, Long organisationId);
    List<ResourceOffer> findByOrganisationId(Long organizationId);
}
