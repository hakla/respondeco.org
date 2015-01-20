package org.respondeco.respondeco.service.exception;

/**
 * SocialMediaApiConnectionException
 *
 * Represents an error between our backend and an SocialMedia API Server which
 * may occur during communication via REST.
 */
public class SocialMediaApiConnectionException extends Exception {

    public SocialMediaApiConnectionException(String message) {
        super(message);
    }
}
