package org.respondeco.respondeco.service.exception;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clemens on 29/11/14.
 */
public class IllegalValueException extends ServiceException {

    public IllegalValueException(String key, String message, List<String> detailKeys,
                                 List<Serializable> detailValues) {
        super(key, message, detailKeys, detailValues);
    }

    public IllegalValueException(String key, String message, List<String> detailKeys,
                                 List<Serializable> detailValues, Throwable t) {
        super(key, message, detailKeys, detailValues, t);
    }

}
