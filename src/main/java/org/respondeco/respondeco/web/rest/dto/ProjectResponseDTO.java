package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.web.rest.dto.util.CustomLocalDateSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by clemens on 25/11/14.
 */

@Data
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ProjectResponseDTO {

    private static final Logger log = LoggerFactory.getLogger(ProjectResponseDTO.class);

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
            "id", "name", "purpose", "concrete", "startDate", "endDate",
            "organizationId", "managerId", "propertyTags", "resourceRequirements", "logo", "ratings", "successful");

    public static ProjectResponseDTO fromEntity(Project project, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        ProjectResponseDTO responseDTO = new ProjectResponseDTO();
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
        if (fieldNames.contains("organization")) {
            responseDTO.setOrganization(OrganizationResponseDTO
                    .fromEntity(project.getOrganization(),null));
        }
        if (fieldNames.contains("organizationId")) {
            responseDTO.setOrganizationId(project.getOrganization().getId());
        }
        if (fieldNames.contains("manager")) {
            responseDTO.setManager(new UserDTO(project.getManager()));
        }
        if (fieldNames.contains("managerId")) {
            responseDTO.setManagerId(project.getManager().getId());
        }
        if (fieldNames.contains("propertyTags")) {
            responseDTO.setPropertyTags(PropertyTagResponseDTO
                .fromEntities(project.getPropertyTags(), null));
        }
        if (fieldNames.contains("resourceRequirements")) {
            log.debug("adding resource requirements");
            responseDTO.setResourceRequirements(ResourceRequirementResponseDTO
                .fromEntities(project.getResourceRequirements(), null));
        }
        if (fieldNames.contains("logo")) {
            if (project.getProjectLogo() != null) {
                responseDTO.setLogo(new ImageDTO(project.getProjectLogo()));
            }
        }
        if (fieldNames.contains("successful")) {
            responseDTO.setSuccessful(project.getSuccessful());
        }
        //TODO FIX
        /**
        if (fieldNames.contains("ratings")) {
            responseDTO.setRatings(ProjectRatingResponseDTO
                    .fromEntities(project.getRatings(),null));
            responseDTO.setRatings(RatingResponseDTO.fromEntities(project.getResourceMatches().g, null));
        }
         **/
        return responseDTO;
    }

    public static List<ProjectResponseDTO> fromEntities(Collection<Project> projects, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<ProjectResponseDTO> responseDTOs = new ArrayList<>();

        if (projects != null) {
            for (Project project : projects) {
                responseDTOs.add(ProjectResponseDTO.fromEntity(project, fieldNames));
            }
        }

        return responseDTOs;
    }

    private Long id;
    private String name;
    private String purpose;
    private Boolean concrete;
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    private LocalDate startDate;
    private OrganizationResponseDTO organization;
    private Long organizationId;
    private UserDTO manager;
    private Long managerId;
    private List<PropertyTagResponseDTO> propertyTags;
    private List<ResourceRequirementResponseDTO> resourceRequirements;
    private ImageDTO logo;
    private List<RatingResponseDTO> ratings;
    private Boolean successful;

}
