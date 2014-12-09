package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.domain.ResourceRequirement;

/**
 * Created by Benjamin Fraller on 08.12.2014.
 */
@Data
public class ClaimResourceDTO {

    private Long resourceOfferId;

    private Long resourceRequirementId;

    private Long organizationId;

    private Long projectId;

    private boolean accepted;

}
