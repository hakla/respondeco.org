package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAbstractResourceTagEntity is a Querydsl query type for AbstractResourceTagEntity
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAbstractResourceTagEntity extends EntityPathBase<AbstractResourceTagEntity> {

    private static final long serialVersionUID = -930960915L;

    public static final QAbstractResourceTagEntity abstractResourceTagEntity = new QAbstractResourceTagEntity("abstractResourceTagEntity");

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

    public final NumberPath<Long> resourceTagId = createNumber("resourceTagId", Long.class);

    public QAbstractResourceTagEntity(String variable) {
        super(AbstractResourceTagEntity.class, forVariable(variable));
    }

    public QAbstractResourceTagEntity(Path<? extends AbstractResourceTagEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAbstractResourceTagEntity(PathMetadata<?> metadata) {
        super(AbstractResourceTagEntity.class, metadata);
    }

}

