package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.SocialMediaConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This DTO represents a SocialMediaConnection object
 */
@Data
public class SocialMediaConnectionResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList("id", "userId","provider");

    /**
     * Generates a SocialMediaConnectionResponseDTO out of a SocialMediaConnection object
     * @param connection SocialMediaConnect object, contains necessary information for the DTO.
     * @param fieldNames defines which fields are set in the DTO
     * @return SocialMediaConnectionResponseDTO representing a SociaLMediaConnection object
     */
    public static SocialMediaConnectionResponseDTO fromEntity(SocialMediaConnection connection, Collection<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }

        SocialMediaConnectionResponseDTO responseDTO = new SocialMediaConnectionResponseDTO();
        if(fieldNames.contains("id")) {
            responseDTO.setId(connection.getId());
        }
        if(fieldNames.contains("userId")) {
            responseDTO.setUserId(connection.getUser().getId());
        }
        if(fieldNames.contains("provider")) {
            responseDTO.setProvider(connection.getProvider());
        }


        return responseDTO;
    }

    /**
     * Takes a list of SocialMediaConnections and wraps them into DTO objects.
     * @param connections list of SocialMediaConnect objects, containing necessary information for the DTO.
     * @param fieldNames defines which fields are set in the DTO
     * @return list of SocialMediaConnectionResponseDTO representing a list of SocialMediaConnections
     */
    public static List<SocialMediaConnectionResponseDTO> fromEntities(List<SocialMediaConnection> connections, Collection<String> fieldNames) {
        List<SocialMediaConnectionResponseDTO> responseDTOs = new ArrayList<>();

        for(SocialMediaConnection conn : connections) {
            responseDTOs.add(SocialMediaConnectionResponseDTO.fromEntity(conn, fieldNames));
        }

        return responseDTOs;
    }

    private Long id;
    private Long userId;
    private String provider;

}
