package org.respondeco.respondeco.service.exception;

import org.respondeco.respondeco.service.exception.enumException.EnumResourceException;

/**
 * Created by Roman Kern on 23.11.14.
 * Genereal exception for all Resource Operations
 */
public class ResourceNotFoundException extends IllegalValueException{

    public ResourceNotFoundException(String message){
        super("resource.errors.notfound", message);
    }


    public ResourceNotFoundException(String key, String message){
        super(key, message);
    }
}
