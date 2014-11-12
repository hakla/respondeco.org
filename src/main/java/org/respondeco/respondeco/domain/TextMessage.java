package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import lombok.Data;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.respondeco.respondeco.domain.util.CustomLocalDateSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

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

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "sender")
    private String sender;

    @Column(name = "receiver")
    private String receiver;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name = "timestamp", nullable = false)
    private DateTime timestamp;

    @Column(name = "content")
    private String content;

    @Override
    public String toString() {
        return "TextMessage{" +
                "id=" + id +
                ", sender='" + sender + "'" +
                ", receiver='" + receiver + "'" +
                ", timestamp='" + timestamp + "'" +
                ", content='" + content + "'" +
                '}';
    }
}
