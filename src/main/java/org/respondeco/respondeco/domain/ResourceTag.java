package org.respondeco.respondeco.domain;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * A ResourceTag.
 */
@Entity
@Table(name = "T_RESOURCETAG")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResourceTag extends AbstractAuditingEntity implements Serializable {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany
    @JoinTable(name="T_RESOURCE_T_RESOURCETAG",
        joinColumns={@JoinColumn(name="T_RESOURCETAG_id", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="T_RESOURCE_id", referencedColumnName = "id")})
    private List<ResourceBase> resources;

    public void setName(@NonNull String name) {
        if(name.trim().isEmpty() == true){
            throw new IllegalArgumentException("Kein Name für resource Tag definiert");
        }
        this.name = name.trim();
    }

    public ResourceTag(Long id, String name){
        this.setId(id);
        this.setName(name);
    }

}
