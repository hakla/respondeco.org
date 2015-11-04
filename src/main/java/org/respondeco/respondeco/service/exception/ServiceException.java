package org.respondeco.respondeco.service.exception;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clemens on 01/10/15.
 */
public abstract class ServiceException extends RuntimeException {

    @Getter
    private Details details;

    public ServiceException(String key, String message, List<String> detailKeys, List<Serializable> detailValues) {
        super(String.format(message, detailValues));
        details = new Details(key, getMessage(), detailKeys, detailValues);
    }

    public ServiceException(String key, String message,
                            List<String> detailKeys, List<Serializable> detailValues, Throwable t) {
        super(String.format(message, detailValues), t);
        details = new Details(key, getMessage(), detailKeys, detailValues);
    }

    @Override
    public String toString() {
        return this.details.toString();
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Getter
    public static class ErrorPrefix {

        private static final String ERROR_COMPONENT = "error";

        private final String key;

        public ErrorPrefix(String prefix) {
            this.key = String.join(".", prefix, ERROR_COMPONENT);
        }

        public String join(String postfix) {
            return new ErrorPrefix(String.join(".", key, postfix)).getKey();
        }

    }

    @Getter
    @ToString
    public static class Details {
        private final String key;
        private final String message;
        private final Map<String, Serializable> details;

        public Details(String key, String message, List<String> detailKeys,
                       List<Serializable> detailValues) {
            this.key = key;
            this.message = message;
            this.details = new HashMap<>();
            if(detailKeys != null && detailValues != null) {
                if (detailKeys.size() != detailValues.size()) {
                    throw new IllegalArgumentException("detail keys and values cannot be of different size");
                }
                for (Integer i = 0; i < detailKeys.size(); i++) {
                    this.details.put(detailKeys.get(i), detailValues.get(i));
                }
            }
        }
    }
}
