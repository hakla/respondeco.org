package org.respondeco.respondeco.testutil;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;

import org.apache.commons.lang.SerializationUtils;
import org.respondeco.respondeco.domain.AbstractAuditingEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for testing REST controllers.
 */
public class TestUtil {

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
