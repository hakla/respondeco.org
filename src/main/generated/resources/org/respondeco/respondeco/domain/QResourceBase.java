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

    public final StringPath name = createString("name");

    public final NumberPath<Integer> originalAmount = createNumber("originalAmount", Integer.class);

    public final ListPath<ResourceTag, QResourceTag> resourceTags = this.<ResourceTag, QResourceTag>createList("resourceTags", ResourceTag.class, QResourceTag.class, PathInits.DIRECT2);

    public QResourceBase(String variable) {
        super(ResourceBase.class, forVariable(variable));
    }

    public QResourceBase(Path<? extends ResourceBase> path) {
        super(path.getType(), path.getMetadata());
    }

    public QResourceBase(PathMetadata<?> metadata) {
        super(ResourceBase.class, metadata);
    }

}

