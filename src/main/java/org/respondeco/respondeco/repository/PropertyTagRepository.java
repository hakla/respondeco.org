package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.PropertyTag;
        import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the PropertyTag entity.
 */
public interface PropertyTagRepository extends JpaRepository<PropertyTag, Long> {

}
