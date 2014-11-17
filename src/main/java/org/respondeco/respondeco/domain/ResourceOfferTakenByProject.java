package org.respondeco.respondeco.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by Roman Kern on 15.11.14.
 */
@Data
@Entity
@Table(name = "T_RESOURCEOFFER_TAKENBYPROJECT")
public class ResourceOfferTakenByProject extends AbstractAuditingEntity implements Serializable {

    @NotNull
    private BigDecimal amount;

    @NotNull
    @Column(name = "resource_offer_id", nullable = false)
    private Long resourceOfferId;

    @NotNull
    @Column(name = "resource_requirement_id", nullable = false)
    private Long resourceRequirementId;
}
