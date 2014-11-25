package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceTag entity.
 */
public interface ResourceTagRepository extends JpaRepository<ResourceTag, Long> {
    List<ResourceTag> findByName(String name);
}
