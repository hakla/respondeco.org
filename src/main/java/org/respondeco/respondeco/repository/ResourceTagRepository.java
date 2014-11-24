package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceTag;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceTag entity.
 */
@Transactional
public interface ResourceTagRepository extends AbstractEntityRepository<ResourceTag, Long> {
    List<ResourceTag> findByName(String name);
}
