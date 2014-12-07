package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTextMessage is a Querydsl query type for TextMessage
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTextMessage extends EntityPathBase<TextMessage> {

    private static final long serialVersionUID = -442116006L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTextMessage textMessage = new QTextMessage("textMessage");

    public final QAbstractAuditingEntity _super = new QAbstractAuditingEntity(this);

    //inherited
    public final BooleanPath active = _super.active;

    public final StringPath content = createString("content");

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

    public final QUser receiver;

    public final QUser sender;

    public final DateTimePath<org.joda.time.DateTime> timestamp = createDateTime("timestamp", org.joda.time.DateTime.class);

    public QTextMessage(String variable) {
        this(TextMessage.class, forVariable(variable), INITS);
    }

    public QTextMessage(Path<? extends TextMessage> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTextMessage(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTextMessage(PathMetadata<?> metadata, PathInits inits) {
        this(TextMessage.class, metadata, inits);
    }

    public QTextMessage(Class<? extends TextMessage> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.receiver = inits.isInitialized("receiver") ? new QUser(forProperty("receiver"), inits.get("receiver")) : null;
        this.sender = inits.isInitialized("sender") ? new QUser(forProperty("sender"), inits.get("sender")) : null;
    }

}

