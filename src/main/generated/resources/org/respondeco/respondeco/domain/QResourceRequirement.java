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

    public final QResourceBase _super;

    //inherited
    public final BooleanPath active;

    //inherited
    public final NumberPath<java.math.BigDecimal> amount;

    //inherited
    public final StringPath createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate;

    //inherited
    public final StringPath description;

    //inherited
    public final NumberPath<Long> id;

    public final BooleanPath isEssential = createBoolean("isEssential");

    //inherited
    public final StringPath lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate;

    // inherited
    public final QImage logo;

    //inherited
    public final StringPath name;

    //inherited
    public final NumberPath<java.math.BigDecimal> originalAmount;

    public final QProject project;

    public final ListPath<ResourceMatch, QResourceMatch> resourceMatches = this.<ResourceMatch, QResourceMatch>createList("resourceMatches", ResourceMatch.class, QResourceMatch.class, PathInits.DIRECT2);

    //inherited
    public final ListPath<ResourceTag, QResourceTag> resourceTags;

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
        this._super = new QResourceBase(type, metadata, inits);
        this.active = _super.active;
        this.amount = _super.amount;
        this.createdBy = _super.createdBy;
        this.createdDate = _super.createdDate;
        this.description = _super.description;
        this.id = _super.id;
        this.lastModifiedBy = _super.lastModifiedBy;
        this.lastModifiedDate = _super.lastModifiedDate;
        this.logo = _super.logo;
        this.name = _super.name;
        this.originalAmount = _super.originalAmount;
        this.project = inits.isInitialized("project") ? new QProject(forProperty("project"), inits.get("project")) : null;
        this.resourceTags = _super.resourceTags;
    }

}

