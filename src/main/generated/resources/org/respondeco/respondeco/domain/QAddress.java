package org.respondeco.respondeco.domain;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAddress is a Querydsl query type for Address
 */
@Generated("com.mysema.query.codegen.EmbeddableSerializer")
public class QAddress extends BeanPath<Address> {

    private static final long serialVersionUID = 1155399220L;

    public static final QAddress address = new QAddress("address");

    public final StringPath city = createString("city");

    public final StringPath country = createString("country");

    public final StringPath extra = createString("extra");

    public final NumberPath<Double> latitude = createNumber("latitude", Double.class);

    public final NumberPath<Double> longitude = createNumber("longitude", Double.class);

    public final StringPath street = createString("street");

    public final NumberPath<Integer> streetNumber = createNumber("streetNumber", Integer.class);

    public final StringPath zipCode = createString("zipCode");

    public QAddress(String variable) {
        super(Address.class, forVariable(variable));
    }

    public QAddress(Path<? extends Address> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAddress(PathMetadata<?> metadata) {
        super(Address.class, metadata);
    }

}

