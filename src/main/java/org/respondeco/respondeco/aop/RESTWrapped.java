package org.respondeco.respondeco.aop;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by klaus.harrer on 13.04.15.
 */
@Component
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RESTWrapped {
    HttpStatus returnStatus() default HttpStatus.OK;
}
