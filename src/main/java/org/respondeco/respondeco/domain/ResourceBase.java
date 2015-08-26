package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.respondeco.respondeco.matching.MatchingEntity;
import org.respondeco.respondeco.matching.MatchingTag;
import org.respondeco.respondeco.web.rest.mapping.DefaultReturnValue;

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
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "T_RESOURCE")
@Inheritance(strategy = InheritanceType.JOINED)
public class ResourceBase extends AbstractAuditingEntity implements MatchingEntity {

    @NotNull
    @Column(length = 50)
    @DefaultReturnValue
    protected String name;

    @NotNull
    @Column(name = "original_amount")
    private BigDecimal originalAmount;

    @NotNull
    @Column(name = "amount", precision=10, scale=2)
    protected BigDecimal amount;

    @Column
    @NotNull
    protected String description;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private Image logo;

    @ManyToMany
    @JoinTable(name="T_RESOURCE_T_RESOURCETAG",
        joinColumns={@JoinColumn(name="T_RESOURCE_id", referencedColumnName = "id")},
        inverseJoinColumns={@JoinColumn(name="T_RESOURCETAG_id", referencedColumnName = "id")})
    private List<ResourceTag> resourceTags = new ArrayList<>();

    public void addResourceTag(ResourceTag tag) {
        this.resourceTags.add(tag);
    }

    @Override
    public Set<MatchingTag> getTags() {
        return new HashSet<>(resourceTags);
    }

}
