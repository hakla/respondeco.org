package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */


@Entity
@Table(name = "T_SUPPORTER_RATING")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString
public class SupporterRating extends AbstractAuditingEntity {

    @NotNull
    private Integer rating;

    private String comment;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
