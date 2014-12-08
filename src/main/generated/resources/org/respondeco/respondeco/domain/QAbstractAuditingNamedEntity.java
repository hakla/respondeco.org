package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAbstractAuditingNamedEntity is a Querydsl query type for AbstractAuditingNamedEntity
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAbstractAuditingNamedEntity extends EntityPathBase<AbstractAuditingNamedEntity> {

    private static final long serialVersionUID = -339502445L;

    public static final QAbstractAuditingNamedEntity abstractAuditingNamedEntity = new QAbstractAuditingNamedEntity("abstractAuditingNamedEntity");

    public final QAbstractAuditingEntity _super = new QAbstractAuditingEntity(this);

    //inherited
    public final BooleanPath active = _super.active;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath name = createString("name");

    public QAbstractAuditingNamedEntity(String variable) {
        super(AbstractAuditingNamedEntity.class, forVariable(variable));
    }

    public QAbstractAuditingNamedEntity(Path<? extends AbstractAuditingNamedEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAbstractAuditingNamedEntity(PathMetadata<?> metadata) {
        super(AbstractAuditingNamedEntity.class, metadata);
    }

}

