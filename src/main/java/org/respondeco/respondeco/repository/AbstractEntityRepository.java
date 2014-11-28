package org.respondeco.respondeco.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Roman Kern on 23.11.14.
 * Use this to override delete to UPDATE SET is_active = 0
 */
@NoRepositoryBean
public interface AbstractEntityRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    @Override
    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} SET is_active = 0 WHERE id = :id")
    void delete(@Param("id") ID var1);
    /*
    @Override
    @Query("SELECT e.* FROM #{#entityName} e WHERE e.is_active = 1")
    List<T> findAll();
    */
}
