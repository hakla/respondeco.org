package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.ProjectLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * ProjectLocationResponseDTO
 *
 * ResponseDTO for Project Location used for sending information about Project Location
 * from backend to frontend.
 */
@Data
public class ProjectLocationResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
        "id", "address", "latitude", "longitude", "project"
    );

    /**
     * Creates a ProjectLocationResponseDTO Object from a ProjectLocation-Object with the specified fields
     * @param projectLocation ProjectLocation object which contains the information for the DTO
     * @param fieldNames String-Collection defining the returned fields of the DTO.
     * @return DTO Object representing a ProjectLocation
     */
    public static ProjectLocationResponseDTO fromEntity(ProjectLocation projectLocation, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }

        ProjectLocationResponseDTO responseDTO = new ProjectLocationResponseDTO();

        if (fieldNames.contains("id")) {
            responseDTO.setId(projectLocation.getId());
        }
        if (fieldNames.contains("latitude")) {
            responseDTO.setLatitude(projectLocation.getLat());
        }
        if (fieldNames.contains("longitude")) {
            responseDTO.setLongitude(projectLocation.getLng());
        }
        if (fieldNames.contains("address")) {
            responseDTO.setAddress(projectLocation.getAddress());
        }
        if (fieldNames.contains("project")) {
            responseDTO.setProject(ProjectResponseDTO.fromEntity(projectLocation.getProject(), null));
        }

        return responseDTO;
    }

    /**
     * Creates a List of ProjectLocationResponseDTOs from a List of given projecLocations containing the specified fields in fieldNames
     * @param projectLocations List of ProjectLocations
     * @param fieldNames String-Collection defining the returned fields of the DTO.
     * @return List of DTOs representing ProjectLocation Objects
     */
    public static List<ProjectLocationResponseDTO> fromEntities(List<ProjectLocation> projectLocations, Collection<String> fieldNames) {

        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }

        List<ProjectLocationResponseDTO> responseDTOs = new ArrayList<>();

        for(ProjectLocation location : projectLocations) {
            responseDTOs.add(ProjectLocationResponseDTO.fromEntity(location, fieldNames));
        }

        return responseDTOs;
    }

    private Long id;
    private String address;
    private double latitude;
    private double longitude;
    private ProjectResponseDTO project;
}
