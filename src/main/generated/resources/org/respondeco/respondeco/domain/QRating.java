package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QRating is a Querydsl query type for Rating
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QRating extends EntityPathBase<Rating> {

    private static final long serialVersionUID = 660211549L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QRating rating1 = new QRating("rating1");

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

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    public final QResourceMatch resourceMatch;

    public QRating(String variable) {
        this(Rating.class, forVariable(variable), INITS);
    }

    public QRating(Path<? extends Rating> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QRating(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QRating(PathMetadata<?> metadata, PathInits inits) {
        this(Rating.class, metadata, inits);
    }

    public QRating(Class<? extends Rating> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.resourceMatch = inits.isInitialized("resourceMatch") ? new QResourceMatch(forProperty("resourceMatch"), inits.get("resourceMatch")) : null;
    }

}

