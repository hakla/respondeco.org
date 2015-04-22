package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.matching.MatchingEntity;
import org.respondeco.respondeco.matching.MatchingTag;
import org.respondeco.respondeco.web.rest.mapping.serializer.CustomLocalDateSerializer;
import org.respondeco.respondeco.web.rest.mapping.DefaultReturnField;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A Project
 */
@Entity
@Table(name = "T_PROJECT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString(exclude = {"organization", "manager", "propertyTags", "resourceRequirements", "projectLogo", "FollowingUsers"})
public class Project extends AbstractAuditingNamedEntity implements Serializable, MatchingEntity {

    @Column(name = "purpose", length = 2048)
    @DefaultReturnField
    private String purpose;

    @Column(name = "is_concrete")
    private boolean concrete = false;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @ManyToOne
    private Organization organization;

    @NotNull
    @ManyToOne
    private User manager;

    @ManyToMany
    @JoinTable(
            name="T_PROJECT_JOIN_T_PROPERTYTAG",
            joinColumns = { @JoinColumn(name = "PROJECT_ID", referencedColumnName = "id" ) },
            inverseJoinColumns = { @JoinColumn(name = "PROPERTYTAG_ID", referencedColumnName = "id" ) }
    )
    @Lazy(false)
    private List<PropertyTag> propertyTags;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "projectLogo_id")
    private Image projectLogo;

    @OneToMany(mappedBy = "project")
    private List<ResourceRequirement> resourceRequirements;

    @OneToMany(mappedBy = "project")
    private List<ResourceMatch> resourceMatches;

    /**
     * If the project was started successfully (all essential resources were donated by the project start)
     */
    @Column(name = "is_successful")
    private Boolean successful;

    @NotNull
    @OneToOne
    @JoinColumn(name = "postingfeed_id")
    private PostingFeed postingFeed;

    @ManyToMany
    @JoinTable(
        name="T_USER_FOLLOW_PROJECT",
        joinColumns = {@JoinColumn(name = "PROJECT_ID", referencedColumnName = "id")},
        inverseJoinColumns = { @JoinColumn(name = "USER_ID", referencedColumnName = "id")}
    )
    private List<User> FollowingUsers;

    @Override
    public Set<MatchingTag> getTags() {
        return new HashSet<>(propertyTags);
    }
}
