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

    public final QResourceBase _super = new QResourceBase(this);

    //inherited
    public final BooleanPath active = _super.active;

    //inherited
    public final NumberPath<java.math.BigDecimal> amount = _super.amount;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate = _super.createdDate;

    //inherited
    public final StringPath description = _super.description;

    public final DatePath<org.joda.time.LocalDate> endDate = createDate("endDate", org.joda.time.LocalDate.class);

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isCommercial = createBoolean("isCommercial");

    public final BooleanPath isRecurrent = createBoolean("isRecurrent");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate = _super.lastModifiedDate;

    //inherited
    public final StringPath name = _super.name;

    public final QOrganization organisation;

    //inherited
    public final ListPath<ResourceTag, QResourceTag> resourceTags = _super.resourceTags;

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
        this.organisation = inits.isInitialized("organisation") ? new QOrganization(forProperty("organisation"), inits.get("organisation")) : null;
    }

}

