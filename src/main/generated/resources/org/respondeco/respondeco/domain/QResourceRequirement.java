package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QResourceRequirement is a Querydsl query type for ResourceRequirement
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QResourceRequirement extends EntityPathBase<ResourceRequirement> {

    private static final long serialVersionUID = 744508821L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResourceRequirement resourceRequirement = new QResourceRequirement("resourceRequirement");

    public final QResourceBase _super = new QResourceBase(this);

    //inherited
    public final BooleanPath active = _super.active;

    //inherited
    public final NumberPath<java.math.BigDecimal> amount = _super.amount;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate = _super.createdDate;

    //inherited
    public final StringPath description = _super.description;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final BooleanPath isEssential = createBoolean("isEssential");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate = _super.lastModifiedDate;

    //inherited
    public final StringPath name = _super.name;

    public final QProject project;

    public final ListPath<ResourceOffer, QResourceOffer> resourceOffers = this.<ResourceOffer, QResourceOffer>createList("resourceOffers", ResourceOffer.class, QResourceOffer.class, PathInits.DIRECT2);

    //inherited
    public final ListPath<ResourceTag, QResourceTag> resourceTags = _super.resourceTags;

    public QResourceRequirement(String variable) {
        this(ResourceRequirement.class, forVariable(variable), INITS);
    }

    public QResourceRequirement(Path<? extends ResourceRequirement> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QResourceRequirement(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QResourceRequirement(PathMetadata<?> metadata, PathInits inits) {
        this(ResourceRequirement.class, metadata, inits);
    }

    public QResourceRequirement(Class<? extends ResourceRequirement> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
    }

}

