package org.respondeco.respondeco.web.rest.mapping;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by clemens on 11/03/15.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface DefaultReturnValue {
    int maxDepth() default Integer.MAX_VALUE;
    String useName() default "";
}

