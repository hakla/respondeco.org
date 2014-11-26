package org.respondeco.respondeco.service.exception;

import org.respondeco.respondeco.service.exception.enumException.EnumResourceTagException;

/**
 * Created by Roman Kern on 23.11.14.
 */
public class ResourceTagException extends GeneralResourceException {

    private EnumResourceTagException errorType;

    public EnumResourceTagException getErrorType(){
        return this.errorType;
    }

    public ResourceTagException(String message, EnumResourceTagException errorType, Throwable t){
        super(message, t);
        this.errorType = errorType;
    }

    public ResourceTagException(String message, EnumResourceTagException errorType){
        this(message, errorType, null);
    }
}
