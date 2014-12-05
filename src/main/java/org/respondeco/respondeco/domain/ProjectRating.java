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
@Table(name = "T_PROJECT_RATING")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString
public class ProjectRating extends AbstractAuditingEntity {

    @NotNull
    private Integer rating;

    private String comment;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
