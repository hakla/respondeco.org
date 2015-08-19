package org.respondeco.respondeco.service.exception;

import lombok.Getter;

/**
 * Created by clemens on 29/11/14.
 */
public class IllegalValueException extends RuntimeException {

    @Getter
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

    public ExceptionObject getObject() {
        return new ExceptionObject(getMessage(), getInternationalizationKey());
    }

    private static class ExceptionObject {
        private final String message;
        private final String key;

        public ExceptionObject(String message, String key) {
            this.message = message;
            this.key = key;
        }
    }

}
