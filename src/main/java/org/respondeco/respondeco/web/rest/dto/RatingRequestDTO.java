package org.respondeco.respondeco.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Created by Chris on 10.11.2014.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequestDTO {

    private Long id;

    private Long matchid;

    @NotNull
    @Min(0)
    @Max(5)
    private Integer rating;

    private String comment;
}
