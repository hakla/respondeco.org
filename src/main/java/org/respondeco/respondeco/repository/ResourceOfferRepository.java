package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceOffer entity.
 */
@Transactional
public interface ResourceOfferRepository extends AbstractEntityRepository<ResourceOffer, Long>, QueryDslPredicateExecutor {
    List<ResourceOffer> findByNameAndActiveIsTrue(String name, Pageable pageable);
    Page<ResourceOffer> findByActiveIsTrue(Pageable pageable);
    List<ResourceOffer> findByOrganizationIdAndActiveIsTrue(Long id);
    Page<ResourceOffer> findByOrganizationNotAndActiveIsTrue(Organization organization, Pageable pageable);
}
