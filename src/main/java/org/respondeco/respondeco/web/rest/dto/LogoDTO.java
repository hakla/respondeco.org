package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;

import java.util.Arrays;

/**
 * Created by clemens on 28/10/14.
 */
@Data
public class LogoDTO {

    private String label;

    private byte[] data;

    private Long orgId;

}
