package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QProject is a Querydsl query type for Project
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QProject extends EntityPathBase<Project> {

    private static final long serialVersionUID = 1993780569L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProject project = new QProject("project");

    public final QAbstractAuditingNamedEntity _super = new QAbstractAuditingNamedEntity(this);

    //inherited
    public final BooleanPath active = _super.active;

    public final BooleanPath concrete = createBoolean("concrete");

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

    public final QUser manager;

    //inherited
    public final StringPath name = _super.name;

    public final QOrganization organization;

    public final QPostingFeed postingFeed;

    public final QImage projectLogo;

    public final ListPath<PropertyTag, QPropertyTag> propertyTags = this.<PropertyTag, QPropertyTag>createList("propertyTags", PropertyTag.class, QPropertyTag.class, PathInits.DIRECT2);

    public final StringPath purpose = createString("purpose");

    public final ListPath<ResourceMatch, QResourceMatch> resourceMatches = this.<ResourceMatch, QResourceMatch>createList("resourceMatches", ResourceMatch.class, QResourceMatch.class, PathInits.DIRECT2);

    public final ListPath<ResourceRequirement, QResourceRequirement> resourceRequirements = this.<ResourceRequirement, QResourceRequirement>createList("resourceRequirements", ResourceRequirement.class, QResourceRequirement.class, PathInits.DIRECT2);

    public final DatePath<org.joda.time.LocalDate> startDate = createDate("startDate", org.joda.time.LocalDate.class);

    public final BooleanPath successful = createBoolean("successful");

    public QProject(String variable) {
        this(Project.class, forVariable(variable), INITS);
    }

    public QProject(Path<? extends Project> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QProject(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QProject(PathMetadata<?> metadata, PathInits inits) {
        this(Project.class, metadata, inits);
    }

    public QProject(Class<? extends Project> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.manager = inits.isInitialized("manager") ? new QUser(forProperty("manager"), inits.get("manager")) : null;
        this.organization = inits.isInitialized("organization") ? new QOrganization(forProperty("organization"), inits.get("organization")) : null;
        this.postingFeed = inits.isInitialized("postingFeed") ? new QPostingFeed(forProperty("postingFeed")) : null;
        this.projectLogo = inits.isInitialized("projectLogo") ? new QImage(forProperty("projectLogo")) : null;
    }

}

