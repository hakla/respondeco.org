package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Data
public class SupporterRatingRequestDTO {

    private Long id;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer rating;
    private String comment;
    private Long projectId;
    private Long organizationId;

}
