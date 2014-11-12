package org.respondeco.respondeco.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A OrgJoinRequest.
 */
@Entity
@Table(name = "T_ORGJOINREQUEST")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class OrgJoinRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "user_login")
    private String userLogin;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OrgJoinRequest orgjoinrequest = (OrgJoinRequest) o;

        if (id != null ? !id.equals(orgjoinrequest.id) : orgjoinrequest.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "OrgJoinRequest{" +
                "id=" + id +
                ", orgId='" + orgId + "'" +
                ", userLogin='" + userLogin + "'" +
                '}';
    }
}
