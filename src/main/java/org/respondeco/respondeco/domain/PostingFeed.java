package org.respondeco.respondeco.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;


/**
 * A Posting.
 */
@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "T_POSTINGFEED")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PostingFeed extends AbstractAuditingEntity {

    @OneToMany(mappedBy = "postingfeed")
    private List<Posting> postings;
}
