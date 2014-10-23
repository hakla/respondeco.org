package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A ProfilePicture.
 */
@Entity
@Table(name = "T_PROFILEPICTURE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProfilePicture extends AbstractAuditingEntity implements Serializable {

    @Id
    @Column(name = "userlogin")
    private String userlogin;

    @Column(name = "label")
    private String label;

    @Column(name = "data")
    @Lob
    private byte[] data;

    public String getUserlogin() {
        return userlogin;
    }

    public void setUserlogin(String userlogin) {
        this.userlogin = userlogin;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProfilePicture profilepicture = (ProfilePicture) o;

        if (userlogin != null ? !userlogin.equals(profilepicture.userlogin) : profilepicture.userlogin != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return userlogin.hashCode();
    }

    @Override
    public String toString() {
        return "ProfilePicture{" +
                ", userlogin='" + userlogin + "'" +
                ", label='" + label + "'" +
                ", data='" + data + "'" +
                '}';
    }
}
