package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A ProjectLogo.
 */
@Data
@Entity
@Table(name = "T_PROJECTLOGO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProjectLogo extends AbstractAuditingEntity implements Serializable {

    @Column(name = "data")
    @Lob
    private byte[] data;

}
