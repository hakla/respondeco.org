package org.respondeco.respondeco.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.respondeco.respondeco.domain.Image;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class UserDTO {

    public static final String[] DEFAULT_FIELDS = {"id", "login", "title", "gender", "firstName", "lastName", "email",
        "description", "langKey", "roles", "organizationId", "profilePicture"};

    public static UserDTO fromEntity(User user, List<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = Arrays.asList(DEFAULT_FIELDS);
        }
        UserDTO responseDTO = new UserDTO();
        if (fieldNames.contains("id")) {
            responseDTO.setId(user.getId());
        }
        if (fieldNames.contains("login")) {
            responseDTO.setLogin(user.getLogin());
        }
        if (fieldNames.contains("title")) {
            responseDTO.setTitle(user.getTitle());
        }
        if (fieldNames.contains("gender")) {
            responseDTO.setGender(user.getGender().name());
        }
        if (fieldNames.contains("firstName")) {
            responseDTO.setFirstName(user.getFirstName());
        }
        if (fieldNames.contains("lastName")) {
            responseDTO.setLastName(user.getLastName());
        }
        if (fieldNames.contains("email")) {
            responseDTO.setEmail(user.getEmail());
        }
        if (fieldNames.contains("description")) {
            responseDTO.setDescription(user.getDescription());
        }
        if (fieldNames.contains("langKey")) {
            responseDTO.setLangKey(user.getLangKey());
        }
        if (fieldNames.contains("roles")) {
            responseDTO.setRoles(user.getAuthorities()
                .stream()
                    .map(authority -> authority.getName())
                        .collect(Collectors.toList()));
        }
        if (fieldNames.contains("organizationId")) {
            responseDTO.setOrganizationId(user.getOrganization().getId());
        }
        if (fieldNames.contains("profilePicture")) {
            responseDTO.setProfilePicture(ImageDTO.fromEntity(user.getProfilePicture(), null));
        }
        return responseDTO;
    }

    public static List<UserDTO> fromEntities(List<User> users, List<String> fieldNames) {
        if(fieldNames == null || fieldNames.size() == 0) {
            fieldNames = Arrays.asList(DEFAULT_FIELDS);
        }
        List<UserDTO> responseDTOs = new ArrayList<>();
        for(User user : users) {
            responseDTOs.add(UserDTO.fromEntity(user, fieldNames));
        }
        return responseDTOs;
    }

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
