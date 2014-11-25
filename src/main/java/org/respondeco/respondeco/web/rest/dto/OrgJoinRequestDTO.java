package org.respondeco.respondeco.web.rest.dto;

import lombok.*;
import org.respondeco.respondeco.domain.OrgJoinRequest;

/**
 * Created by Chris on 10.11.2014.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgJoinRequestDTO {

    private Long id;
    private String orgName;
    private String userLogin;

}
