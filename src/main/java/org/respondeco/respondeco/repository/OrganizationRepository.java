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
public interface OrganizationRepository extends AbstractEntityRepository<Organization, Long> {

    @Query("SELECT o FROM Organization o WHERE o.owner = :owner AND o.active = TRUE")
    Organization findByOwner(@Param("owner") User owner);

    @Query("SELECT o FROM Organization o WHERE o.name = :name AND o.active = TRUE")
    Organization findByName(@Param("name") String orgName);

    @Query(
        "SELECT o " +
        "FROM Organization o INNER JOIN o.FollowingUsers u " +
            "WHERE u = :user " +
            "AND o.id = :organizationId " +
            "AND u.active = 1 " +
            "AND o.active = 1"
    )
    public Organization findOrganizationIfUserFollows(
        @Param("user") User user,
        @Param("organizationId") Long organizationId
    );
}
