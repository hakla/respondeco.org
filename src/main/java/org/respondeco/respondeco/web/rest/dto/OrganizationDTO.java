package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.User;

import javax.validation.constraints.NotNull;
import java.util.List;
@Data
public class OrganizationDTO {

    @NotNull
    private String name;

    private String description;

    private String email;

    private boolean isNpo;

}
