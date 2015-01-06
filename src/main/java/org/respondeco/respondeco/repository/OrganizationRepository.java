package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
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
    List<Organization> findByActiveIsTrue();
    Organization findByIdAndActiveIsTrue(Long id);


    @Query ("SELECT p FROM Organization p INNER JOIN p.FollowingUsers u" +
        " WHERE u.id = :user_id AND p.id = :organization_id AND u.active = 1 AND p.active = 1"
    )
    public Organization findByUserIdAndOrganizationId(
        @Param("user_id") Long user_id,
        @Param("organization_id") Long organization_id
    );
}
