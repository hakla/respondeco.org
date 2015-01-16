package org.respondeco.respondeco.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TwitterConnectionDTO
 * Used to send token and verifier from user to backend
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TwitterConnectionDTO {

    private String token;
    private String verifier;
}
