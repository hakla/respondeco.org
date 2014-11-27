package org.respondeco.respondeco.service.exception;

/**
 * Created by Roman Kern on 25.11.14.
 */
public class GeneralResourceException extends Exception {

    public GeneralResourceException(String message){
        this(message, null);
    }
    public GeneralResourceException(String message, Throwable t){
        super(message, t);
    }
}
