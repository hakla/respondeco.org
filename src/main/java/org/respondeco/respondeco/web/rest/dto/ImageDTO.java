package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.respondeco.respondeco.domain.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Klaus on 26.11.2014.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class ImageDTO {

    public static final String[] DEFAULT_FIELDS = {"id", "name"};

    public static ImageDTO fromEntity(Image image, List<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = Arrays.asList(DEFAULT_FIELDS);
        }
        ImageDTO responseDTO = new ImageDTO();
        if(fieldNames.contains("id")) {
            responseDTO.setId(image.getId());
        }
        if(fieldNames.contains("name")) {
            responseDTO.setName(image.getName());
        }
        return responseDTO;
    }

    public static List<ImageDTO> fromEntities(List<Image> images, List<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = Arrays.asList(DEFAULT_FIELDS);
        }
        List<ImageDTO> responseDTOs = new ArrayList<>();
        for(Image image : images) {
            responseDTOs.add(ImageDTO.fromEntity(image, fieldNames));
        }
        return responseDTOs;
    }

    public Long id;
    public String name;

    public ImageDTO(Image image) {
        this.id = image.getId();
        this.name = image.getName();
    }
}
