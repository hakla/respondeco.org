package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A ResourceRequirementJoinResourceTag.
 */
@Entity
@Table(name = "T_RESOURCEREQUIREMENTJOINRESOURCETAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceRequirementJoinResourceTag extends AbstractResourceTagEntity implements Serializable {

    @Column(name = "resource_requirement_id")
    private Long resourceRequirementId;

    public Long getResourceRequirementId() {
        return resourceRequirementId;
    }

    public void setResourceRequirementId(Long resourceRequirementId) {
        this.resourceRequirementId = resourceRequirementId;
    }

    @Override
    public String toString() {
        return "ResourceRequirementJoinResourceTag{" +
                "id=" + id +
                ", resourceRequirementId='" + resourceRequirementId + "'" +
                ", resourceTagId='" + resourceTagId + "'" +
                '}';
    }
}
