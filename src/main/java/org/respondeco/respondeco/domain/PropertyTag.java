package org.respondeco.respondeco.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A PropertyTag.
 */
@Data
@Entity
@Table(name = "T_PROPERTYTAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PropertyTag extends AbstractAuditingEntity implements Serializable {

    @Column(name = "name")
    private String name;

}
