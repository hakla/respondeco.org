package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceOfferJoinResourceTag;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceOfferJoinResourceTag entity.
 */
@Transactional
public interface ResourceOfferJoinResourceTagRepository extends AbstractEntityRepository<ResourceOfferJoinResourceTag, Long> {
    List<ResourceOfferJoinResourceTag> findByResourceOfferId(Long resourceOfferId);
    @Query("SELECT COUNT(*) FROM #{#entityName} WHERE resource_offer_id = ?1 AND resource_tag_id = ?2")
    Long countByResourceOfferIdAndResourceTagId(Long resourceOfferId, Long resourceTagId);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} SET is_active = 0 where resource_offer_id = ?1")
    void deleteByOfferId(Long offerId);
}
