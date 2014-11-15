package org.respondeco.respondeco.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Roman Kern on 13.11.14.
 * Enable create resources offer
 */
@Data
@Audited
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class ResourceBase extends AbstractAuditingEntity implements Serializable {

    @NotNull
    private BigDecimal amount;

    @Column(length = 255)
    @NotNull
    private String description;

    @NotNull
    @Column(name = "resource_tag")
    private Long resourceTag;
}
