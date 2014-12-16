package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestWithActiveFlagDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the OrgJoinRequest entity.
 */
public interface OrgJoinRequestRepository extends JpaRepository<OrgJoinRequest, Long> {
    List<OrgJoinRequest> findByOrganizationAndActiveIsTrue(Organization organization);
    List<OrgJoinRequest> findByUserAndActiveIsTrue(User user);
    OrgJoinRequest findByIdAndActiveIsTrue(Long id);
    OrgJoinRequest findByUserAndOrganizationAndActiveIsTrue(User user, Organization organization);
}
