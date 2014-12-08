package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QOrgJoinRequest is a Querydsl query type for OrgJoinRequest
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QOrgJoinRequest extends EntityPathBase<OrgJoinRequest> {

    private static final long serialVersionUID = 314409537L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrgJoinRequest orgJoinRequest = new QOrgJoinRequest("orgJoinRequest");

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

    public final QOrganization organization;

    public final QUser user;

    public QOrgJoinRequest(String variable) {
        this(OrgJoinRequest.class, forVariable(variable), INITS);
    }

    public QOrgJoinRequest(Path<? extends OrgJoinRequest> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QOrgJoinRequest(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QOrgJoinRequest(PathMetadata<?> metadata, PathInits inits) {
        this(OrgJoinRequest.class, metadata, inits);
    }

    public QOrgJoinRequest(Class<? extends OrgJoinRequest> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.organization = inits.isInitialized("organization") ? new QOrganization(forProperty("organization"), inits.get("organization")) : null;
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

