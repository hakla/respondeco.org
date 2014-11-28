package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;

/**
 * Created by Chris on 10.11.2014.
 */
@Data
public class OrgJoinRequestWithActiveFlagDTO {

    private Long id;

    private String orgName;

    private String userLogin;

    private boolean isActive;
}
