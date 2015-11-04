package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.respondeco.respondeco.web.rest.mapping.DefaultReturnValue;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A user.
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = {"persistentTokens", "authorities", "organization"}) // otherwise there will be errors in the log because authorities is lazy loaded
@Entity
@Table(name = "T_USER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class User extends AbstractAuditingEntity {

    @NotNull
    @Size(min = 0, max = 50)
    @Column(length = 50)
    @DefaultReturnValue
    private String login;

    @JsonIgnore
    @Size(min = 8, max = 100)
    @Column(length = 100)
    private String password;

    @Column(name = "title", length = 20)
    private String title;

    @NotNull
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Size(min = 0, max = 50)
    @Column(name = "first_name", length = 50)
    @DefaultReturnValue
    private String firstName;

    @Size(min = 0, max = 50)
    @Column(name = "last_name", length = 50)
    @DefaultReturnValue
    private String lastName;

    @Size(min = 0, max = 100)
    @Column(length = 100)
    private String email;

    @Column(name = "description", length = 2048)
    private String description;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @JsonIgnore
    private boolean activated = false;

    @Size(min = 2, max = 5)
    @Column(name = "lang_key", length = 5)
    private String langKey;

    @JsonIgnore
    @Size(min = 0, max = 20)
    @Column(name = "activation_key", length = 20)
    private String activationKey;

    @ManyToOne
    @JoinColumn(name = "imageId")
    @DefaultReturnValue
    private Image profilePicture;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "T_USER_AUTHORITY",
            joinColumns = {@JoinColumn(name = "login", referencedColumnName = "login")},
            inverseJoinColumns = {@JoinColumn(name = "name", referencedColumnName = "name")})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Authority> authorities = new HashSet<>();

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<PersistentToken> persistentTokens = new HashSet<>();

    @NotNull
    @Column(name = "invited")
    private boolean invited;

    @ManyToMany
    @JoinTable(
        name="T_USER_FOLLOW_PROJECT",
        joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "id")},
        inverseJoinColumns = { @JoinColumn(name = "PROJECT_ID", referencedColumnName = "id")}
    )
    private List<Project> followProjects;

    @ManyToMany
    @JoinTable(
        name="T_USER_FOLLOW_ORGANIZATION",
        joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "id")},
        inverseJoinColumns = { @JoinColumn(name = "ORGANIZATION_ID", referencedColumnName = "id")}
    )
    private List<Organization> followOrganizations;

    @DefaultReturnValue
    public List<String> getRoles() {
        return authorities
            .stream()
            .map(Authority::getAuthority)
            .collect(Collectors.toList());
    }
}
