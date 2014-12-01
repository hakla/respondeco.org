package org.respondeco.respondeco.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.sf.cglib.core.Local;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.respondeco.respondeco.web.rest.dto.util.CustomLocalDateDeserializer;
import org.respondeco.respondeco.web.rest.dto.util.CustomLocalDateSerializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * A ResourceOffer.
 */
@Entity
@Table(name = "T_RESOURCEOFFER")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@PrimaryKeyJoinColumn(name = "resource_id", referencedColumnName = "id")
@Getter
@Setter
@ToString
public class ResourceOffer extends ResourceBase implements Serializable {

    @ManyToOne
    @JoinColumn(name = "organisation_id")
    private Organization organisation;

    @Column(name = "is_commercial", nullable = false)
    private Boolean isCommercial = false;

    @Column(name = "is_recurrent")
    private Boolean isRecurrent = false;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "start_date")
    private LocalDate startDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "end_date")
    private LocalDate endDate;
}
