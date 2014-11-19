package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * A ResourceOffer.
 */
@Entity
@Table(name = "T_RESOURCEOFFER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceOffer extends ResourceBase  implements Serializable {

    @Column(name = "organisation_id")
    private Long organisationId;

    @ManyToMany(mappedBy = "resourceOffers")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ResourceOfferJoinResourceRequirement> resourceOfferJoinResourceRequirements = new HashSet<>();

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Set<ResourceOfferJoinResourceRequirement> getResourceOfferJoinResourceRequirements() {
        return resourceOfferJoinResourceRequirements;
    }

    public void setResourceOfferJoinResourceRequirements(Set<ResourceOfferJoinResourceRequirement> resourceOfferJoinResourceRequirements) {
        this.resourceOfferJoinResourceRequirements = resourceOfferJoinResourceRequirements;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "ResourceOffer{" +
                "id=" + id +
                ", amount='" + amount + "'" +
                ", description='" + description + "'" +
                ", organisationId='" + organisationId + "'" +
                ", createBy='" + createdBy + "'" +
                ", createdDate='" + createdDate + "'" +
                ", lastModifiedBy='" + lastModifiedBy + "'" +
                ", lastModifiedDate='" + lastModifiedDate + "'" +
                ", isActive='" + active + "'" +
                '}';
    }

}
