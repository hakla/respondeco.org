package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QResourceOffer is a Querydsl query type for ResourceOffer
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QResourceOffer extends EntityPathBase<ResourceOffer> {

    private static final long serialVersionUID = -1837141714L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResourceOffer resourceOffer = new QResourceOffer("resourceOffer");

    public final QResourceBase _super;

    //inherited
    public final BooleanPath active;

    //inherited
    public final NumberPath<java.math.BigDecimal> amount;

    //inherited
    public final StringPath createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate;

    //inherited
    public final StringPath description;

    public final DatePath<org.joda.time.LocalDate> endDate = createDate("endDate", org.joda.time.LocalDate.class);

    //inherited
    public final NumberPath<Long> id;

    public final BooleanPath isCommercial = createBoolean("isCommercial");

    //inherited
    public final StringPath lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate;

    // inherited
    public final QImage logo;

    //inherited
    public final StringPath name;

    public final QOrganization organization;

    //inherited
    public final NumberPath<java.math.BigDecimal> originalAmount;

    public final ListPath<ResourceMatch, QResourceMatch> resourceMatches = this.<ResourceMatch, QResourceMatch>createList("resourceMatches", ResourceMatch.class, QResourceMatch.class, PathInits.DIRECT2);

    //inherited
    public final ListPath<ResourceTag, QResourceTag> resourceTags;

    public final DatePath<org.joda.time.LocalDate> startDate = createDate("startDate", org.joda.time.LocalDate.class);

    public QResourceOffer(String variable) {
        this(ResourceOffer.class, forVariable(variable), INITS);
    }

    public QResourceOffer(Path<? extends ResourceOffer> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QResourceOffer(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QResourceOffer(PathMetadata<?> metadata, PathInits inits) {
        this(ResourceOffer.class, metadata, inits);
    }

    public QResourceOffer(Class<? extends ResourceOffer> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this._super = new QResourceBase(type, metadata, inits);
        this.active = _super.active;
        this.amount = _super.amount;
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.description = _super.description;
        this.id = _super.id;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
        this.logo = _super.logo;
        this.name = _super.name;
        this.organization = inits.isInitialized("organization") ? new QOrganization(forProperty("organization"), inits.get("organization")) : null;
        this.originalAmount = _super.originalAmount;
        this.resourceTags = _super.resourceTags;
    }

}

