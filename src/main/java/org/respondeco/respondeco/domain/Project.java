package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.web.rest.dto.util.CustomLocalDateSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * A ProjectIdea.
 */
@Data
@Entity
@Table(name = "T_PROJECT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Project extends AbstractAuditingNamedEntity implements Serializable {

    @Column(name = "purpose")
    private String purpose;

    @Column(name = "is_concrete")
    private boolean concrete = false;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

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
    private List<PropertyTag> propertyTags;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "projectLogo_id")
    private Image projectLogo;

    @OneToMany
    @JoinTable(
            name="T_PROJECT_JOIN_T_RESOURCEREQUIREMENT",
            joinColumns = { @JoinColumn(name = "PROJECT_ID", referencedColumnName = "id" ) },
            inverseJoinColumns = { @JoinColumn(name = "RESOURCEREQUIREMENT_ID", referencedColumnName = "id" ) }
    )
    private List<ResourceRequirement> resourceRequirements;

}
