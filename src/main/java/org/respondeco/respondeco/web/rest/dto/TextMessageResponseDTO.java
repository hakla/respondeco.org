package org.respondeco.respondeco.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.respondeco.respondeco.domain.User;

import java.time.LocalDate;

/**
 * Created by clemens on 15/11/14.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TextMessageResponseDTO {

    private Long id;

    private UserDTO sender;

    private String content;

    private DateTime timestamp;

    private Boolean isNew;

}
