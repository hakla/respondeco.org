package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ResourceTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Roman Kern on 15.11.14.
 */
public interface ResourceTagRepository extends JpaRepository<ResourceTag, Long> {

    List<ResourceTag> findByName(String name);
}
