package org.respondeco.respondeco.service.exception;

/**
 * Created by Benjamin Fraller on 10.12.2014.
 */
public class MatchAlreadyExistsException extends Exception {

    private String internationalizationKey;

    public MatchAlreadyExistsException(String key, String message) {
        super(message);
        this.internationalizationKey = key;
    }

    public MatchAlreadyExistsException(String key, String message, Throwable t) {
        super(message, t);
        this.internationalizationKey = key;
    }

    public String getInternationalizationKey() {
        return internationalizationKey;
    }
}

