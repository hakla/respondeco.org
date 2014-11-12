package org.respondeco.respondeco.web.rest.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * Created by Clemens Puehringer on 28/10/14.
 */

@Data
@ApiModel(value = "Profile Picture", description = "A profile picture DTO")
public class ProfilePictureDTO {

    @ApiModelProperty(value = "The label of the profile picture", required = true)
    private String label;

    @ApiModelProperty(value = "The data of the profile picture", required = true)
    private byte[] data;

    @Override
    public String toString() {
        return "ProfilePictureDTO{" +
                "label='" + label + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
