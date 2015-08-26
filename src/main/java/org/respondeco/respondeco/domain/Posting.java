package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;
import org.respondeco.respondeco.web.rest.mapping.DefaultReturnValue;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


/**
 * A Posting.
 */
@Getter
@Setter
@ToString(callSuper = true, exclude = {"author","postingfeed"})
@Entity
@Table(name = "T_POSTING")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Posting extends AbstractAuditingEntity {

    @NotNull
    @Column(length = 2048)
    @DefaultReturnValue
    private String information;

    @NotNull
    @ManyToOne
    @DefaultReturnValue
    private User author;

    @NotNull
    @DefaultReturnValue
    private String title;

    @ManyToOne
    @JoinColumn(name = "postingfeed_id")
    private PostingFeed postingfeed;

}
