package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.joda.time.DateTime;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A TextMessage.
 */
@Data
@Entity
@Table(name = "T_TEXTMESSAGE")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TextMessage extends AbstractAuditingEntity implements Serializable {

    @ManyToOne
    private User sender;

    @JsonIgnore
    @ManyToOne
    private User receiver;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "timestamp", nullable = false)
    private DateTime timestamp;

    @Column(name = "content")
    private String content;

    @Column(name = "is_read")
    private boolean read;

}
