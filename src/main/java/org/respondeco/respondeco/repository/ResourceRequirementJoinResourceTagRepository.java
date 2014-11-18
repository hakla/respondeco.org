package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceRequirementJoinResourceTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Spring Data JPA repository for the ResourceRequirementJoinResourceTag entity.
 */
public interface ResourceRequirementJoinResourceTagRepository extends JpaRepository<ResourceRequirementJoinResourceTag, Long> {

    List<ResourceRequirementJoinResourceTag> findByResourceRequirementId(Long requirementId);
    List<ResourceRequirementJoinResourceTag> findByResourceRequirementIdAndResourceTagId(Long requirementId, Long resourceTagId);
    @Modifying
    @Transactional
    @Query("delete from T_RESOURCEREQUIREMENTJOINRESOURCETAG u where u.resource_requirement_id = ?1")
    void deleteByRequirementId(Long requirementId);

}
