package org.respondeco.respondeco.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Project Location Object
 */
@Entity
@Data
@Table(name = "T_PROJECT_LOCATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProjectLocation extends AbstractAuditingEntity implements Serializable {

    @OneToOne
    @JoinColumn(name = "project_id")
    private Project project;

    private String address;

    private float lat;

    private float lng;

}
