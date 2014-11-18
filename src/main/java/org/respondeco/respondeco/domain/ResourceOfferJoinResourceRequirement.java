package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A ResourceOfferJoinResourceRequirement.
 */
@Entity
@Table(name = "T_RESOURCEOFFERJOINRESOURCEREQUIREMENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceOfferJoinResourceRequirement extends AbstractAuditingEntity implements Serializable {

    @Column(name = "resource_offer_id")
    private Long resourceOfferId;

    @Column(name = "resource_requirement_id")
    private Long resourceRequirementId;

    @Column(name = "amount", precision=10, scale=2)
    private BigDecimal amount;

    public Long getResourceOfferId() {
        return resourceOfferId;
    }

    public void setResourceOfferId(Long resourceOfferId) {
        this.resourceOfferId = resourceOfferId;
    }

    public Long getResourceRequirementId() {
        return resourceRequirementId;
    }

    public void setResourceRequirementId(Long resourceRequirementId) {
        this.resourceRequirementId = resourceRequirementId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ResourceOfferJoinResourceRequirement{" +
                "id=" + id +
                ", resourceOfferId='" + resourceOfferId + "'" +
                ", resourceRequirementId='" + resourceRequirementId + "'" +
                ", amount='" + amount + "'" +
                ", createdBy='" + createdBy + "'" +
                ", createdDate='" + createdDate + "'" +
                ", lastModifiedBy='" + lastModifiedBy + "'" +
                ", lastModifiedDate='" + lastModifiedDate + "'" +
                ", isActive='" + active + "'" +
                '}';
    }
}
