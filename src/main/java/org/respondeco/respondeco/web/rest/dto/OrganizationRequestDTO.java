package org.respondeco.respondeco.web.rest.dto;

import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.respondeco.respondeco.domain.Image;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
@Data
@Getter
public class OrganizationRequestDTO {

    @ApiModelProperty(value = "Organization ID")
    private Long id;

    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    private String description;

    private String email;

    private Boolean isNpo;

    private UserDTO owner;

    private ImageDTO logo;

    private String website;

}
