package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A ProfilePicture.
 */
@Data
@Entity
@Table(name = "T_PROFILEPICTURE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProfilePicture extends AbstractAuditingEntity implements Serializable {

    @Id
    @NotNull
    @Column(name = "userlogin")
    private String userlogin;

    @Column(name = "label")
    private String label;

    @Column(name = "data")
    @Lob
    private byte[] data;

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
