package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.ResourceMatch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Benjamin Fraller on 08.12.2014.
 */
@Data
public class ResourceMatchResponseDTO {

    Long matchId;

    Boolean accepted;

    RatingResponseDTO projectRating;

    RatingResponseDTO supporterRating;

//    ProjectResponseDTO project;

    String matchDirection;

    ResourceOfferDTO resourceOffer;

    ResourceRequirementResponseDTO resourceRequirement;

    OrganizationResponseDTO organization;

    BigDecimal amount;

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
        "matchId", "project", "organization", "resourceOffer","resourceRequirement", "projectRating", "supporterRating", "accepted", "amount", "matchDirection");

    public static ResourceMatchResponseDTO fromEntity(ResourceMatch match, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        ResourceMatchResponseDTO responseDTO = new ResourceMatchResponseDTO();
        if (fieldNames.contains("matchId")) {
            responseDTO.setMatchId(match.getId());
        }
//        if (fieldNames.contains("project")) {
//            responseDTO.setProject(ProjectResponseDTO.fromEntity(match.getProject(), null));
//        }
        if (fieldNames.contains("organization")) {
            responseDTO.setOrganization(OrganizationResponseDTO.fromEntity(match.getOrganization(), null));
        }
        if (fieldNames.contains("resourceOffer")) {
            responseDTO.setResourceOffer(new ResourceOfferDTO(match.getResourceOffer()));
        }
        if (fieldNames.contains("resourceRequirement")) {
            responseDTO.setResourceRequirement(ResourceRequirementResponseDTO.fromEntity(match.getResourceRequirement(), null));
        }
        if (fieldNames.contains("projectRating") && match.getProjectRating() != null) {
            responseDTO.setProjectRating(RatingResponseDTO.fromEntity(match.getProjectRating(), null));
        }
        if (fieldNames.contains("supporterRating")&& match.getSupporterRating() != null) {
            responseDTO.setSupporterRating(RatingResponseDTO.fromEntity(match.getSupporterRating(), null));
        }
        if (fieldNames.contains("accepted")) {
            responseDTO.setAccepted(match.getAccepted());
        }

        if( fieldNames.contains("amount")) {
            responseDTO.setAmount(match.getAmount());
        }

        if(fieldNames.contains("matchDirection") == true && match.getMatchDirection() != null){
            responseDTO.setMatchDirection(match.getMatchDirection().toString());
        }

        return responseDTO;
    }

    public static List<ResourceMatchResponseDTO> fromEntities(Collection<ResourceMatch> matches, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<ResourceMatchResponseDTO> responseDTOs = new ArrayList<>();
        for(ResourceMatch match : matches) {
            responseDTOs.add(ResourceMatchResponseDTO.fromEntity(match, fieldNames));
        }
        return responseDTOs;
    }

}
