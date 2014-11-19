package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A ResourceTag.
 */
@Entity
@Table(name = "T_RESOURCETAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceTag extends AbstractAuditingEntity implements Serializable {

    @Column(name = "name")
    private String name;

    @Ignore
    private boolean isNewEntry;

    public boolean getIsNewEntry(){ return this.isNewEntry; }

    public void setIsNewEntry(boolean value) { this.isNewEntry = value; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceTag(){ }

    public ResourceTag(Long id, String name){
        this.setId(id);
        this.setName(name);
    }

    @Override
    public String toString() {
        return "ResourceTag{" +
                "id=" + id +
                ", name='" + name + "'" +
                ", createdBy='" + createdBy + "'" +
                ", createdDate='" + createdDate + "'" +
                ", lastModifiedBy='" + lastModifiedBy + "'" +
                ", lastModifiedDate='" + lastModifiedDate + "'" +
                ", isActive='" + active + "'" +
                '}';
    }

}
