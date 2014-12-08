package org.respondeco.respondeco.web.rest.dto;

import lombok.*;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.web.rest.util.RestParameters;

/**
 * Created by Chris on 10.11.2014.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgJoinRequestDTO {

    private Long id;
    private OrganizationResponseDTO organization;
    private UserDTO user;

    public OrgJoinRequestDTO(OrgJoinRequest orgJoinRequest) {
        id = orgJoinRequest.getId();
        Organization organizationEntity = orgJoinRequest.getOrganization();
        organization = OrganizationResponseDTO
                .fromEntity(organizationEntity, null);
        user = new UserDTO(orgJoinRequest.getUser());
    }

}
