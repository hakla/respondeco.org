package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QResourceBase is a Querydsl query type for ResourceBase
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QResourceBase extends EntityPathBase<ResourceBase> {

    private static final long serialVersionUID = -475296321L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResourceBase resourceBase = new QResourceBase("resourceBase");

    public final QAbstractAuditingEntity _super = new QAbstractAuditingEntity(this);

    //inherited
    public final BooleanPath active = _super.active;

    public final NumberPath<java.math.BigDecimal> amount = createNumber("amount", java.math.BigDecimal.class);

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QImage logo;

    public final StringPath name = createString("name");

    public final NumberPath<java.math.BigDecimal> originalAmount = createNumber("originalAmount", java.math.BigDecimal.class);

    public final ListPath<ResourceTag, QResourceTag> resourceTags = this.<ResourceTag, QResourceTag>createList("resourceTags", ResourceTag.class, QResourceTag.class, PathInits.DIRECT2);

    public QResourceBase(String variable) {
        this(ResourceBase.class, forVariable(variable), INITS);
    }

    public QResourceBase(Path<? extends ResourceBase> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QResourceBase(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QResourceBase(PathMetadata<?> metadata, PathInits inits) {
        this(ResourceBase.class, metadata, inits);
    }

    public QResourceBase(Class<? extends ResourceBase> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.logo = inits.isInitialized("logo") ? new QImage(forProperty("logo")) : null;
    }

}

