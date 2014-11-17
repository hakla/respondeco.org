package org.respondeco.respondeco.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A OrgJoinRequest.
 */
@Data
@Entity
@Table(name = "T_ORGJOINREQUEST")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OrgJoinRequest extends AbstractAuditingEntity implements Serializable {

    @NotNull
    @Column(name = "org_id")
    private Long orgId;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

}
