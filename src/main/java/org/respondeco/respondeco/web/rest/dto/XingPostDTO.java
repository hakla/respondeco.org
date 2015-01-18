package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;

/**
 * XingPostDTO
 * Represents the necessary information to create a new XingPost namely
 * one string containing the post information and one string containing the
 * urlPath which gets attended to the postBaseUrl defined in application.yml
 * This Url will be posted on Xing to link to the appropriate respondeco
 * suburl.
 */
@Data
public class XingPostDTO {
    private String post;
    private String urlPath;
}
