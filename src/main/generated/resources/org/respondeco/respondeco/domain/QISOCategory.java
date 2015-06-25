package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QISOCategory is a Querydsl query type for ISOCategory
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QISOCategory extends EntityPathBase<ISOCategory> {

    private static final long serialVersionUID = -329014557L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QISOCategory iSOCategory = new QISOCategory("iSOCategory");

    public final QAbstractAuditingEntity _super = new QAbstractAuditingEntity(this);

    //inherited
    public final BooleanPath active = _super.active;

    //inherited
    public final StringPath createdBy = _super.createdBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> createdDate = _super.createdDate;

    //inherited
    public final NumberPath<Long> id = _super.id;

    public final StringPath key = createString("key");

    //inherited
    public final StringPath lastModifiedBy = _super.lastModifiedBy;

    //inherited
    public final DateTimePath<org.joda.time.DateTime> lastModifiedDate = _super.lastModifiedDate;

    public final ListPath<Organization, QOrganization> organizations = this.<Organization, QOrganization>createList("organizations", Organization.class, QOrganization.class, PathInits.DIRECT2);

    public final ListPath<ISOCategory, QISOCategory> subCategories = this.<ISOCategory, QISOCategory>createList("subCategories", ISOCategory.class, QISOCategory.class, PathInits.DIRECT2);

    public final QISOCategory superCategory;

    public QISOCategory(String variable) {
        this(ISOCategory.class, forVariable(variable), INITS);
    }

    public QISOCategory(Path<? extends ISOCategory> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QISOCategory(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QISOCategory(PathMetadata<?> metadata, PathInits inits) {
        this(ISOCategory.class, metadata, inits);
    }

    public QISOCategory(Class<? extends ISOCategory> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.superCategory = inits.isInitialized("superCategory") ? new QISOCategory(forProperty("superCategory"), inits.get("superCategory")) : null;
    }

}

