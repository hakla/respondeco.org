package org.respondeco.respondeco.domain;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * A PropertyTag.
 */
@Entity
@Table(name = "T_PROPERTYTAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@ToString(exclude = "projects")
public class PropertyTag extends AbstractAuditingEntity implements Serializable {

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
