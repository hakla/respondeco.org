package org.respondeco.respondeco.service.exception;

/**
 * Created by Benjamin Fraller on 19.01.2015.
 */
public class SocialMediaPermissionRevokedException extends RuntimeException {

    public SocialMediaPermissionRevokedException(String key, String message) {
        super(message);
    }

}
