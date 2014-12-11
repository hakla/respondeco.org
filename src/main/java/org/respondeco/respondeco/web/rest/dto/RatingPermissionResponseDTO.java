package org.respondeco.respondeco.web.rest.dto;

import com.google.common.collect.Lists;
import lombok.Data;
import org.respondeco.respondeco.domain.RatingPermission;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by clemens on 11/12/14.
 */

@Data
public class RatingPermissionResponseDTO {

    /**
     * transform a RatingPermission object into a response dto
     * @param ratingPermission the permission object to transform into a dto
     * @return a dto version of the RatingPermission object, containing the match id and the status flag
     */
    public static RatingPermissionResponseDTO fromEntity(RatingPermission ratingPermission) {
        RatingPermissionResponseDTO responseDTO = new RatingPermissionResponseDTO();
        if(ratingPermission.getResourceMatch() != null) {
            responseDTO.setMatchid(ratingPermission.getResourceMatch().getId());
        }
        responseDTO.setAllowed(ratingPermission.getAllowed());
        return responseDTO;
    }

    /**
     * transform a list of RatingPermission objects into DTOs,
     * {@see RatingPermissionResponseDTO.fromEntity(RatingPermission)}
     * @param ratingPermissions the objects to transform
     * @return a list of DTOs
     */
    public static List<RatingPermissionResponseDTO> fromEntities(List<RatingPermission> ratingPermissions) {
        //stream the ratingPermissions list, map the elements of the list to the RatingPermissionResponseDTO::fromEntity
        //method and collect the results as a list
        return ratingPermissions.stream()
            .map(RatingPermissionResponseDTO::fromEntity)
            .collect(Collectors.toList());
    }


    /**
     * ResourceMatch id
     */
    private Long matchid;

    /**
     * indicator if the Organization or Project can be rated by the current User
     */
    private Boolean allowed;

}
