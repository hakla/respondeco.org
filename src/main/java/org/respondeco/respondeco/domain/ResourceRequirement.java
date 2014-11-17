package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.respondeco.respondeco.domain.util.CustomDateTimeDeserializer;
import org.respondeco.respondeco.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A ResourceRequirement.
 */
@Entity
@Table(name = "T_RESOURCEREQUIREMENT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceRequirement extends ResourceBase implements Serializable {

    @Column(name = "amount", precision=10, scale=2)
    private BigDecimal amount;

    @Column(name = "description")
    private String description;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "is_essential")
    private Boolean isEssential;

    /*
    @ManyToMany(mappedBy = "resourceRequirements")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ResourceTag> resourceTags = new HashSet<>();

    @OneToMany(mappedBy = "resourceRequirement")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Project> projects = new HashSet<>();

    @ManyToMany(mappedBy = "resourceRequirements")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ResourceOfferJoinResourceRequirement> resourceOfferJoinResourceRequirements = new HashSet<>();

        public BigDecimal getAmount() {
        return amount;
    }
    */

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Boolean getIsEssential() {
        return isEssential;
    }

    public void setIsEssential(Boolean isEssential) {
        this.isEssential = isEssential;
    }

    /*
    public Set<ResourceTag> getResourceTags() {
        return resourceTags;
    }

    public void setResourceTags(Set<ResourceTag> resourceTags) {
        this.resourceTags = resourceTags;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }
    public Set<ResourceOfferJoinResourceRequirement> getResourceOfferJoinResourceRequirements() {
        return resourceOfferJoinResourceRequirements;
    }

    public void setResourceOfferJoinResourceRequirements(Set<ResourceOfferJoinResourceRequirement> resourceOfferJoinResourceRequirements) {
        this.resourceOfferJoinResourceRequirements = resourceOfferJoinResourceRequirements;
    }
    */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResourceRequirement resourceRequirement = (ResourceRequirement) o;

        if (id != null ? !id.equals(resourceRequirement.id) : resourceRequirement.id != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "ResourceRequirement{" +
                "id=" + id +
                ", amount='" + amount + "'" +
                ", description='" + description + "'" +
                ", projectId='" + projectId + "'" +
                ", isEssential='" + isEssential + "'" +
                ", createdBy='" + createdBy + "'" +
                ", createdDate='" + createdDate + "'" +
                ", lastModifiedBy='" + lastModifiedBy + "'" +
                ", lastModifiedDate='" + lastModifiedDate + "'" +
                ", isActive='" + active + "'" +
                '}';
    }
}
