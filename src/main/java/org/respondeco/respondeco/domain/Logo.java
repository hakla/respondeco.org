package org.respondeco.respondeco.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A Logo.
 */
@Data
@Entity
@Table(name = "T_LOGO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Logo extends AbstractAuditingEntity implements Serializable {

    @Id
    @NotNull
    @Column(name = "org_name")
    private String orgName;

    @Column(name = "label")
    private String label;

    @Column(name = "data")
    @Lob
    private  byte[] data;

}
