package org.respondeco.respondeco.service.util;

import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.service.exception.IllegalValueException;
import org.respondeco.respondeco.service.exception.ServiceException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Created by Clemens Puehringer on 18/08/15.
 */
public class Assert {

    public static void isNull(Object object, String key, String message,
                              List<String> detailKeys, List<Serializable> detailValues) {
        if(object != null) {
            throw new IllegalValueException(key, message, detailKeys, detailValues);
        }
    }

    public static void notNull(Object object, String key, String message) {
        notNull(object, key, message, null, null);
    }

    public static void notNull(Object object, String key, String message,
                               List<String> detailKeys, List<Serializable> detailValues) {
        if(object == null) {
            throw new IllegalValueException(key, message, detailKeys, detailValues);
        }
    }

    public static void isValid(String string, String key, String message,
                               List<String> detailKeys, List<Serializable> detailValues) {
        if(string == null || string.length() == 0) {
            throw new IllegalValueException(key, message, detailKeys, detailValues);
        }
    }

    public static void isValid(String string, Integer min, Integer max,
                               String key, String message, List<String> detailKeys, List<Serializable> detailValues) {
        if(string == null || string.length() == 0 || string.length() < min || string.length() > max) {
            throw new IllegalValueException(key, message, detailKeys, detailValues);
        }
    }

    public static void isFalse(Boolean bool, String key, String message,
                               List<String> detailKeys, List<Serializable> detailValues) {
        if(bool) {
            throw new IllegalValueException(key, message, detailKeys, detailValues);
        }
    }

    public static void isEqual(Object o1, Object o2,
                               String key, String message, List<String> detailKeys, List<Serializable> detailValues) {
        if(o1 == null) {
            Assert.isNull(o2, key, message, detailKeys, detailValues);
        }
        if(!o1.equals(o2)) {
            throw new IllegalValueException(key, message, detailKeys, detailValues);
        }
    }

    public static void isEmpty(Collection<?> collection,
                               String key, String message, List<String> detailKeys, List<Serializable> detailValues) {
        if(collection.size() > 0) {
            throw new IllegalValueException(key, message, detailKeys, detailValues);
        }
    }

    public static void isEqualOrNull(BigDecimal targetValue, BigDecimal comparedValue,
                                     String key, String message,
                                     List<String> detailKeys, List<Serializable> detailValues) {
        if(comparedValue != null) {
            Assert.isEqual(targetValue.doubleValue(), comparedValue.doubleValue(),
                key, message, detailKeys, detailValues);
        }
    }

}
