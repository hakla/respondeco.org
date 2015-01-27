package org.respondeco.respondeco.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO which represents a ProjectLocation
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectLocationDTO {

    private Long id;

    private Long projectId;

    private String address;

    private float latitude;

    private float longitude;

}
