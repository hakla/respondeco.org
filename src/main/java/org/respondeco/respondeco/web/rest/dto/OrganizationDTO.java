package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.User;

import java.util.List;
@Data
public class OrganizationDTO {

    private String name;

    private String description;

    private String email;

    private boolean isNpo;

}
