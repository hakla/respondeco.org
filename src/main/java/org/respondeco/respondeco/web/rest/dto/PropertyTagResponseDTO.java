package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.respondeco.respondeco.domain.PropertyTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Clemens Puehringer on 25/11/14.
 */

@Data
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class PropertyTagResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList("name");

    public static List<PropertyTagResponseDTO> fromEntity(List<PropertyTag> propertyTags, List<String> fieldNames) {
        if(propertyTags == null) {
            return null;
        }
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<PropertyTagResponseDTO> response = new ArrayList<>();
        PropertyTagResponseDTO propertyTagResponseDTO;
        for(PropertyTag pt : propertyTags) {
            propertyTagResponseDTO = new PropertyTagResponseDTO();
            if(fieldNames.contains("id")) {
                propertyTagResponseDTO.setId(pt.getId());
            }
            if(fieldNames.contains("name")) {
                propertyTagResponseDTO.setName(pt.getName());
            }
            response.add(propertyTagResponseDTO);
        }
        return response;
    }

    private Long id;
    private String name;

}
