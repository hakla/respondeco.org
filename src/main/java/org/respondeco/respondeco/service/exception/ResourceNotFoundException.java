package org.respondeco.respondeco.service.exception;

/**
 * Created by Roman Kern on 23.11.14.
 * Genereal exception for all Resource Operations
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message){
        super(message);
    }


    public ResourceNotFoundException(String key, String message){
        super(message);
    }
}
