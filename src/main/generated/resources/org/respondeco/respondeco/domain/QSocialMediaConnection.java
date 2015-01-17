package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QSocialMediaConnection is a Querydsl query type for SocialMediaConnection
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QSocialMediaConnection extends EntityPathBase<SocialMediaConnection> {

    private static final long serialVersionUID = 1889069301L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSocialMediaConnection socialMediaConnection = new QSocialMediaConnection("socialMediaConnection");

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

    public final StringPath provider = createString("provider");

    public final StringPath secret = createString("secret");

    public final StringPath token = createString("token");

    public final QUser user;

    public QSocialMediaConnection(String variable) {
        this(SocialMediaConnection.class, forVariable(variable), INITS);
    }

    public QSocialMediaConnection(Path<? extends SocialMediaConnection> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSocialMediaConnection(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QSocialMediaConnection(PathMetadata<?> metadata, PathInits inits) {
        this(SocialMediaConnection.class, metadata, inits);
    }

    public QSocialMediaConnection(Class<? extends SocialMediaConnection> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

