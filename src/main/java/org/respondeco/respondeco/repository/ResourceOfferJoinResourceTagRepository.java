package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceOfferJoinResourceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Spring Data JPA repository for the ResourceOfferJoinResourceTag entity.
 */
public interface ResourceOfferJoinResourceTagRepository extends JpaRepository<ResourceOfferJoinResourceTag, Long> {
    List<ResourceOfferJoinResourceTag> findByResourceOfferId(Long resourceOfferId);
    @Query("SELECT COUNT(*) FROM #{#entityName} WHERE resource_offer_id = ?1 AND resource_tag_id = ?2")
    Long countByResourceOfferIdAndResourceTagIdCount(Long resourceOfferId, Long resourceTagId);
    @Modifying
    @Transactional
    @Query("delete from #{#entityName} where resource_offer_id = ?1")
    void deleteByOfferId(Long offerId);
}
