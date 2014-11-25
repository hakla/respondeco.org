package org.respondeco.respondeco.web.rest.dto;

import lombok.*;
import org.respondeco.respondeco.domain.OrgJoinRequest;

import java.util.List;

/**
 * Created by Chris on 10.11.2014.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrgJoinRequestDTO {
    private String orgName;

    private String userLogin;


}
