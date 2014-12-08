package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by Clemens Puehringer on 07/12/14.
 */


@Entity
@Table(name = "T_RESOURCE_MATCH")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString(exclude = {"projectRating", "supporterRating", "resourceOffer",
    "resourceRequirement", "organization", "project"})
public class ResourceMatch extends AbstractAuditingEntity {

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_direction")
    private MatchDirection matchDirection;

    private Boolean accepted = false;

    @OneToOne
    @JoinColumn(name = "project_rating_id")
    private Rating projectRating;

    @OneToOne
    @JoinColumn(name = "supporter_rating_id")
    private Rating supporterRating;

    @ManyToOne
    @JoinColumn(name = "resource_offer_id")
    private ResourceOffer resourceOffer;

    @ManyToOne
    @JoinColumn(name = "resource_requirement_id")
    private ResourceRequirement resourceRequirement;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;



}
