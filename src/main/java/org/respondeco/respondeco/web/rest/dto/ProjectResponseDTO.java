package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.domain.util.CustomLocalDateSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by clemens on 25/11/14.
 */

@Data
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ProjectResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
            "id", "name", "purpose", "concrete", "startDate", "endDate",
            "organizationId", "managerId", "propertyTags");

    public static List<ProjectResponseDTO> fromEntity(List<Project> projects, List<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<ProjectResponseDTO> responseDTOs = new ArrayList<>();
        ProjectResponseDTO responseDTO;
        for(Project project : projects) {
            responseDTO = new ProjectResponseDTO();
            if (fieldNames.contains("id")) {
                responseDTO.setId(project.getId());
            }
            if (fieldNames.contains("name")) {
                responseDTO.setName(project.getName());
            }
            if (fieldNames.contains("purpose")) {
                responseDTO.setPurpose(project.getPurpose());
            }
            if (fieldNames.contains("concrete")) {
                responseDTO.setConcrete(project.isConcrete());
            }
            if (fieldNames.contains("startDate")) {
                responseDTO.setStartDate(project.getStartDate());
            }
            if (fieldNames.contains("endDate")) {
                responseDTO.setEndDate(project.getEndDate());
            }
            if (fieldNames.contains("organization")) {
                responseDTO.setOrganization(project.getOrganization());
            }
            if (fieldNames.contains("organizationId")) {
                responseDTO.setOrganizationId(project.getOrganization().getId());
            }
            if (fieldNames.contains("manager")) {
                responseDTO.setManager(project.getManager());
            }
            if (fieldNames.contains("managerId")) {
                responseDTO.setManagerId(project.getManager().getId());
            }
            if (fieldNames.contains("propertyTags")) {
                responseDTO.setPropertyTags(PropertyTagResponseDTO.fromEntity(project.getPropertyTags(), Arrays.asList("name")));
            }
            if (fieldNames.contains("resourceRequirements")) {
                responseDTO.setResourceRequirements(project.getResourceRequirements());
            }
            responseDTOs.add(responseDTO);
        }
        return responseDTOs;
    }

    private Long id;
    private String name;
    private String purpose;
    private Boolean concrete;
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate startDate;
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate endDate;
    private Organization organization;
    private Long organizationId;
    private User manager;
    private Long managerId;
    private List<PropertyTagResponseDTO> propertyTags;
    private List<ResourceRequirement> resourceRequirements;

}
