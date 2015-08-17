package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Clemens Puehringer on 19/12/14.
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterDTO {

    @NotNull
    @Size(min = 3, max = 50)
    private String organization;

    private Boolean npo;
    private String langKey;

    @NotNull
    @Email
    private String email;
    @NotNull
    @Size(min = 5)
    private String password;

}
