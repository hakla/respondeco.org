package org.respondeco.respondeco.domain;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by Roman on 13.11.14.
 * Describe the resource requirements for the project
 */
@Data
@Table
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ResourceRequirement extends ResourceBase {

    @NotNull
    private Long projectId;

    @NotNull
    private boolean isEssential;
}
