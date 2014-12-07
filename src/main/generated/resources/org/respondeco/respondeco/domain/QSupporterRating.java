package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QSupporterRating is a Querydsl query type for SupporterRating
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSupporterRating extends EntityPathBase<SupporterRating> {

    private static final long serialVersionUID = 412161977L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSupporterRating supporterRating = new QSupporterRating("supporterRating");

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

    public final QOrganization organization;

    public final QProject project;

    public final NumberPath<Integer> rating = createNumber("rating", Integer.class);

    public final QUser user;

    public QSupporterRating(String variable) {
        this(SupporterRating.class, forVariable(variable), INITS);
    }

    public QSupporterRating(Path<? extends SupporterRating> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSupporterRating(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSupporterRating(PathMetadata<?> metadata, PathInits inits) {
        this(SupporterRating.class, metadata, inits);
    }

    public QSupporterRating(Class<? extends SupporterRating> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.organization = inits.isInitialized("organization") ? new QOrganization(forProperty("organization"), inits.get("organization")) : null;
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

