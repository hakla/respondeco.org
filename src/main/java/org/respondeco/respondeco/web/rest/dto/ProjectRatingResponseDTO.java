package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.domain.ProjectRating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Data
public class ProjectRatingResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
            "id", "rating", "comment");

    public static ProjectRatingResponseDTO fromEntity(ProjectRating projectRating, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        ProjectRatingResponseDTO responseDTO = new ProjectRatingResponseDTO();
        if (fieldNames.contains("id")) {
            responseDTO.setId(projectRating.getId());
        }
        if (fieldNames.contains("rating")) {
            responseDTO.setRating(projectRating.getRating());
        }
        if (fieldNames.contains("comment")) {
            responseDTO.setComment(projectRating.getComment());
        }
        return responseDTO;
    }

    public static List<ProjectRatingResponseDTO> fromEntities(Collection<ProjectRating> projectRatings, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<ProjectRatingResponseDTO> responseDTOs = new ArrayList<>();
        for(ProjectRating projectRating : projectRatings) {
            responseDTOs.add(ProjectRatingResponseDTO.fromEntity(projectRating, fieldNames));
        }
        return responseDTOs;
    }
    private Long id;
    private Integer rating;
    private String comment;

}
