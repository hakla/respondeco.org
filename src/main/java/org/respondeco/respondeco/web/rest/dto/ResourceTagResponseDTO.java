package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.joda.time.DateTime;
import org.respondeco.respondeco.domain.PropertyTag;
import org.respondeco.respondeco.domain.ResourceTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Roman Kern on 18.11.14.
 */

@Data
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ResourceTagResponseDTO {

    public static List<String> DEFAULT_FIELDS = Arrays.asList("name");

    public static List<ResourceTagResponseDTO> fromEntities(List<ResourceTag> resourceTags, List<String> fieldNames) {
        if(resourceTags == null) {
            return null;
        }
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = DEFAULT_FIELDS;
        }
        List<ResourceTagResponseDTO> response = new ArrayList<>();
        ResourceTagResponseDTO resourceTagResponseDTO;
        for(ResourceTag rt : resourceTags) {
            resourceTagResponseDTO = new ResourceTagResponseDTO();
            if(fieldNames.contains("id")) {
                resourceTagResponseDTO.setId(rt.getId());
            }
            if(fieldNames.contains("name")) {
                resourceTagResponseDTO.setName(rt.getName());
            }
            response.add(resourceTagResponseDTO);
        }
        return response;
    }

    private Long id;
    private String name;

}
