package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Organization entity.
 */
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Organization findByOwner(User owner);
    Organization findByName(String orgName);
    Page<Organization> findByActiveIsTrue(Pageable pageable);
    Organization findByIdAndActiveIsTrue(Long id);


    @Query(
        "SELECT p " +
        "FROM Organization p INNER JOIN p.FollowingUsers u " +
            "WHERE u.id = :userId " +
            "AND p.id = :organizationId " +
            "AND u.active = 1 " +
            "AND p.active = 1"
    )
    public Organization findByUserIdAndOrganizationId(
        @Param("userId") Long userId,
        @Param("organizationId") Long organizationId
    );
}
