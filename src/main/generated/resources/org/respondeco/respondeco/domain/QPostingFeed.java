package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QPostingFeed is a Querydsl query type for PostingFeed
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPostingFeed extends EntityPathBase<PostingFeed> {

    private static final long serialVersionUID = 258467392L;

    public static final QPostingFeed postingFeed = new QPostingFeed("postingFeed");

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

    public final ListPath<Posting, QPosting> postings = this.<Posting, QPosting>createList("postings", Posting.class, QPosting.class, PathInits.DIRECT2);

    public QPostingFeed(String variable) {
        super(PostingFeed.class, forVariable(variable));
    }

    public QPostingFeed(Path<? extends PostingFeed> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPostingFeed(PathMetadata<?> metadata) {
        super(PostingFeed.class, metadata);
    }

}

