package org.respondeco.respondeco.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.respondeco.respondeco.domain.User;

import java.util.List;
@Data
public class OrganizationDTO {

    private String name;

    private String description;

    private String email;

    private boolean isNpo;

    private String owner;

}
