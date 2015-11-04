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
 * A OrgJoinRequest.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "T_ORGJOINREQUEST")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OrgJoinRequest extends AbstractAuditingEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name="org_id")
    private Organization organization;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
