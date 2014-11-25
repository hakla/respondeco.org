package org.respondeco.respondeco.web.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.respondeco.respondeco.domain.User;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
@Data
public class OrganizationDTO {

    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    private String description;

    private String email;

    private boolean isNpo;

}
