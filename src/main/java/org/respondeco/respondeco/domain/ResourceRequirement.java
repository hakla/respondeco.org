package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
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



    @ManyToOne
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Project project;

    @ManyToMany(mappedBy = "resourceRequirements")
    @JsonIgnore
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<ResourceOfferJoinResourceRequirement> resourceOfferJoinResourceRequirements = new HashSet<>();

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

    public Project getProjects() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
    public Set<ResourceOfferJoinResourceRequirement> getResourceOfferJoinResourceRequirements() {
        return resourceOfferJoinResourceRequirements;
    }

    public void setResourceOfferJoinResourceRequirements(Set<ResourceOfferJoinResourceRequirement> resourceOfferJoinResourceRequirements) {
        this.resourceOfferJoinResourceRequirements = resourceOfferJoinResourceRequirements;
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
