package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * A ResourceRequirement.
 */
@Entity
@Table(name = "T_RESOURCEREQUIREMENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@PrimaryKeyJoinColumn(name = "resource_id", referencedColumnName = "id")
@Getter
@Setter
@ToString(callSuper = true, exclude = {"resourceMatches", "project"})
public class ResourceRequirement extends ResourceBase implements Serializable {

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(name = "is_essential")
    private Boolean isEssential;

    @OneToMany(mappedBy = "resourceRequirement")
    private List<ResourceMatch> resourceMatches;

}
