package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


/**
 * A Organization.
 */
@Data
@Entity
@Table(name = "T_ORGANIZATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Organization extends AbstractAuditingEntity implements Serializable {

    @NotNull
    @Size(min = 0, max = 50)
    @Column(length = 50)
    private String name;

    @Column(length = 2048)
    private String description;

    @Email
    @Size(min = 0, max = 100)
    @Column(length = 100)
    private String email;

    @Column(name = "is_npo")
    private Boolean isNpo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    @Column(name = "spokesperson")
    private Long spokesPerson;

}
