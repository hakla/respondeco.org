package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QPosting is a Querydsl query type for Posting
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPosting extends EntityPathBase<Posting> {

    private static final long serialVersionUID = 1911889282L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPosting posting = new QPosting("posting");

    public final QAbstractAuditingEntity _super = new QAbstractAuditingEntity(this);

    //inherited
    public final BooleanPath active = _super.active;

    public final QUser author;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath information = createString("information");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QPostingFeed postingfeed;

    public final StringPath title = createString("title");

    public QPosting(String variable) {
        this(Posting.class, forVariable(variable), INITS);
    }

    public QPosting(Path<? extends Posting> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPosting(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPosting(PathMetadata<?> metadata, PathInits inits) {
        this(Posting.class, metadata, inits);
    }

    public QPosting(Class<? extends Posting> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.author = inits.isInitialized("author") ? new QUser(forProperty("author"), inits.get("author")) : null;
        this.postingfeed = inits.isInitialized("postingfeed") ? new QPostingFeed(forProperty("postingfeed")) : null;
    }

}

