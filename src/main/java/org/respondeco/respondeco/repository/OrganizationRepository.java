package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.Organization;
        import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA repository for the Organization entity.
 */
public interface OrganizationRepository extends JpaRepository<Organization, String> {

    Organization findByOwner(String owner);
}
