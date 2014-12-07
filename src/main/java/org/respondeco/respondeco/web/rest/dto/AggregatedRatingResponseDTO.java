package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.AggregatedRating;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Data
public class AggregatedRatingResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList(
            "count", "rating");

    public static AggregatedRatingResponseDTO fromEntity(AggregatedRating aggregatedRating, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        AggregatedRatingResponseDTO responseDTO = new AggregatedRatingResponseDTO();
        if (fieldNames.contains("count")) {
            responseDTO.setCount(aggregatedRating.getCount());
        }
        if (fieldNames.contains("rating")) {
            responseDTO.setRating(aggregatedRating.getRating());
        }
        return responseDTO;
    }

    public static List<AggregatedRatingResponseDTO> fromEntities(Collection<AggregatedRating> aggregatedRatings, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<AggregatedRatingResponseDTO> responseDTOs = new ArrayList<>();
        for(AggregatedRating aggregatedRating : aggregatedRatings) {
            responseDTOs.add(AggregatedRatingResponseDTO.fromEntity(aggregatedRating, fieldNames));
        }
        return responseDTOs;
    }
    private Integer count;
    private Double rating;

}
