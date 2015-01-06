package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Roman Kern on 25.12.15.
 * Describe the state of current follow Project/Organization.
 */
@Getter
@Setter
public class FollowStateDTO {
    /**
     * Follow State
     */
    private Boolean state;
}
