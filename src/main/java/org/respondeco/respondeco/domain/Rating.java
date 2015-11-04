package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by clemens on 07/12/14.
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = {"resourceMatch"})
@Entity
@Table(name = "T_RATING")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Rating extends AbstractAuditingEntity {

    @Min(0)
    @Max(5)
    private Integer rating;
    private String comment;

    @OneToOne
    @JoinColumn(name = "match_id")
    private ResourceMatch resourceMatch;

}
