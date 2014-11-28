package org.respondeco.respondeco.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Clemens Puehringer on 28/11/14.
 */

@Data
@AllArgsConstructor
public class ErrorResponseDTO {

    private String key;
    private String message;

}
