package org.respondeco.respondeco.service.exception;

/**
 * Created by Clemens Puehringer on 18/08/15.
 */
public class ExceptionUtil {

    public static String concatKey(String ... parts) {
        return String.join(".", parts);
    }

    public static class KeyBuilder {
        private final String prefix;

        public KeyBuilder(String prefix) {
            this.prefix = prefix;
        }

        public String from(String postfix) {
            return ExceptionUtil.concatKey(prefix, postfix);
        }
    }

}
