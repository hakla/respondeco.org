package org.respondeco.respondeco.service.exception;

/**
 * Created by Benjamin Fraller on 19.01.2015.
 */
public class SocialMediaPermissionRevokedException extends IllegalValueException {

    public SocialMediaPermissionRevokedException(String key, String message) {
        super(key, message);
    }

}
