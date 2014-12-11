package org.respondeco.respondeco.domain;

import lombok.Data;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Data
public class AggregatedRating {

    private Long count;
    private Double rating;
    private Boolean rateable;

}
