package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QProjectLocation is a Querydsl query type for ProjectLocation
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QProjectLocation extends EntityPathBase<ProjectLocation> {

    private static final long serialVersionUID = -2133964754L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProjectLocation projectLocation = new QProjectLocation("projectLocation");

    public final QAbstractAuditingEntity _super = new QAbstractAuditingEntity(this);

    //inherited
    public final BooleanPath active = _super.active;

    public final StringPath address = createString("address");

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

    public final NumberPath<Double> lat = createNumber("lat", Double.class);

    public final NumberPath<Double> lng = createNumber("lng", Double.class);

    public final QProject project;

    public QProjectLocation(String variable) {
        this(ProjectLocation.class, forVariable(variable), INITS);
    }

    public QProjectLocation(Path<? extends ProjectLocation> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QProjectLocation(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QProjectLocation(PathMetadata<?> metadata, PathInits inits) {
        this(ProjectLocation.class, metadata, inits);
    }

    public QProjectLocation(Class<? extends ProjectLocation> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
    }

}

