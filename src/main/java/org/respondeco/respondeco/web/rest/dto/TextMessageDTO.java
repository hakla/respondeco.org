package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by clemens on 02/11/14.
 */

@Data
public class TextMessageDTO {

    private String receiver;
    private String content;


}
