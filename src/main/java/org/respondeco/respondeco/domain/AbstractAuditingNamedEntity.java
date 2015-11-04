package org.respondeco.respondeco.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.respondeco.respondeco.web.rest.mapping.DefaultReturnValue;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Roman Kern on 13.11.14.
 * This class e
 */
@Getter
@Setter
@ToString(callSuper = true)
@MappedSuperclass
public abstract class AbstractAuditingNamedEntity extends AbstractAuditingEntity {

    @NotNull
    @Size(min = 3, max = 255)
    @Column(length = 255)
    @DefaultReturnValue
    private String name;
}
