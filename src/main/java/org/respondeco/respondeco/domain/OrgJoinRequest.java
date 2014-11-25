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
    @ManyToOne
    @JoinColumn(name="org_id")
    private Organization organization;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
