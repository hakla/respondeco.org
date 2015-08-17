package org.respondeco.respondeco.domain;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * An Image.
 */
@Entity
@Table(name = "T_IMAGE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Data
@ToString(exclude = { "data" })
public class Image extends AbstractAuditingNamedEntity implements Serializable {

    @Column(name = "data")
    @Lob
    private byte[] data;

}
