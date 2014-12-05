package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.Project;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Data
public class ProjectRatingRequestDTO {

    private Long id;

    @NotNull
    @Min(1)
    @Max(10)
    private Integer rating;
    private String comment;
    private Long projectId;

}
