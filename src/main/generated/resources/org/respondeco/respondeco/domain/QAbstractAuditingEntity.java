package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAbstractAuditingEntity is a Querydsl query type for AbstractAuditingEntity
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAbstractAuditingEntity extends EntityPathBase<AbstractAuditingEntity> {

    private static final long serialVersionUID = -1429360788L;

    public static final QAbstractAuditingEntity abstractAuditingEntity = new QAbstractAuditingEntity("abstractAuditingEntity");

    public final BooleanPath active = createBoolean("active");

    public final StringPath createdBy = createString("createdBy");

    public final DateTimePath<org.joda.time.DateTime> createdDate = createDateTime("createdDate", org.joda.time.DateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastModifiedBy = createString("lastModifiedBy");

    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate = createDateTime("lastModifiedDate", org.joda.time.DateTime.class);

    public QAbstractAuditingEntity(String variable) {
        super(AbstractAuditingEntity.class, forVariable(variable));
    }

    public QAbstractAuditingEntity(Path<? extends AbstractAuditingEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAbstractAuditingEntity(PathMetadata<?> metadata) {
        super(AbstractAuditingEntity.class, metadata);
    }

}

