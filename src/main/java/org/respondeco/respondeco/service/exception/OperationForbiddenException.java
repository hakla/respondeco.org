package org.respondeco.respondeco.service.exception;

import java.io.Serializable;
import java.util.List;

/**
 * Created by clemens on 18/11/14.
 */
public class OperationForbiddenException extends ServiceException {

    public OperationForbiddenException(String key, String message) {
        super(key, message, null, null);
    }

    public OperationForbiddenException(String key, String message,
                                       List<String> detailKeys, List<Serializable> detailValues) {
        super(key, message, detailKeys, detailValues);
    }

    public OperationForbiddenException(String key, String message, List<String> detailKeys,
                                       List<Serializable> detailValues, Throwable t) {
        super(key, message, detailKeys, detailValues, t);
    }

}
