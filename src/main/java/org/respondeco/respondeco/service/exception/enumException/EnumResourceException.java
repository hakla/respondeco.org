package org.respondeco.respondeco.service.exception.enumException;

/**
 * Created by Roman Kern on 23.11.14.
 * Define simple states of the Exception.
 * */
public enum EnumResourceException {
    /*
    Accures on CREATE, where current item already exists
     */
    ALREADY_EXISTS,
    /*
    Accures when searching Item is not found in DB
     */
    NOT_FOUND,
    /*
    Exception on create Statement
     */
    CREATE,
    /*
    Exception on update Statement
     */
    UPDATE,
    /*
    Exception on delete (or set inactive) Statement
     */
    DELETE,
    /*
    Current User is not authorized to execute the service
     */
    USER_NOT_AUTHORIZED
}
