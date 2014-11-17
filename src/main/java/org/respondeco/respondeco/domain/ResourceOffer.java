package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * Created by Roman on 13.11.14.
 * Organisation thats offer resources
 */
@Data
@Entity
@Table(name = "T_RESOURCEOFFER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceOffer extends ResourceBase {

    @NotNull
    @Column(name = "organisation_id")
    private Long organisationId;

    @JsonIgnore
    @ManyToMany
    private Set<ResourceTag> resourceTags;

}
