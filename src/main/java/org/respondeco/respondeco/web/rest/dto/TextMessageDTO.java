package org.respondeco.respondeco.web.rest.dto;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Clemens Puehringer on 02/11/14.
 */

@Data
@ApiModel(value = "Text Message", description = "A text message DTO")
public class TextMessageDTO {

    @ApiModelProperty(value = "The receiver of the message", required = true)
    private String receiver;

    @ApiModelProperty(value = "The content of the message", required = true)
    private String content;

}
