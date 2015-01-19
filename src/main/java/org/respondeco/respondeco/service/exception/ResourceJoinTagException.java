package org.respondeco.respondeco.service.exception;

import org.respondeco.respondeco.service.exception.enumException.EnumResourceJoinTag;

/**
 * Created by Roman Kern on 23.11.14.
 * General exception for handling Resource and Tag Join Entries
 */
public class ResourceJoinTagException extends GeneralResourceException {

    private EnumResourceJoinTag errorType;

    public EnumResourceJoinTag getErrorType(){ return  this.errorType;}

    public ResourceJoinTagException(String message, EnumResourceJoinTag errorType, Throwable t){
        super(message, t);
        this.errorType = errorType;
    }

    public ResourceJoinTagException(String message, EnumResourceJoinTag errorType){
        this(message, errorType, null);
    }
}
