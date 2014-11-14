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

    @NotNull
    @Column(name = "user_id")
    private Long userId;

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

        if (userId != null ? !userId.equals(profilepicture.userId) : profilepicture.userId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public String toString() {
        return "ProfilePicture{" +
                ", userId='" + userId + "'" +
                ", label='" + label + "'" +
                ", data='" + data + "'" +
                '}';
    }
}
