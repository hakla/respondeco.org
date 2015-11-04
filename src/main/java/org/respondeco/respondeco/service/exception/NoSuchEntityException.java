package org.respondeco.respondeco.service.exception;

import org.respondeco.respondeco.domain.AbstractAuditingEntity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by clemens on 02/11/14.
 */
public class NoSuchEntityException extends ServiceException {

    private static final String BASE_ERROR_COMPONENT    = "no_such_entity";
    private static final String BASE_MESSAGE            = "no such entity: %1 (%2)";

    public NoSuchEntityException(ErrorPrefix prefix, Long id, Class<?> clazz) {
        this(prefix, id.toString(), clazz);
    }

    public NoSuchEntityException(ErrorPrefix prefix, String id, Class<?> clazz) {
        super(prefix.join(BASE_ERROR_COMPONENT), BASE_MESSAGE,
            Arrays.asList("id", "class"),
            Arrays.asList(id, clazz.getSimpleName()));
    }

    public NoSuchEntityException(ErrorPrefix prefix, String message, List<String> detailKeys,
                                 List<Serializable> detailValues) {
        super(prefix.join(BASE_ERROR_COMPONENT), message, detailKeys, detailValues);
    }

    public NoSuchEntityException(ErrorPrefix prefix, String message, List<String> detailKeys,
                                 List<Serializable> detailValues, Throwable t) {
        super(prefix.join(BASE_ERROR_COMPONENT), message, detailKeys, detailValues, t);
    }

}
