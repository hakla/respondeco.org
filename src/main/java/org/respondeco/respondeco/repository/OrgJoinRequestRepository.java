package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the OrgJoinRequest entity.
 */
public interface OrgJoinRequestRepository extends JpaRepository<OrgJoinRequest, Long> {
    List<OrgJoinRequest> findByOrganization(Organization organization);

    List<OrgJoinRequest> findByUser(User user);
}
