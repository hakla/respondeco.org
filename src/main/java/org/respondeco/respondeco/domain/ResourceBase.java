package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Roman Kern on 13.11.14.
 * Enable create resources offer
 */
@Entity
@Table(name = "T_RESOURCE")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@ToString
public class ResourceBase extends AbstractAuditingEntity implements Serializable {

    @NotNull
    @Column(length = 50)
    protected String name;

    @NotNull
    @Column(name = "amount", precision=10, scale=2)
    protected BigDecimal amount;

    @Column
    @Type(type = "clob")
    @NotNull
    protected String description;

    @ManyToMany
    @JoinTable(name="T_RESOURCE_T_RESOURCETAG",
        joinColumns={@JoinColumn(name="T_RESOURCE_id", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="T_RESOURCETAG_id", referencedColumnName = "id")})
    private List<ResourceTag> resourceTags = new ArrayList<>();

    public void addResourceTag(ResourceTag tag) {
        this.resourceTags.add(tag);
    }

}
