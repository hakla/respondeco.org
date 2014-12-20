package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Posting;
import org.respondeco.respondeco.domain.PostingFeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the PostingFeed entity.
 */
public interface PostingFeedRepository extends JpaRepository<PostingFeed, Long> {

    @Query("SELECT p " +
            "FROM Organization o INNER JOIN o.postingFeed pf INNER JOIN pf.postings p " +
            "WHERE o.id = :organizationid " +
            "ORDER BY p.createdDate ASC ")
    public List<Posting> getPostingsForOrganization(@Param("organizationid") Long id);

    @Query("SELECT p " +
            "FROM Project po INNER JOIN po.postingFeed pf INNER JOIN pf.postings p " +
            "WHERE po.id = :projectid " +
            "ORDER BY p.createdDate ASC ")
    public List<Posting> getPostingsForProject(@Param("projectid") Long id);
}
