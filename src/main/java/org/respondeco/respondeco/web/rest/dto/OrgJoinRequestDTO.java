package org.respondeco.respondeco.web.rest.dto;

import lombok.*;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.web.rest.util.RestParameters;

import javax.validation.constraints.NotNull;

/**
 * Created by Chris on 10.11.2014.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgJoinRequestDTO {

    private Long id;

    @NotNull
    private OrganizationResponseDTO organization;

    @NotNull
    private UserDTO user;

    public OrgJoinRequestDTO(OrgJoinRequest orgJoinRequest) {
        id = orgJoinRequest.getId();
        Organization organizationEntity = orgJoinRequest.getOrganization();
        organization = OrganizationResponseDTO
                .fromEntity(organizationEntity, null);
        user = new UserDTO(orgJoinRequest.getUser());
    }

}
