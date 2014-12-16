package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by clemens on 11/12/14.
 */

@Getter
@Setter
@ToString(exclude = {"resourceMatch"})
public class RatingPermission {

    private ResourceMatch resourceMatch;
    private Boolean allowed;

}
