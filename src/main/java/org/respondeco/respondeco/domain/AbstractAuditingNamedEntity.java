package org.respondeco.respondeco.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Roman Kern on 13.11.14.
 * This class e
 */
@Data
@MappedSuperclass
public abstract class AbstractAuditingNamedEntity extends AbstractAuditingEntity {

    @NotNull
    @Size(min = 0, max = 50)
    @Column(length = 50)
    private String name;
}
