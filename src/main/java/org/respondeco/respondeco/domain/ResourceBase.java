package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Roman Kern on 13.11.14.
 * Enable create resources offer
 */
@Audited
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class ResourceBase extends AbstractAuditingEntity implements Serializable {

    @NotNull
    @Column(name = "amount", precision=10, scale=2)
    protected BigDecimal amount;

    @Column(length = 255)
    @NotNull
    protected String description;

    @Transient
    private Set<ResourceTag> resourceTags = new HashSet<>();


    public void addResourceTag(ResourceTag tag){
        this.resourceTags.add(tag);
    }

    public Set<ResourceTag> getResourceTags() {
        return resourceTags;
    }

    public void setResourceTags(Set<ResourceTag> resourceTags) {
        this.resourceTags = resourceTags;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount(){
        return this.amount;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
