package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by clemens on 23/06/15.
 */

@Entity
@Table(name = "T_ISO_CATEGORY")
@Getter
@Setter
public class ISOCategory extends AbstractAuditingEntity {

    @OneToOne
    private ISOCategory superCategory;

    @NotNull
    @Size(max = 100)
    private String key;

}
