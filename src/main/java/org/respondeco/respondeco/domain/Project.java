package org.respondeco.respondeco.domain;

import lombok.Data;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Roman Kern on 15.11.14.
 * Definition of project
 */
@Data
@MappedSuperclass
public abstract class Project extends AbstractAuditingEntity implements Serializable {

    @NotNull
    @Column(nullable = false, length = 255)
    private String name;

    @NotNull
    @Column(length = 2048)
    private String purpose;

    @Lob
    private Byte[] picture;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime start;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    private DateTime end;
}
