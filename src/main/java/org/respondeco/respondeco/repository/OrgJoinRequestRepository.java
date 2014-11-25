package org.respondeco.respondeco.repository;

import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestWithActiveFlagDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the OrgJoinRequest entity.
 */
public interface OrgJoinRequestRepository extends JpaRepository<OrgJoinRequest, Long> {
    List<OrgJoinRequest> findByOrgIdAndActiveIsTrue(Long orgId);
    List<OrgJoinRequest> findByUserIdAndActiveIsTrue(Long userId);
    OrgJoinRequest findByIdAndActiveIsTrue(Long id);
    List<OrgJoinRequest> findByOrgId(Long orgId);
}
