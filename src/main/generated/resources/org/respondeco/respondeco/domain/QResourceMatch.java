package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QResourceMatch is a Querydsl query type for ResourceMatch
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QResourceMatch extends EntityPathBase<ResourceMatch> {

    private static final long serialVersionUID = -1839124329L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResourceMatch resourceMatch = new QResourceMatch("resourceMatch");

    public final QAbstractAuditingEntity _super = new QAbstractAuditingEntity(this);

    public final BooleanPath accepted = createBoolean("accepted");

    //inherited
    public final BooleanPath active = _super.active;

    public final NumberPath<Integer> amount = createNumber("amount", Integer.class);

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

    public final EnumPath<MatchDirection> matchDirection = createEnum("matchDirection", MatchDirection.class);

    public final QOrganization organization;

    public final QProject project;

    public final QRating projectRating;

    public final QResourceOffer resourceOffer;

    public final QResourceRequirement resourceRequirement;

    public final QRating supporterRating;

    public QResourceMatch(String variable) {
        this(ResourceMatch.class, forVariable(variable), INITS);
    }

    public QResourceMatch(Path<? extends ResourceMatch> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QResourceMatch(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QResourceMatch(PathMetadata<?> metadata, PathInits inits) {
        this(ResourceMatch.class, metadata, inits);
    }

    public QResourceMatch(Class<? extends ResourceMatch> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.organization = inits.isInitialized("organization") ? new QOrganization(forProperty("organization"), inits.get("organization")) : null;
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
        this.projectRating = inits.isInitialized("projectRating") ? new QRating(forProperty("projectRating"), inits.get("projectRating")) : null;
        this.resourceOffer = inits.isInitialized("resourceOffer") ? new QResourceOffer(forProperty("resourceOffer"), inits.get("resourceOffer")) : null;
        this.resourceRequirement = inits.isInitialized("resourceRequirement") ? new QResourceRequirement(forProperty("resourceRequirement"), inits.get("resourceRequirement")) : null;
        this.supporterRating = inits.isInitialized("supporterRating") ? new QRating(forProperty("supporterRating"), inits.get("supporterRating")) : null;
    }

}

