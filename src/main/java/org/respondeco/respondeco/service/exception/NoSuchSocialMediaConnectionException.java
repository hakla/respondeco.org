package org.respondeco.respondeco.service.exception;

/**
 * Created by Benjamin Fraller on 13.01.2015.
 */
public class NoSuchSocialMediaConnectionException extends Exception{

    public NoSuchSocialMediaConnectionException(String message) {
        super(message);
    }

    public NoSuchSocialMediaConnectionException(String message, Throwable t) {
        super(message, t);
    }
}
