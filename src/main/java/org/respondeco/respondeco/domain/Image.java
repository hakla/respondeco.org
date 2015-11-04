package org.respondeco.respondeco.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * An Image.
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = { "data" })
@Entity
@Table(name = "T_IMAGE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Image extends AbstractAuditingNamedEntity {

    @Column(name = "data")
    @Lob
    private byte[] data;

}
