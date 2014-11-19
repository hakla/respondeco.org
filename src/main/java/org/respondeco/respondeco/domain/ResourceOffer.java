package org.respondeco.respondeco.domain;


import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import javax.persistence.*;
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

    @Column(name = "is_commercial", nullable = false)
    private Boolean isCommercial = false;

    @Column(name = "is_recurrent")
    private Boolean isRecurrent = false;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "start_date")
    private DateTime startDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "end_date")
    private DateTime endDate;


    public void setIsCommercial(Boolean isCommercial){ this.isCommercial = isCommercial; }
    public void setIsRecurrent(Boolean isRecurrent){ this.isRecurrent = isRecurrent; }
    public void setStartDate(DateTime startDate){ this.startDate = startDate; }
    public void setEndDate(DateTime endDate){ this.endDate = endDate; }

    public Boolean getIsCommercial() { return this.isCommercial; }
    public Boolean getIsRecurrent() { return this.isRecurrent; }
    public DateTime getStartDate() { return this.startDate; }
    public DateTime getEndDate() { return this.endDate; }

    /*
    @ManyToMany(mappedBy = "resourceOffers")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    */
    @Transient
    private Set<ResourceOfferJoinResourceRequirement> resourceOfferJoinResourceRequirements = new HashSet<>();

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }
    @Transient
    public Set<ResourceOfferJoinResourceRequirement> getResourceOfferJoinResourceRequirements() {
        return resourceOfferJoinResourceRequirements;
    }
    @Transient
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
                ", isCommercial='" + isCommercial + "'" +
                ", isRecurrent='" + isRecurrent + "'" +
                ", startDate='" + startDate + "'" +
                ", endDate='" + endDate + "'" +
                ", createBy='" + createdBy + "'" +
                ", createdDate='" + createdDate + "'" +
                ", lastModifiedBy='" + lastModifiedBy + "'" +
                ", lastModifiedDate='" + lastModifiedDate + "'" +
                ", isActive='" + active + "'" +
                '}';
    }

}
