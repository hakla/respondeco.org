package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;

/**
 * Created by Benjamin Fraller on 08.12.2014.
 */
@Data
public class ClaimResourceResponseDTO {

    Long matchId;

    ProjectResponseDTO project;

    ResourceOfferDTO resourceOffer;

    ResourceRequirementResponseDTO resourceRequirement;

}
