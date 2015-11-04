package org.respondeco.respondeco.testutil;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.nio.charset.Charset;
import java.util.*;

import net.sf.cglib.proxy.Enhancer;
import org.apache.commons.lang.SerializationUtils;
import org.respondeco.respondeco.domain.AbstractAuditingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtil {

    private static final Logger log = LoggerFactory.getLogger(TestUtil.class);

    /** MediaType for JSON UTF8 */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static <T extends Serializable> T clone(T entity) {
        return (T) SerializationUtils.clone(entity);
    }

    public static <T extends AbstractAuditingEntity> T blank(Class<T> clazz) {
        try {
            T entity = clazz.newInstance();
            entity.setId(null);
            entity.setCreatedBy(null);
            entity.setCreatedDate(null);
            entity.setLastModifiedBy(null);
            entity.setLastModifiedDate(null);
            entity.setActive(null);
            return entity;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Convert an object to JSON byte array.
     *
     * @param object
     *            the object to convert
     * @return the JSON byte array
     * @throws IOException
     */
    public static byte[] convertObjectToJsonBytes(Object object)
            throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }

    public static void inject(Object target, String fieldName, Object value)
        throws Exception {
        log.debug("injecting {} into field {} of object {}", value, fieldName, target);
        target = unwrap(target);
        log.debug("final target object: {}", target);
        ReflectionTestUtils.setField(target, fieldName, value);
    }

    public static Object unwrap(Object object) throws Exception {
        if (AopUtils.isAopProxy(object) && object instanceof Advised) {
            log.debug("target is an AOP proxy, unwrapping...");
            return ((Advised) object).getTargetSource().getTarget();
        }
        return object;
    }

    public static <A extends Annotation> A getAnnotation(Class<?> type, Class<A> annotationType) {
        while(type != Object.class) {
            Annotation annotation = type.getAnnotation(annotationType);
            if(annotation != null) {
                return (A) annotation;
            }
            type = type.getSuperclass();
        }
        return null;
    }

    public static List<String> getAscendingStrings(int number) {
        return getAscendingStrings(number, "");
    }

    public static List<String> getAscendingStrings(int number, String prefix) {
        List<String> response = new ArrayList<>();
        Set<String> strings = new HashSet<>();
        while(strings.size() != number) {
            strings.add(getRandomString(20));
        }
        for(String s : strings) {
            response.add(prefix + s);
        }
        Collections.sort(response);
        return response;
    }

    public static String getRandomString(int places) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            char c = CHARACTERS.charAt(random.nextInt(CHARACTERS.length()));
            sb.append(c);
        }
        return sb.toString();
    }

}
