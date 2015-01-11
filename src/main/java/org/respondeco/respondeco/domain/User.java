package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A user.
 */
@Entity
@Getter
@Setter
@Table(name = "T_USER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@ToString(exclude = {"persistentTokens", "authorities", "organization", "UserFollowProjects", "UserFollowOrganizations"}) // otherwise there will be errors in the log because authorities is lazy loaded
public class User extends AbstractAuditingEntity implements Serializable {

    @NotNull
    @Size(min = 0, max = 50)
    @Column(length = 50)
    private String login;

    @JsonIgnore
    @Size(min = 0, max = 100)
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
    private String firstName;

    @Size(min = 0, max = 50)
    @Column(name = "last_name", length = 50)
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
}
