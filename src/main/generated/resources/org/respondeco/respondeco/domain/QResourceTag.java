package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QResourceTag is a Querydsl query type for ResourceTag
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QResourceTag extends EntityPathBase<ResourceTag> {

    private static final long serialVersionUID = 677421804L;

    public static final QResourceTag resourceTag = new QResourceTag("resourceTag");

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

    public final ListPath<ResourceBase, QResourceBase> resources = this.<ResourceBase, QResourceBase>createList("resources", ResourceBase.class, QResourceBase.class, PathInits.DIRECT2);

    public QResourceTag(String variable) {
        super(ResourceTag.class, forVariable(variable));
    }

    public QResourceTag(Path<? extends ResourceTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QResourceTag(PathMetadata<?> metadata) {
        super(ResourceTag.class, metadata);
    }

}

