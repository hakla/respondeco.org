package org.respondeco.respondeco.service.exception;

import org.respondeco.respondeco.service.exception.enumException.EnumResourceException;

/**
 * Created by Roman Kern on 23.11.14.
 * Genereal exception for all Resource Operations
 */
public class ResourceException extends Exception{

    private EnumResourceException errorType;

    public EnumResourceException getErrorType(){
        return this.errorType;
    }

    public ResourceException(String message, EnumResourceException errorType){
        this(message, errorType, null);
    }
    public ResourceException(String message, EnumResourceException errorType, Throwable t){
        super(message, t);
        this.errorType = errorType;
    }
}
