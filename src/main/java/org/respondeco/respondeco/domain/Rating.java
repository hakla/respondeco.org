package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created by clemens on 07/12/14.
 */

@Entity
@Table(name = "T_RATING")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString(exclude = {"resourceMatch"})
public class Rating extends AbstractAuditingEntity {

    private Integer rating;
    private String comment;

    @OneToOne
    private ResourceMatch resourceMatch;

}
