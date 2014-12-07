package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QProjectRating is a Querydsl query type for ProjectRating
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QProjectRating extends EntityPathBase<ProjectRating> {

    private static final long serialVersionUID = 166077526L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectRating projectRating = new QProjectRating("projectRating");

    public final QAbstractAuditingEntity _super = new QAbstractAuditingEntity(this);

    //inherited
    public final BooleanPath active = _super.active;

    public final StringPath comment = createString("comment");

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

    public final QProject project;

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    public final QUser user;

    public QProjectRating(String variable) {
        this(ProjectRating.class, forVariable(variable), INITS);
    }

    public QProjectRating(Path<? extends ProjectRating> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QProjectRating(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QProjectRating(PathMetadata<?> metadata, PathInits inits) {
        this(ProjectRating.class, metadata, inits);
    }

    public QProjectRating(Class<? extends ProjectRating> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

