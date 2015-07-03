package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.ISOCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by clemens on 23/06/15.
 */
public interface ISOCategoryRepository extends JpaRepository<ISOCategory, Long> {

    public Page<ISOCategory> findBySuperCategoryIsNull(Pageable pageable);

    public Page<ISOCategory> findBySuperCategory(ISOCategory isoCategory, Pageable pageable);

}
