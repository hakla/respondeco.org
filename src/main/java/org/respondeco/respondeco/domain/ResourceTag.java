package org.respondeco.respondeco.domain;

import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.respondeco.respondeco.matching.MatchingTag;
import org.respondeco.respondeco.service.exception.IllegalValueException;

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
public class ResourceTag extends AbstractAuditingEntity implements Serializable, MatchingTag {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy="resourceTags")
    private List<ResourceBase> resources;

    public void setName(@NonNull String name) {
        if(name.trim().isEmpty()){
            throw new IllegalValueException("resource.errors.notagname", "No name for resource tag defined");
        }
        this.name = name.trim();
    }

    public ResourceTag(Long id, String name){
        this.setId(id);
        this.setName(name);
    }

}
