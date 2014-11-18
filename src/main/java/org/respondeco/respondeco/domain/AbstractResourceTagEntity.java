package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Roman Kern on 17.11.14.
 */
@MappedSuperclass
public abstract class AbstractResourceTagEntity extends AbstractAuditingEntity implements Serializable {

    @Column(name = "resource_tag_id")
    protected Long resourceTagId;

    @ManyToMany(mappedBy = "resourceOfferJoinResourceTags")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ResourceTag> resourceTags = new HashSet<>();


    public Long getResourceTagId() {
        return resourceTagId;
    }

    public void setResourceTagId(Long resourceTagId) {
        this.resourceTagId = resourceTagId;
    }

    public Set<ResourceTag> getResourceTags() {
        return resourceTags;
    }

    public void setResourceTags(Set<ResourceTag> resourceTags) {
        this.resourceTags = resourceTags;
    }


}
