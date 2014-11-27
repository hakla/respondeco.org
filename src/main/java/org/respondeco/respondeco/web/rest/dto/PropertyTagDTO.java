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
public class PropertyTagDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList("name");

    public static List<PropertyTagDTO> fromEntity(List<PropertyTag> propertyTags, List<String> fieldNames) {
        if(propertyTags == null) {
            return null;
        }
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<PropertyTagDTO> response = new ArrayList<>();
        PropertyTagDTO propertyTagDTO;
        for(PropertyTag pt : propertyTags) {
            propertyTagDTO = new PropertyTagDTO();
            if(fieldNames.contains("id")) {
                propertyTagDTO.setId(pt.getId());
            }
            if(fieldNames.contains("name")) {
                propertyTagDTO.setName(pt.getName());
            }
            response.add(propertyTagDTO);
        }
        return response;
    }

    private Long id;
    private String name;

}
