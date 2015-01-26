package org.respondeco.respondeco.web.rest.dto;

import lombok.*;

/**
 * PostDTO
 * Represents the necessary information to create a new Post namely
 * one string containing the post information and one string containing the
 * urlPath which gets attended to the postBaseUrl defined in application.yml
 * This Url will be posted on the appropriate social media platform to link
 * to the appropriate respondeco suburl.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private String post;
    private String urlPath;
}
