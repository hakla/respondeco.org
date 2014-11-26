package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceRequirementJoinResourceTag;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceRequirementJoinResourceTag entity.
 */
@Transactional
public interface ResourceRequirementJoinResourceTagRepository extends AbstractEntityRepository<ResourceRequirementJoinResourceTag, Long> {

    List<ResourceRequirementJoinResourceTag> findByResourceRequirementId(Long requirementId);
    @Query("SELECT COUNT(id) FROM #{#entityName} WHERE resource_requirement_id = ?1 AND resource_tag_id = ?2")
    Long countByResourceRequirementIdAndResourceTagId(Long requirementId, Long resourceTagId);
    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} SET is_active = 0 where resource_requirement_id = ?1")
    void deleteByRequirementId(Long requirementId);

}
