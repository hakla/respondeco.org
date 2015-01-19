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
import java.util.ArrayList;
import java.util.List;


/**
 * A Organization.
 */

@Entity
@Table(name = "T_ORGANIZATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString(exclude = {"owner", "members", "logo", "projects", "FollowingUsers"})
public class Organization extends AbstractAuditingEntity implements Serializable {

    @NotNull
    @Size(min = 3, max = 255)
    @Column(length = 255)
    private String name;

    @Column(length = 2048)
    private String description;

    @Email
    @Size(min = 0, max = 100)
    @Column(length = 100)
    private String email;

    @Column(name = "is_npo")
    private Boolean isNpo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    @OneToMany(mappedBy = "organization")
    private List<User> members;

    @Column(name = "spokesperson")
    private Long spokesPerson;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image logo;

    @OneToMany(mappedBy = "organization")
    private List<ResourceOffer> resourceOffers;

    @OneToMany(mappedBy = "organization")
    private List<ResourceMatch> resourceMatches;

    @OneToMany(mappedBy = "organization")
    private List<Project> projects;

    @NotNull
    @OneToOne
    @JoinColumn(name = "postingfeed_id")
    private PostingFeed postingFeed;

    @Column(name = "verified")
    private Boolean verified = false;


    public void addMember(User user) {
        members.add(user);
    }

    @ManyToMany
    @JoinTable(
        name="T_USER_FOLLOW_ORGANIZATION",
        joinColumns = {@JoinColumn(name = "ORGANIZATION_ID", referencedColumnName = "id")},
        inverseJoinColumns = { @JoinColumn(name = "USER_ID", referencedColumnName = "id")}
    )
    private List<User> FollowingUsers;
}
