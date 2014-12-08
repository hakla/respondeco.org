package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QPropertyTag is a Querydsl query type for PropertyTag
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPropertyTag extends EntityPathBase<PropertyTag> {

    private static final long serialVersionUID = 800481221L;

    public static final QPropertyTag propertyTag = new QPropertyTag("propertyTag");

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

    public final ListPath<Project, QProject> projects = this.<Project, QProject>createList("projects", Project.class, QProject.class, PathInits.DIRECT2);

    public QPropertyTag(String variable) {
        super(PropertyTag.class, forVariable(variable));
    }

    public QPropertyTag(Path<? extends PropertyTag> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPropertyTag(PathMetadata<?> metadata) {
        super(PropertyTag.class, metadata);
    }

}

