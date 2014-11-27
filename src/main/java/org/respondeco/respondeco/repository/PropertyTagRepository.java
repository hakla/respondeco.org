package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.PropertyTag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the PropertyTag entity.
 */
public interface PropertyTagRepository extends JpaRepository<PropertyTag, Long> {

    @Query("SELECT pt FROM PropertyTag pt WHERE pt.name LIKE %?1%")
    public List<PropertyTag> findWhereNameLike(String filter, Pageable pageable);

    public PropertyTag findByName(String name);

}
