package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;


/**
 * A Posting.
 */

@Entity
@Table(name = "T_POSTING")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
@ToString(exclude = {"author","postingfeed"})
public class Posting extends AbstractAuditingEntity implements Serializable {

    @NotNull
    @Column(length = 2048)
    private String information;

    @NotNull
    @ManyToOne
    private User author;

    @ManyToOne
    @JoinColumn(name = "postingfeed_id")
    private PostingFeed postingfeed;

}
