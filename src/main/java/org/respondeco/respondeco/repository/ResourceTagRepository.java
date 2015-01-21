package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.domain.ResourceTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Spring Data JPA repository for the ResourceTag entity.
 */
@Transactional
public interface ResourceTagRepository extends JpaRepository<ResourceTag, Long> {

    public List<ResourceTag> findByNameContainingIgnoreCase(String filter, Pageable pageable);

    public ResourceTag findByName(String name);

}
