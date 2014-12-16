package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;

import java.util.List;

@Getter
@Setter
@ToString
public class UserDTO {

    private Long id;
    private String login;
    private String password;
    private String title;
    private String gender;
    private String firstName;
    private String lastName;
    private String email;
    private String description;
    private String langKey;
    private List<String> roles;
    private Long organizationId;
    private ImageDTO profilePicture;

    public UserDTO() {
    }

    public UserDTO(String login) {
        this.login = login;
    }

    public UserDTO(Long id) {
        this.id = id;
    }

    public UserDTO(Long id, String login, String password, String title, String gender, String firstName, String lastName,
                   String email, String description, String langKey, List<String> roles, Organization organization) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.title = title;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.description = description;

        this.langKey = langKey;
        this.roles = roles;

        if (organization != null) {
            this.organizationId = organization.getId();
        }
    }

    public UserDTO(Long id, String login, String password, String title, String gender, String firstName, String lastName,
                   String email, String description, String langKey, List<String> roles, Organization organization, ImageDTO profilePicture) {
        this(id, login, password, title, gender, firstName, lastName, email, description, langKey, roles, organization);
        this.profilePicture = profilePicture;
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();

        this.title = title;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.description = description;

        this.langKey = langKey;
        this.roles = roles;
    }
}
