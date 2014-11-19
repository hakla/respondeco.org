package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A ResourceOfferJoinResourceTag.
 */
@Entity
@Table(name = "T_RESOURCEOFFERJOINRESOURCETAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceOfferJoinResourceTag extends AbstractResourceTagEntity implements Serializable {

    @Column(name = "resource_offer_id")
    private Long resourceOfferId;

    public Long getResourceOfferId() {
        return resourceOfferId;
    }

    public void setResourceOfferId(Long resourceOfferId) {
        this.resourceOfferId = resourceOfferId;
    }

    @Override
    public String toString() {
        return "ResourceOfferJoinResourceTag{" +
                "id=" + id +
                ", resourceOfferId='" + resourceOfferId + "'" +
                ", resourceTagId='" + resourceTagId + "'" +
                '}';
    }
}
