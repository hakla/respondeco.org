package org.respondeco.respondeco.service.exception;

/**
 * Created by clemens on 29/11/14.
 */
public class IllegalValueException extends Exception {

    private String internationalizationKey;

    public IllegalValueException(String key, String message) {
        super(message);
        this.internationalizationKey = key;
    }

    public IllegalValueException(String key, String message, Throwable t) {
        super(message, t);
        this.internationalizationKey = key;
    }

    public String getInternationalizationKey() {
        return internationalizationKey;
    }

}
