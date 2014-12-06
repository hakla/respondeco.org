package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.ProjectRating;
import org.respondeco.respondeco.domain.SupporterRating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Data
public class SupporterRatingResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
            "id", "rating", "comment");

    public static SupporterRatingResponseDTO fromEntity(SupporterRating supporterRating, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        SupporterRatingResponseDTO responseDTO = new SupporterRatingResponseDTO();
        if (fieldNames.contains("id")) {
            responseDTO.setId(supporterRating.getId());
        }
        if (fieldNames.contains("rating")) {
            responseDTO.setRating(supporterRating.getRating());
        }
        if (fieldNames.contains("comment")) {
            responseDTO.setComment(supporterRating.getComment());
        }
        return responseDTO;
    }

    public static List<SupporterRatingResponseDTO> fromEntities(Collection<SupporterRating> supporterRatings, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<SupporterRatingResponseDTO> responseDTOs = new ArrayList<>();
        for(SupporterRating supporterRating : supporterRatings) {
            responseDTOs.add(SupporterRatingResponseDTO.fromEntity(supporterRating, fieldNames));
        }
        return responseDTOs;
    }
    private Long id;
    private Integer rating;
    private String comment;

}
