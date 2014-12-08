package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QPersistentToken is a Querydsl query type for PersistentToken
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPersistentToken extends EntityPathBase<PersistentToken> {

    private static final long serialVersionUID = -1109597214L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPersistentToken persistentToken = new QPersistentToken("persistentToken");

    public final StringPath ipAddress = createString("ipAddress");

    public final StringPath series = createString("series");

    public final DatePath<org.joda.time.LocalDate> tokenDate = createDate("tokenDate", org.joda.time.LocalDate.class);

    public final StringPath tokenValue = createString("tokenValue");

    public final QUser user;

    public final StringPath userAgent = createString("userAgent");

    public QPersistentToken(String variable) {
        this(PersistentToken.class, forVariable(variable), INITS);
    }

    public QPersistentToken(Path<? extends PersistentToken> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPersistentToken(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPersistentToken(PathMetadata<?> metadata, PathInits inits) {
        this(PersistentToken.class, metadata, inits);
    }

    public QPersistentToken(Class<? extends PersistentToken> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUser(forProperty("user"), inits.get("user")) : null;
    }

}

