package org.respondeco.respondeco.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.time.DateTime;

import java.time.LocalDate;

/**
 * Created by clemens on 15/11/14.
 */
@Data
@AllArgsConstructor
public class TextMessageResponseDTO {

    private Long id;

    private String sender;

    private String content;

    private DateTime timestamp;

}
