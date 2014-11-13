package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
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
@Table
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceTag extends AbstractAuditingEntity implements Serializable {

    @NotNull
    private String name;

    @JsonIgnore
    @OneToMany(targetEntity = ResourceOffer.class, mappedBy = "resourceTag")
    private Set<ResourceOffer> resourceOfferSet;

    @JsonIgnore
    @OneToMany(targetEntity = ResourceRequirement.class, mappedBy = "resourceTag")
    private Set<ResourceRequirement> resourceRequirements;
}
