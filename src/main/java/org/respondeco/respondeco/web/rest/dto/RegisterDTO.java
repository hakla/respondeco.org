package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Clemens Puehringer on 19/12/14.
 */

@Data
public class RegisterDTO {

    @NotNull
    @Size(min = 3, max = 50)
    private String orgname;

    private Boolean npo;
    private String langKey;

    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min = 5)
    private String password;

}
