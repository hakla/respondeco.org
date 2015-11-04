package org.respondeco.respondeco.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.TransactionUsageException;

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
    @Query("SELECT e FROM #{#entityName} e WHERE e.active = TRUE")
    public List<T> findAll();

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.active = TRUE")
    public Page<T> findAll(Pageable pageable);

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.active = TRUE")
    public List<T> findAll(Sort sort);

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.id IN :ids AND e.active = TRUE")
    public List<T> findAll(@Param("ids") Iterable<ID> ids);

    @Query("SELECT e FROM #{#entityName} e")
    public List<T> findAllIgnoreActive();

    @Query("SELECT e FROM #{#entityName} e")
    public Page<T> findAllIgnoreActive(Pageable pageable);

    @Query("SELECT e FROM #{#entityName} e WHERE e.active = TRUE")
    public List<T> findAllIgnoreActive(Sort sort);

    @Query("SELECT e FROM #{#entityName} e WHERE e.id IN :ids AND e.active = TRUE")
    public List<T> findAllIgnoreActive(@Param("ids") Iterable<ID> ids);

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.active = TRUE")
    public T findOne(@Param("id") ID id);

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id")
    public T findOneIgnoreActive(@Param("id") ID id);

    @Override
    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id AND e.active = TRUE")
    public T getOne(@Param("id") ID id);

    @Query("SELECT e FROM #{#entityName} e WHERE e.id = :id")
    public T getOneIgnoreActive(@Param("id") ID id);

    @Override
    @Modifying(clearAutomatically = true)
    @Query("UPDATE #{#entityName} e SET e.active = false WHERE e = :entity")
    public void delete(@Param("entity") T entity);

    @Override
    @Modifying(clearAutomatically = true)
    @Query("UPDATE #{#entityName} e SET e.active = false WHERE e.id = :id")
    public void delete(@Param("id") ID id);

    @Override
    @Modifying(clearAutomatically = true)
    @Query("UPDATE #{#entityName} e SET e.active = false WHERE e IN :entities")
    public void delete(@Param("entities") Iterable<? extends T> entities);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM #{#entityName} e WHERE e = :entity")
    public void deletePermanently(@Param("entity") T entity);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM #{#entityName} e WHERE e.id = :id")
    public void deletePermanently(@Param("id") ID id);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM #{#entityName} e WHERE e IN :entities")
    public void deletePermanently(@Param("entities") Iterable<? extends T> entities);

}
