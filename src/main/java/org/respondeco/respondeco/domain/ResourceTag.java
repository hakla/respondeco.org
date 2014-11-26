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

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Transient
    private Boolean isNewEntry;

    public boolean getIsNewEntry(){ return this.isNewEntry; }

    public void setIsNewEntry(boolean value) { this.isNewEntry = value; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name == null || name.trim().isEmpty() == true){
            throw new NullPointerException("Kein Name für resource Tag definiert");
        }
        this.name = name.trim();
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
