package org.respondeco.respondeco.service.util;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.service.exception.IllegalValueException;

/**
 * Created by Clemens Puehringer on 18/08/15.
 */
public class Assert {

    public static void isNull(Object object, String key, String message) {
        if(object != null) {
            throw new IllegalValueException(key, message);
        }
    }

    public static void notNull(Object object, String key, String message) {
        if(object == null) {
            throw new IllegalValueException(key, message);
        }
    }

    public static void isValid(String string, String key, String message) {
        if(string == null || string.length() == 0) {
            throw new IllegalValueException(key, message);
        }
    }

    public static void isValid(String string, Integer min, Integer max, String key, String message) {
        if(string == null || string.length() == 0 || string.length() < min || string.length() > max) {
            throw new IllegalValueException(key, message);
        }
    }

    public static void isFalse(Boolean bool, String key, String message) {
        if(bool) {
            throw new IllegalValueException(key, message);
        }
    }

}
