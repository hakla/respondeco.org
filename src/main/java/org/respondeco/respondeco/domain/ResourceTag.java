package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by Roman Kern on 13.11.14.
 * Resource Tags
 * Defined a Tag for each resource
 */
@Data
@Entity
@Table(name = "T_RESOURCETAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceTag extends AbstractAuditingEntity implements Serializable {

    @NotNull
    private String name;

    @JsonIgnore
    @ManyToMany(targetEntity = ResourceOffer.class)
    private Set<ResourceOffer> resourceOffers;

    @JsonIgnore
    @ManyToMany(targetEntity = ResourceRequirement.class)
    private Set<ResourceRequirement> resourceRequirements;
}
