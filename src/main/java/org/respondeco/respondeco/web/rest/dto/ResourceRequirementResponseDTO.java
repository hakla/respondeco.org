package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ResourceOffer;
import org.respondeco.respondeco.domain.ResourceRequirement;
import org.respondeco.respondeco.domain.ResourceTag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Clemens Puehringer on 04/12/14.
 */

@Data
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ResourceRequirementResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
        "id", "name", "amount", "description", "isEssential",
        "resourceTags");

    public static ResourceRequirementResponseDTO fromEntity(ResourceRequirement resourceRequirement, List<String> fieldNames) {
        ResourceRequirementResponseDTO responseDTO = new ResourceRequirementResponseDTO();

            if(fieldNames == null || fieldNames.size() == 0) {
                fieldNames = DEFAULT_FIELDS;
            }
            if (fieldNames.contains("id")) {
                responseDTO.setId(resourceRequirement.getId());
            }
            if (fieldNames.contains("name")) {
                responseDTO.setName(resourceRequirement.getName());
            }
            if (fieldNames.contains("amount")) {
                responseDTO.setAmount(resourceRequirement.getAmount());
            }
            if (fieldNames.contains("description")) {
                responseDTO.setDescription(resourceRequirement.getDescription());
            }
            if (fieldNames.contains("project")) {
                responseDTO.setProjectResponseDTO(ProjectResponseDTO
                    .fromEntity(resourceRequirement.getProject(), null));
            }
            if (fieldNames.contains("projectId")) {
                responseDTO.setProjectId(resourceRequirement.getProject().getId());
            }
            if (fieldNames.contains("isEssential")) {
                responseDTO.setIsEssential(resourceRequirement.getIsEssential());
            }
            if (fieldNames.contains("resourceTags")) {
                responseDTO.setResourceTags(ResourceTagResponseDTO
                    .fromEntities(resourceRequirement.getResourceTags(), null));
            }

            return responseDTO;
        }


    public static List<ResourceRequirementResponseDTO> fromEntities(List<ResourceRequirement> resourceRequirements,
                                                                  List<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<ResourceRequirementResponseDTO> responseDTOs = new ArrayList<>();
        for(ResourceRequirement resourceRequirement : resourceRequirements) {
            responseDTOs.add(ResourceRequirementResponseDTO.fromEntity(resourceRequirement, fieldNames));
        }
        return responseDTOs;
    }

    private Long id;
    private Long projectId;
    private ProjectResponseDTO projectResponseDTO;
    private String name;
    private BigDecimal amount;
    private String description;
    private Boolean isEssential;
    private List<ResourceTagResponseDTO> resourceTags;

}
