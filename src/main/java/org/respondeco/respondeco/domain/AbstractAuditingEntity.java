package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.respondeco.respondeco.web.rest.mapping.DefaultReturnValue;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Base abstract class for entities which will hold definitions for created, last modified by and created,
 * last modified by date.
 */
@Data
@MappedSuperclass
@Audited
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DefaultReturnValue
    protected Long id;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50, updatable = false)
    protected String createdBy;

    @CreatedDate
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "created_date", nullable = false)
    protected DateTime createdDate = DateTime.now();

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    protected String lastModifiedBy;

    @LastModifiedDate
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "last_modified_date")
    protected DateTime lastModifiedDate = DateTime.now();

    @JsonIgnore
    @Column(name = "is_active")
    protected Boolean active = true;

    @Override
    public int hashCode() {
        return (int) (id != null ? (id ^ (id >>> 32)) : 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractAuditingEntity other = (AbstractAuditingEntity) o;

        if (id != null ? !id.equals(other.id) : other.id != null) return false;

        return true;
    }
}
