package org.respondeco.respondeco.web.rest.dto;

import lombok.Data;
import org.respondeco.respondeco.domain.Organization;

import java.util.List;

public class UserDTO {

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

    private OrganizationDTO organization;

    public UserDTO() {
    }

    public UserDTO(String login, String password, String title, String gender, String firstName, String lastName,
                   String email, String description, String langKey, List<String> roles, OrganizationDTO organization) {
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

        this.organization = organization;
    }

    public String getPassword() {
        return password;
    }

    public String getLogin() {
        return login;
    }


    public String getTitle() {
        return title;
    }

    public String getGender() {
        return gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getDescription() {
        return description;
    }

    public String getLangKey() {
        return langKey;
    }

    public List<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserDTO{");
        sb.append("login='").append(login).append('\'');
        if(password != null) {
            sb.append(", password='").append(password.length()).append('\'');
        }
        sb.append(", title='").append(title).append('\'');
        sb.append(", gender='").append(gender).append('\'');
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", langKey='").append(langKey).append('\'');
        sb.append(", roles=").append(roles);
        sb.append('}');
        return sb.toString();
    }
}
