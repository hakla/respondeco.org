package org.respondeco.respondeco.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.respondeco.respondeco.matching.MatchingTag;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * A PropertyTag.
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = "projects")
@Entity
@Table(name = "T_PROPERTYTAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PropertyTag extends AbstractAuditingEntity implements MatchingTag {

    @Column(name = "name")
    @NotNull
    @Size(min = 1, max = 50)
    private String name;

    @ManyToMany
    @JoinTable(
            name="T_PROJECT_JOIN_T_PROPERTYTAG",
            joinColumns = { @JoinColumn(name = "PROPERTYTAG_ID", referencedColumnName = "id" ) },
            inverseJoinColumns = { @JoinColumn(name = "PROJECT_ID", referencedColumnName = "id" ) }
    )
    private List<Project> projects;

}
