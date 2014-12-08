package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.Rating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Data
public class RatingResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
            "id", "rating", "comment");

    public static RatingResponseDTO fromEntity(Rating projectRating, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        RatingResponseDTO responseDTO = new RatingResponseDTO();
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

    public static List<RatingResponseDTO> fromEntities(Collection<Rating> projectRatings, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<RatingResponseDTO> responseDTOs = new ArrayList<>();
        for(Rating projectRating : projectRatings) {
            responseDTOs.add(RatingResponseDTO.fromEntity(projectRating, fieldNames));
        }
        return responseDTOs;
    }
    private Long id;
    private Integer rating;
    private String comment;

}
