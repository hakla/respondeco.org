package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QOrganization is a Querydsl query type for Organization
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QOrganization extends EntityPathBase<Organization> {

    private static final long serialVersionUID = 1088549619L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOrganization organization = new QOrganization("organization");

    public final QAbstractAuditingEntity _super = new QAbstractAuditingEntity(this);

    //inherited
    public final BooleanPath active = _super.active;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate = _super.createdDate;

    public final StringPath description = createString("description");

    public final StringPath email = createString("email");

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isNpo = createBoolean("isNpo");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate = _super.lastModifiedDate;

    public final QImage logo;

    public final ListPath<User, QUser> members = this.<User, QUser>createList("members", User.class, QUser.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final QUser owner;

    public final QPostingFeed postingFeed;

    public final ListPath<Project, QProject> projects = this.<Project, QProject>createList("projects", Project.class, QProject.class, PathInits.DIRECT2);

    public final ListPath<ResourceMatch, QResourceMatch> resourceMatches = this.<ResourceMatch, QResourceMatch>createList("resourceMatches", ResourceMatch.class, QResourceMatch.class, PathInits.DIRECT2);

    public final ListPath<ResourceOffer, QResourceOffer> resourceOffers = this.<ResourceOffer, QResourceOffer>createList("resourceOffers", ResourceOffer.class, QResourceOffer.class, PathInits.DIRECT2);

    public final NumberPath<Long> spokesPerson = createNumber("spokesPerson", Long.class);

    public QOrganization(String variable) {
        this(Organization.class, forVariable(variable), INITS);
    }

    public QOrganization(Path<? extends Organization> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QOrganization(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QOrganization(PathMetadata<?> metadata, PathInits inits) {
        this(Organization.class, metadata, inits);
    }

    public QOrganization(Class<? extends Organization> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.logo = inits.isInitialized("logo") ? new QImage(forProperty("logo")) : null;
        this.owner = inits.isInitialized("owner") ? new QUser(forProperty("owner"), inits.get("owner")) : null;
        this.postingFeed = inits.isInitialized("postingFeed") ? new QPostingFeed(forProperty("postingFeed")) : null;
    }

}

