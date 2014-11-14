package org.respondeco.respondeco.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A OrgJoinRequest.
 */
@Data
@Entity
@Table(name = "T_ORGJOINREQUEST")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OrgJoinRequest extends AbstractAuditingEntity implements Serializable {

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "user_login")
    private String userLogin;

}
